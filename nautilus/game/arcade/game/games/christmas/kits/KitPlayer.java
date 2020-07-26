package nautilus.game.arcade.game.games.christmas.kits;

import mineplex.core.common.util.C;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitPlayer
  extends Kit
{
  public KitPlayer(ArcadeManager manager)
  {
    super(manager, "Santa's Helper", KitAvailability.Free, new String[] {"Help Santa retreive the lost presents!" }, new Perk[0], EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  



  public void GiveItems(Player player)
  {
    ItemStack item = ItemStackFactory.Instance.CreateStack(Material.DIAMOND_SWORD, (byte)0, 1, C.cGreen + C.Bold + "Santas Sword");
    player.getInventory().setItem(0, item);
    

    item = ItemStackFactory.Instance.CreateStack(Material.BOW, (byte)0, 1, C.cGreen + C.Bold + "Christmas Bow");
    item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
    player.getInventory().setItem(1, item);
    player.getInventory().setItem(28, ItemStackFactory.Instance.CreateStack(Material.ARROW));
    

    item = ItemStackFactory.Instance.CreateStack(Material.DIAMOND_PICKAXE, (byte)0, 1, C.cGreen + C.Bold + "Elf Pickaxe");
    player.getInventory().setItem(2, item);
    

    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    
    this.Manager.GetCondition().Factory().Regen("Perm", player, player, 3600000.0D, 0, false, false, true);
  }
}
