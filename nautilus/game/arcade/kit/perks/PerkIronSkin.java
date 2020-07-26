package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class PerkIronSkin
  extends Perk
{
  private double _reduction;
  
  public PerkIronSkin(double d)
  {
    super("Iron Skin", new String[] {C.cGray + "You take " + d + " less damage from attacks" });
    

    this._reduction = d;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageDecrease(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
      return;
    }
    if (event.GetDamage() <= 1.0D) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    event.AddMod(damagee.getName(), GetName(), -this._reduction, false);
  }
}
