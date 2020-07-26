package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PerkCripple extends Perk
{
  private int _power;
  private double _time;
  
  public PerkCripple(int power, double time)
  {
    super("Knockback", new String[] {C.cGray + "Attacks give Slow " + power + " for " + time + " seconds." });
    

    this._power = power;
    this._time = time;
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this.Manager.IsAlive(damager)) {
      return;
    }
    event.SetKnockback(false);
    
    this.Manager.GetCondition().Factory().Slow("Cripple", event.GetDamageeEntity(), damager, this._time, this._power, false, false, false, false);
  }
}
