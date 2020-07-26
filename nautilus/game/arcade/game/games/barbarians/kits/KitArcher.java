package nautilus.game.arcade.game.games.barbarians.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkRopedArrow;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitArcher
  extends Kit
{
  public KitArcher(ArcadeManager manager)
  {
    super(manager, "Barbarian Archer", KitAvailability.Green, new String[] {"Uses some kind of less barbaric ranged weapon..." }, new Perk[] {new PerkRopedArrow("Roped Arrow", 1.0D, 6000L), new PerkFletcher(2, 2, true) }, EntityType.PLAYER, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
  }
}
