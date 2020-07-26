package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkWitchBroom extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  


  public PerkWitchBroom()
  {
    super("Broomstick", new String[] {C.cYellow + "Right-Click" + C.cGray + " to use " + C.cGreen + "Broomstick" });
  }
  


  @EventHandler
  public void Activate(PlayerInteractEvent event)
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    


    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
}
