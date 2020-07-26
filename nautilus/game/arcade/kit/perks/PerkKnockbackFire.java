package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkKnockbackFire extends Perk
{
  private double _power;
  
  public PerkKnockbackFire(double power)
  {
    super("Flaming Knockback", new String[] {C.cGray + "You deal " + (int)(power * 100.0D) + "% Knockback to burning enemies." });
    

    this._power = power;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity().getFireTicks() <= 0) {
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
    event.AddKnockback("Knockback Fire", this._power);
  }
}
