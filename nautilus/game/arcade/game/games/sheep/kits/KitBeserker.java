package nautilus.game.arcade.game.games.sheep.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkLeap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;









public class KitBeserker
  extends Kit
{
  public KitBeserker(ArcadeManager manager)
  {
    super(manager, "Beserker", KitAvailability.Free, new String[] {"Agile warrior trained in the ways axe combat." }, new Perk[] {new PerkLeap("Beserker Leap", 1.2D, 1.2D, 8000L) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_AXE));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.SADDLE) });
    
    ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
    LeatherArmorMeta metaHelm = (LeatherArmorMeta)helm.getItemMeta();
    metaHelm.setColor(this.Manager.GetGame().GetTeam(player).GetColorBase());
    helm.setItemMeta(metaHelm);
    player.getInventory().setHelmet(helm);
    
    ItemStack armor = new ItemStack(Material.LEATHER_CHESTPLATE);
    LeatherArmorMeta meta = (LeatherArmorMeta)armor.getItemMeta();
    meta.setColor(this.Manager.GetGame().GetTeam(player).GetColorBase());
    armor.setItemMeta(meta);
    player.getInventory().setChestplate(armor);
    
    ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
    LeatherArmorMeta metaLegs = (LeatherArmorMeta)armor.getItemMeta();
    metaLegs.setColor(this.Manager.GetGame().GetTeam(player).GetColorBase());
    legs.setItemMeta(metaLegs);
    player.getInventory().setLeggings(legs);
    
    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
    LeatherArmorMeta metaBoots = (LeatherArmorMeta)armor.getItemMeta();
    metaBoots.setColor(this.Manager.GetGame().GetTeam(player).GetColorBase());
    boots.setItemMeta(metaBoots);
    player.getInventory().setBoots(boots);
    
    player.getInventory().setItem(8, armor.clone());
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
  }
}
