package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;











public class KitComingSoon
  extends SmashKit
{
  public KitComingSoon(ArcadeManager manager)
  {
    super(manager, C.cRed + "Coming Soon", KitAvailability.Blue, new String[0], new Perk[0], EntityType.VILLAGER, new ItemStack(Material.IRON_SWORD));
  }
  
  public void GiveItems(Player player) {}
}
