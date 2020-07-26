package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkMammoth;
import nautilus.game.arcade.kit.perks.PerkSeismicSlamHG;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;














public class KitBrawler
  extends Kit
{
  public KitBrawler(ArcadeManager manager)
  {
    super(manager, "Brawler", KitAvailability.Green, new String[] {"Giant and muscular, easily smacks others around." }, new Perk[] {new PerkMammoth(), new PerkSeismicSlamHG() }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  
  public void GiveItems(Player player) {}
}
