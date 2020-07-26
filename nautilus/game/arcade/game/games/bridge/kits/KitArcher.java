package nautilus.game.arcade.game.games.bridge.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBarrage;
import nautilus.game.arcade.kit.perks.PerkFletcher;
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
    super(manager, "Archer", KitAvailability.Green, new String[] {"Highly trained with a bow, probably an elf or something..." }, new Perk[] {new PerkFletcher(20, 3, true), new PerkBarrage(5, 250L, true, false) }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
  }
}
