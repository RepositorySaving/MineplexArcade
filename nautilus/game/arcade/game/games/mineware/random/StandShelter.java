package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class StandShelter extends Order
{
  public StandShelter(MineWare host)
  {
    super(host, "take shelter from rain");
  }
  



  public void Initialize() {}
  


  public void Uninitialize()
  {
    this.Host.WorldData.World.setStorm(false);
  }
  


  public void FailItems(Player player) {}
  


  @org.bukkit.event.EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    this.Host.WorldData.World.setStorm(true);
    
    for (Player player : this.Host.GetPlayers(true))
    {
      Block block = player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock();
      
      while ((block.getTypeId() == 0) && (block.getLocation().getY() < 255.0D))
      {
        block = block.getRelative(org.bukkit.block.BlockFace.UP);
        
        if (block.getTypeId() != 0)
        {
          SetCompleted(player);
          break;
        }
      }
    }
  }
}
