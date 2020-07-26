package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class PerkAxeman extends Perk
{
  public PerkAxeman()
  {
    super("Axe Master", new String[] {C.cGray + "Deals +1 Damage with Axes" });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void AxeDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (damager.getItemInHand() == null) {
      return;
    }
    if (!damager.getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    event.AddMod(damager.getName(), GetName(), 1.0D, false);
  }
}
