package nautilus.game.arcade.game.games.bridge.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkIronSkin;
import nautilus.game.arcade.kit.perks.PerkMammoth;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitMammoth
  extends Kit
{
  public KitMammoth(ArcadeManager manager)
  {
    super(manager, "Brawler", KitAvailability.Green, new String[] {"Giant and muscular, easily smacks others around." }, new Perk[] {new PerkMammoth(), new PerkIronSkin(1.0D) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD) });
  }
}
