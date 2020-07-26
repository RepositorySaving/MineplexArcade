package nautilus.game.arcade.game.games.dragonescape.kits;

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













public class KitWarper
  extends Kit
{
  public KitWarper(ArcadeManager manager)
  {
    super(manager, "Warper", KitAvailability.Blue, new String[] {"Use your Enderpearl to instantly warp", "to the player in front of you!" }, new Perk[] {new PerkLeap("Leap", 1.0D, 1.0D, 8000L, 2) }, EntityType.ZOMBIE, new ItemStack(Material.ENDER_PEARL));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.ENDER_PEARL) });
    player.setExp(0.99F);
  }
}
