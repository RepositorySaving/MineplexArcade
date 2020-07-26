package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkKnockbackGive
  extends Perk
{
  private double _power;
  
  public PerkKnockbackGive(double power)
  {
    super("Knockback", new String[] {C.cGray + "You deal " + (int)(power * 100.0D) + "% Knockback." });
    

    this._power = power;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this.Manager.IsAlive(damager)) {
      return;
    }
    event.AddKnockback("Knockback Multiplier", this._power);
  }
}
