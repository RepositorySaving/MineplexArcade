package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkAxeThrower;
import nautilus.game.arcade.kit.perks.PerkAxeman;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;














public class KitAxeman
  extends Kit
{
  public KitAxeman(ArcadeManager manager)
  {
    super(manager, "Axeman", KitAvailability.Free, new String[] {"Proficient in the art of axe combat!" }, new Perk[] {new PerkAxeman(), new PerkAxeThrower(manager) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_AXE));
  }
  
  public void GiveItems(Player player) {}
}
