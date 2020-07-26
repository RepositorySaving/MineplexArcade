package nautilus.game.arcade.game.games.bridge.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkAxeman;
import nautilus.game.arcade.kit.perks.PerkLeap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitBeserker
  extends Kit
{
  public KitBeserker(ArcadeManager manager)
  {
    super(manager, "Beserker", KitAvailability.Free, new String[] {"Agile warrior trained in the ways axe combat." }, new Perk[] {new PerkLeap("Beserker Leap", 1.2D, 1.2D, 8000L), new PerkAxeman() }, EntityType.ZOMBIE, new ItemStack(Material.STONE_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
  }
}
