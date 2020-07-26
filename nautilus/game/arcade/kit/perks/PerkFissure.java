package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.FissureData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkFissure extends Perk
{
  private HashSet<FissureData> _active = new HashSet();
  


  public PerkFissure()
  {
    super("Fissure", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Spade to " + C.cGreen + "Fissure" });
  }
  

  @EventHandler
  public void Leap(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SPADE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!mineplex.core.common.util.UtilEnt.isGrounded(player))
    {
      UtilPlayer.message(player, F.main("Game", "You cannot use " + F.skill(GetName()) + " while airborne."));
      return;
    }
    
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    FissureData data = new FissureData(this, player, player.getLocation().getDirection(), player.getLocation().add(0.0D, -0.5D, 0.0D));
    this._active.add(data);
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<FissureData> remove = new HashSet();
    
    for (FissureData data : this._active) {
      if (data.Update())
        remove.add(data);
    }
    for (FissureData data : remove)
    {
      this._active.remove(data);
      data.Clear();
    }
  }
}
