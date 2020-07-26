package nautilus.game.arcade.game.games.barbarians.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBodySlam;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitBrute
  extends Kit
{
  public KitBrute(ArcadeManager manager)
  {
    super(manager, "Barbarian Brute", KitAvailability.Free, new String[] {"A true barbarian, loves to kill people!" }, new Perk[] {new PerkBodySlam(8, 2.0D) }, EntityType.PLAYER, new ItemStack(Material.IRON_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.IRON_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.IRON_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.IRON_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.IRON_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
  }
}
