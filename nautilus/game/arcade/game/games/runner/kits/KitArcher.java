package nautilus.game.arcade.game.games.runner.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkQuickshot;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitArcher
  extends Kit
{
  public KitArcher(ArcadeManager manager)
  {
    super(manager, "Archer", KitAvailability.Green, new String[] {"Fire arrows to cause blocks to fall!" }, new Perk[] {new PerkQuickshot("Quickshot", 1.2D, 6000L) }, EntityType.SKELETON, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
  }
}
