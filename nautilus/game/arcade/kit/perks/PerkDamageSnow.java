package nautilus.game.arcade.kit.perks;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkDamageSnow extends Perk
{
  private int _damage;
  private double _knockback;
  
  public PerkDamageSnow(int damage, double knockback)
  {
    super("Snow Attack", new String[] {mineplex.core.common.util.C.cGray + "+" + damage + " Damage and +" + (int)((knockback - 1.0D) * 100.0D) + "% Knockback to enemies on snow." });
    

    this._damage = damage;
    this._knockback = knockback;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity().getLocation().getBlock().getTypeId() != 78) {
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
    event.AddMod(damager.getName(), GetName(), this._damage, false);
    event.AddKnockback("Knockback Snow", this._knockback);
  }
}
