package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;


public class PerkMammoth
  extends Perk
{
  public PerkMammoth()
  {
    super("Mammoth", new String[] {C.cGray + "Take 50% knockback and deal 150% knockback" });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void KnockbackIncrease(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    event.AddKnockback(GetName(), 1.5D);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void KnockbackDecrease(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    event.AddKnockback(GetName(), 0.5D);
  }
}
