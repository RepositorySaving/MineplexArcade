package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class StandAlone
  extends Order
{
  public StandAlone(MineWare host)
  {
    super(host, "Run away from everyone");
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
    for (Player player : this.Host.GetPlayers(true))
    {
      boolean alone = true;
      
      for (Player other : this.Host.GetPlayers(true))
      {
        if (!other.equals(player))
        {

          if (UtilMath.offset(player, other) < 16.0D)
          {
            alone = false;
            break;
          }
        }
      }
      if (alone) {
        SetCompleted(player);
      }
    }
  }
}
