package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class RideBoat
  extends Order
{
  public RideBoat(MineWare host)
  {
    super(host, "Sit in a Boat");
  }
  



  public void Initialize() {}
  



  public void Uninitialize() {}
  



  public void FailItems(Player player) {}
  



  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : this.Host.GetPlayers(true)) {
      if ((player.isInsideVehicle()) && 
        ((player.getVehicle() instanceof Boat))) {
        SetCompleted(player);
      }
    }
  }
}
