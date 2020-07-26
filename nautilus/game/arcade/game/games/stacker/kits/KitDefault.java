package nautilus.game.arcade.game.games.stacker.kits;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;














public class KitDefault
  extends Kit
{
  public KitDefault(ArcadeManager manager)
  {
    super(manager, "Default", KitAvailability.Free, new String[0], new Perk[0], EntityType.ZOMBIE, new ItemStack(Material.WOOD_BUTTON));
  }
  
  public void GiveItems(Player player) {}
}
