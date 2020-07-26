package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PerkDoubleJump extends Perk
{
  private double _power;
  private double _heightMax;
  private boolean _control;
  
  public PerkDoubleJump(String name, double power, double heightLimit, boolean control)
  {
    super("Jumper", new String[] {C.cYellow + "Tap Jump Twice" + C.cGray + " to " + C.cGreen + name });
    

    this._power = power;
    this._heightMax = heightLimit;
    this._control = control;
  }
  
  @EventHandler
  public void FlightHop(PlayerToggleFlightEvent event)
  {
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(true);
    player.setFlying(false);
    

    player.setAllowFlight(false);
    

    if (this._control)
    {
      UtilAction.velocity(player, this._power, 0.2D, this._heightMax, true);
    }
    else
    {
      UtilAction.velocity(player, player.getLocation().getDirection(), this._power, true, this._power, 0.0D, this._heightMax, true);
    }
    

    player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
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

          if ((UtilEnt.isGrounded(player)) || (UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN)))) {
            player.setAllowFlight(true);
          }
        }
      }
    }
  }
}
