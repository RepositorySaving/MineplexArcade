package nautilus.game.arcade.game.games.paintball.kits;

import mineplex.core.common.util.C;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkPaintballShotgun;
import nautilus.game.arcade.kit.perks.PerkSpeed;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;













public class KitShotgun
  extends Kit
{
  public KitShotgun(ArcadeManager manager)
  {
    super(manager, "Shotgun", KitAvailability.Green, new String[] {"Pump action paintball shotgun.", C.cGold + "8 Pellets, 4 Pellet Hits Kill" }, new Perk[] {new PerkPaintballShotgun(), new PerkSpeed(1) }, EntityType.ZOMBIE, new ItemStack(Material.GOLD_BARDING));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.GOLD_BARDING, 0, 1, "Paintball Shotgun") });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.POTION, 0, 3, "Water Bomb") });
    
    ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
    LeatherArmorMeta metaHelm = (LeatherArmorMeta)helm.getItemMeta();
    metaHelm.setColor(Color.WHITE);
    helm.setItemMeta(metaHelm);
    player.getInventory().setHelmet(helm);
    
    ItemStack armor = new ItemStack(Material.LEATHER_CHESTPLATE);
    LeatherArmorMeta meta = (LeatherArmorMeta)armor.getItemMeta();
    meta.setColor(Color.WHITE);
    armor.setItemMeta(meta);
    player.getInventory().setChestplate(armor);
    
    ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
    LeatherArmorMeta metaLegs = (LeatherArmorMeta)armor.getItemMeta();
    metaLegs.setColor(Color.WHITE);
    legs.setItemMeta(metaLegs);
    player.getInventory().setLeggings(legs);
    
    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
    LeatherArmorMeta metaBoots = (LeatherArmorMeta)armor.getItemMeta();
    metaBoots.setColor(Color.WHITE);
    boots.setItemMeta(metaBoots);
    player.getInventory().setBoots(boots);
  }
}
