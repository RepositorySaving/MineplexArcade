package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class PerkBackstab extends Perk
{
  public PerkBackstab()
  {
    super("Backstab", new String[] {C.cGray + "Deal +2 damage from behind opponents." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    if (event.GetDamageInitial() <= 1.0D) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    Vector look = damagee.getLocation().getDirection();
    look.setY(0);
    look.normalize();
    
    Vector from = damager.getLocation().toVector().subtract(damagee.getLocation().toVector());
    from.setY(0);
    from.normalize();
    
    Vector check = new Vector(look.getX() * -1.0D, 0.0D, look.getZ() * -1.0D);
    if (check.subtract(from).length() < 0.8D)
    {

      event.AddMod(damager.getName(), GetName(), 2.0D, true);
      

      damagee.getWorld().playSound(damagee.getLocation(), Sound.HURT_FLESH, 1.0F, 2.0F);
      return;
    }
  }
}
