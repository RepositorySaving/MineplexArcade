package nautilus.game.arcade.game.games.dragonriders.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkDragonRider;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitRider
  extends Kit
{
  public KitRider(ArcadeManager manager)
  {
    super(manager, "Dragon Rider", KitAvailability.Free, new String[] {"woosh" }, new Perk[] {new PerkDragonRider() }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.ARROW, 64) });
    
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
