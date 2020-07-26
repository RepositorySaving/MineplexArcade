package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkHiltSmash;
import nautilus.game.arcade.kit.perks.PerkIronSkin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitKnight
  extends Kit
{
  public KitKnight(ArcadeManager manager)
  {
    super(manager, "Knight", KitAvailability.Free, new String[0], new Perk[] {new PerkIronSkin(0.5D), new PerkHiltSmash() }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  
  public void GiveItems(Player player) {}
}
