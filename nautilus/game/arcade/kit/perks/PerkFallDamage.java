package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class PerkFallDamage
  extends Perk
{
  private int _mod;
  
  public PerkFallDamage(int mod)
  {
    super("Feather Falling", new String[] {C.cGray + "You take " + mod + " damage from falling" });
    

    this._mod = mod;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageDecrease(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    int decrease = 0;
    if (this._mod < 0)
    {
      decrease = (int)-Math.min(Math.abs(this._mod), event.GetDamageInitial());
    }
    
    event.AddMod(damagee.getName(), GetName(), decrease, false);
  }
}
