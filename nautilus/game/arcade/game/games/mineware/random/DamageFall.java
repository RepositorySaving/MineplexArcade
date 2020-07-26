package nautilus.game.arcade.game.games.mineware.random;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageFall
  extends Order
{
  public DamageFall(MineWare host)
  {
    super(host, "Take fall damage");
  }
  



  public void Initialize() {}
  



  public void Uninitialize() {}
  



  public void FailItems(Player player) {}
  



  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    Player player = event.GetDamageePlayer();
    if (player == null) { return;
    }
    SetCompleted(player);
  }
}
