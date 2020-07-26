package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PerkSquidSwim extends Perk
{
  private HashMap<Player, Long> _push = new HashMap();
  private HashMap<Player, Long> _active = new HashMap();
  



  public PerkSquidSwim()
  {
    super("Swimming", new String[] {C.cYellow + "Tap Crouch" + C.cGray + " to use " + C.cGreen + "Squid Thrust", C.cYellow + "Hold Crouch" + C.cGray + " to use " + C.cGreen + "Squid Swim" });
  }
  

  @EventHandler
  public void EnergyUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        player.setExp((float)Math.min(0.999D, player.getExp() + 0.007D));
      }
    }
  }
  
  @EventHandler
  public void Use(PlayerToggleSneakEvent event) {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    

    if (!player.isSneaking())
    {
      this._push.put(event.getPlayer(), Long.valueOf(System.currentTimeMillis()));
      return;
    }
    
    if ((!this._push.containsKey(player)) || (mineplex.core.common.util.UtilTime.elapsed(((Long)this._push.get(player)).longValue(), 500L))) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!player.getLocation().getBlock().isLiquid()) {
      return;
    }
    if (player.getExp() < 0.5D) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 500L, false, false)) {
      return;
    }
    player.setExp(player.getExp() - 0.5F);
    

    UtilAction.velocity(player, 0.9D, 0.2D, 2.0D, false);
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SPLASH, 0.5F, 0.75F);
    
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Reuse(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (player.getLocation().getBlock().isLiquid())
      {


        if (this._active.containsKey(player))
        {
          if (!mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(player)).longValue(), 200L))
          {
            UtilAction.velocity(player, 1.0D, 0.1D, 2.0D, false);

          }
          else
          {
            this._active.remove(player);
          }
          

        }
        else if (player.isSneaking())
        {
          UtilAction.velocity(player, 0.5D, 0.0D, 2.0D, false);
        }
      }
    }
  }
}
