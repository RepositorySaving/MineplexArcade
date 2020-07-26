package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilServer;
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
import org.bukkit.util.Vector;


public class PerkSpiderLeap
  extends Perk
{
  public PerkSpiderLeap()
  {
    super("Spider Leap", new String[] {C.cYellow + "Tap Jump Twice" + C.cGray + " to " + C.cGreen + "Spider Leap", C.cYellow + "Hold Crouch" + C.cGray + " to " + C.cGreen + "Wall Grab", C.cWhite + "Spider Leap and Wall Grab require Energy (Experience Bar)." });
  }
  


  @EventHandler
  public void WallClimb(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (player.getGameMode() != GameMode.CREATIVE)
      {

        if (this.Kit.HasKit(player))
        {

          if (!player.isSneaking())
          {
            player.setExp((float)Math.min(0.999D, player.getExp() + 0.01D));
          }
          else
          {
            player.setExp(Math.max(0.0F, player.getExp() - 0.01666667F));
            
            if (player.getExp() > 0.0F)
            {

              if (Recharge.Instance.usable(player, GetName()))
              {

                for (Block block : UtilBlock.getSurrounding(player.getLocation().getBlock(), true))
                {
                  if (!UtilBlock.airFoliage(block))
                  {
                    player.setVelocity(new Vector(0, 0, 0));
                    AllowFlight(player);
                  } } } }
          }
        }
      }
    }
  }
  
  @EventHandler
  public void FlightHop(PlayerToggleFlightEvent event) {
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
    

    UtilAction.velocity(player, 1.0D, 0.2D, 1.0D, true);
    


    player.getWorld().playSound(player.getLocation(), Sound.SPIDER_IDLE, 1.0F, 1.5F);
    
    Recharge.Instance.use(player, GetName(), 500L, false, false);
  }
  
  @EventHandler
  public void FlightUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (player.getGameMode() != GameMode.CREATIVE)
      {

        if (this.Kit.HasKit(player))
        {

          if ((UtilEnt.isGrounded(player)) || (UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN))))
            AllowFlight(player); }
      }
    }
  }
  
  public void AllowFlight(Player player) {
    if (player.getAllowFlight()) {
      return;
    }
    if (player.getExp() > 0.1666667F)
    {
      player.setExp(Math.max(0.0F, player.getExp() - 0.1666667F));
      player.setAllowFlight(true);
    }
  }
}
