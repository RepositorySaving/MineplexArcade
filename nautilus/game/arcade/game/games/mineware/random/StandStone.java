package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class StandStone extends Order
{
  public StandStone(MineWare host)
  {
    super(host, "Stand on stone");
  }
  



  public void Initialize() {}
  



  public void Uninitialize() {}
  



  public void FailItems(Player player) {}
  



  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Player player : this.Host.GetPlayers(true)) {
      if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.STONE) {
        SetCompleted(player);
      }
    }
  }
}
