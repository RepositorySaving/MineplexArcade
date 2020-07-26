package nautilus.game.arcade.game.games.horsecharge.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitDefenceArcher
  extends Kit
{
  public KitDefenceArcher(ArcadeManager manager)
  {
    super(manager, "Horseback Marksman", KitAvailability.Free, new String[] {"Able to shoot arrows that can stop horses" }, new Perk[] {new PerkFletcher(2, 4, true) }, EntityType.SKELETON, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.LEATHER_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.LEATHER_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.LEATHER_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.LEATHER_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
  }
}
