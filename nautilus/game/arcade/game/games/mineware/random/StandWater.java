package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class StandWater extends Order
{
  public StandWater(MineWare host)
  {
    super(host, "Go for a swim");
  }
  



  public void Initialize() {}
  



  public void Uninitialize() {}
  



  public void FailItems(Player player) {}
  



  @org.bukkit.event.EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : this.Host.GetPlayers(true)) {
      if (player.getLocation().getBlock().isLiquid()) {
        SetCompleted(player);
      }
    }
  }
}
