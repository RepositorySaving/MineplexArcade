package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkKnockbackMultiplier
  extends Perk
{
  private double _power;
  
  public PerkKnockbackMultiplier(double power)
  {
    super("Knockback", new String[] {C.cGray + "You take " + (int)(power * 100.0D) + "% Knockback." });
    

    this._power = power;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    if (!this.Manager.IsAlive(damagee)) {
      return;
    }
    event.AddKnockback("Knockback Multiplier", this._power);
  }
}
