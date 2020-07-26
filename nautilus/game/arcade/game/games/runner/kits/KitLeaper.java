package nautilus.game.arcade.game.games.runner.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkLeap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitLeaper
  extends Kit
{
  public KitLeaper(ArcadeManager manager)
  {
    super(manager, "Jumper", KitAvailability.Free, new String[] {"Leap to avoid falling to your death!" }, new Perk[] {new PerkLeap("Leap", 1.2D, 1.2D, 8000L) }, EntityType.SKELETON, new ItemStack(Material.STONE_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
  }
}
