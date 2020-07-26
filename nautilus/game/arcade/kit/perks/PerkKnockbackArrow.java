package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkKnockbackArrow
  extends Perk
{
  private double _power;
  
  public PerkKnockbackArrow(double power)
  {
    super("Arrow Knockback", new String[] {C.cGray + "Arrows deal " + (int)(power * 100.0D) + "% Knockback." });
    

    this._power = power;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Arrow)) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this.Manager.IsAlive(damager)) {
      return;
    }
    event.AddKnockback("Knockback Arrow", this._power);
  }
}
