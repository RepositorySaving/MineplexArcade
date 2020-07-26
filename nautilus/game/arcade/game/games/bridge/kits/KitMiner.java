package nautilus.game.arcade.game.games.bridge.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkDigger;
import nautilus.game.arcade.kit.perks.PerkOreFinder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitMiner
  extends Kit
{
  public KitMiner(ArcadeManager manager)
  {
    super(manager, "Miner", KitAvailability.Blue, new String[] {"Master of ore prospecting and digging quickly." }, new Perk[] {new PerkOreFinder(), new PerkDigger() }, EntityType.ZOMBIE, new ItemStack(Material.STONE_PICKAXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_PICKAXE) });
  }
}
