package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PerkShockingStrike extends Perk
{
  public PerkShockingStrike()
  {
    super("Shocking Strikes", new String[] {C.cGray + "Your attacks Shock/Blind/Slow opponents." });
  }
  

  @EventHandler(priority=EventPriority.MONITOR)
  public void Effect(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    org.bukkit.entity.Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this.Manager.IsAlive(damager)) {
      return;
    }
    this.Manager.GetCondition().Factory().Slow(GetName(), event.GetDamageeEntity(), damager, 2.0D, 1, false, false, false, false);
    this.Manager.GetCondition().Factory().Blind(GetName(), event.GetDamageeEntity(), damager, 1.0D, 0, false, false, false);
    this.Manager.GetCondition().Factory().Shock(GetName(), event.GetDamageeEntity(), damager, 1.0D, false, false);
  }
}
