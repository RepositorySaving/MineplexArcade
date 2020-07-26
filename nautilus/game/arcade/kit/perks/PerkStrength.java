package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;


public class PerkStrength
  extends Perk
{
  private int _power;
  
  public PerkStrength(int power)
  {
    super("Strength", new String[] {C.cGray + "You deal " + power + " more damage" });
    

    this._power = power;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageDecrease(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    event.AddMod(damager.getName(), GetName(), this._power, false);
  }
}
