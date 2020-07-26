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

public class PerkDoubleJumpHorse extends Perk
{
  public PerkDoubleJumpHorse()
  {
    super("Jumper", new String[] {C.cYellow + "Tap Jump Twice" + C.cGray + " to " + C.cGreen + "Double Jump" });
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
    

    if (player.getVehicle() == null) {
      UtilAction.velocity(player, player.getLocation().getDirection(), 1.2D, true, 1.2D, 0.0D, 1.2D, true);
    } else {
      UtilAction.velocity(player.getVehicle(), player.getLocation().getDirection(), 0.8D, true, 0.8D, 0.0D, 0.8D, true);
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



          if (player.getVehicle() == null)
          {
            if ((UtilEnt.isGrounded(player)) || (UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN)))) {
              player.setAllowFlight(true);
            }
            
          }
          else if ((UtilEnt.isGrounded(player.getVehicle())) || (UtilBlock.solid(player.getVehicle().getLocation().getBlock().getRelative(BlockFace.DOWN)))) {
            player.setAllowFlight(true);
          }
        }
      }
    }
  }
}
