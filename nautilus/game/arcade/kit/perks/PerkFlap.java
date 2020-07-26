package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PerkFlap extends Perk
{
  private double _power;
  private boolean _control;
  
  public PerkFlap(double power, double heightLimit, boolean control)
  {
    super("Flap", new String[] {C.cYellow + "Tap Jump Twice" + C.cGray + " to " + C.cGreen + "Flap" });
    

    this._power = power;
    this._control = control;
  }
  
  @EventHandler
  public void FlightHop(PlayerToggleFlightEvent event)
  {
    Player player = event.getPlayer();
    
    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    player.setFlying(false);
    

    player.setAllowFlight(false);
    
    double power = 0.4D + 0.6D * (this._power * player.getExp());
    

    if (this._control)
    {
      UtilAction.velocity(player, power, 0.2D, 10.0D, true);
    }
    else
    {
      UtilAction.velocity(player, player.getLocation().getDirection(), power, true, power, 0.0D, 10.0D, true);
    }
    

    player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, (float)(0.3D + player.getExp()), (float)(Math.random() / 2.0D + 1.0D));
    

    Recharge.Instance.use(player, GetName(), 80L, false, false);
    

    player.setExp(Math.max(0.0F, player.getExp() - 0.1666667F));
  }
  
  @EventHandler
  public void FlightUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : mineplex.core.common.util.UtilServer.getPlayers())
    {
      if (player.getGameMode() != GameMode.CREATIVE)
      {

        if (this.Kit.HasKit(player))
        {


          if ((UtilEnt.isGrounded(player)) || (UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN))))
          {
            player.setExp(0.999F);
            player.setAllowFlight(true);
          }
          else if ((Recharge.Instance.usable(player, GetName())) && (player.getExp() > 0.0F))
          {
            player.setAllowFlight(true);
          }
        }
      }
    }
  }
}
