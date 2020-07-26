package nautilus.game.arcade.game.games.mineware.random;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageChicken
  extends Order
{
  public DamageChicken(MineWare host)
  {
    super(host, "punch a chicken");
  }
  



  public void Initialize() {}
  



  public void Uninitialize() {}
  



  public void FailItems(Player player) {}
  



  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player player = event.GetDamagerPlayer(false);
    if (player == null) { return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    if (!(ent instanceof Chicken)) {
      return;
    }
    SetCompleted(player);
  }
}
