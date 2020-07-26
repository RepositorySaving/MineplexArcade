package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilInv;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.games.dragonriders.DragonData;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkDragonRider extends Perk
{
  public HashMap<Player, DragonData> _dragons = new HashMap();
  


  public PerkDragonRider()
  {
    super("Dragon Rider", new String[] {C.cGray + "You ride a dragon!" });
  }
  

  @EventHandler
  public void DragonSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    if (this.Manager.GetGame().GetState() != Game.GameState.Live) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        if (!this._dragons.containsKey(player))
          this._dragons.put(player, new DragonData(this.Manager, player));
      }
    }
  }
  
  @EventHandler
  public void DragonLocation(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this.Manager.GetGame().GetState() != Game.GameState.Live) {
      return;
    }
    
    for (DragonData data : this._dragons.values()) {
      data.Move();
    }
  }
  
  @EventHandler
  public void DragonTargetCancel(EntityTargetEvent event) {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void ShootWeb(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("BOW")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 1000L, true, false)) {
      return;
    }
    event.setCancelled(true);
    
    UtilInv.Update(player);
    
    Fireball ball = (Fireball)((DragonData)this._dragons.get(player)).Dragon.launchProjectile(Fireball.class);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Dragon Blast") + "."));
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLAZE_BREATH, 2.0F, 1.0F);
  }
}
