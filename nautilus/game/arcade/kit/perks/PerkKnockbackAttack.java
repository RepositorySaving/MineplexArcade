package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class PerkKnockbackAttack
  extends Perk
{
  private double _power;
  
  public PerkKnockbackAttack(double power)
  {
    super("Melee Knockback", new String[] {C.cGray + "Melee attacks deal " + (int)(power * 100.0D) + "% Knockback." });
    

    this._power = power;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
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
    event.AddKnockback("Knockback Melee", this._power);
  }
}
