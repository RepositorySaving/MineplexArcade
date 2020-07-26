package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkKnockback extends Perk
{
  private double _power;
  
  public PerkKnockback(double power)
  {
    super("Knockback", new String[] {C.cGray + "Attacks gives knockback with " + power + " power." });
    

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
    event.SetKnockback(false);
    
    if (!Recharge.Instance.use(damager, "KB " + UtilEnt.getName(event.GetDamageeEntity()), 400L, false, false)) {
      return;
    }
    event.GetDamageeEntity().playEffect(EntityEffect.HURT);
    
    UtilAction.velocity(event.GetDamageeEntity(), 
      UtilAlg.getTrajectory(damager, event.GetDamageeEntity()), 
      this._power, false, 0.0D, 0.1D, 10.0D, true);
  }
}
