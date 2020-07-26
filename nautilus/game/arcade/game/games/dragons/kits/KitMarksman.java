package nautilus.game.arcade.game.games.dragons.kits;

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









public class KitMarksman
  extends Kit
{
  public KitMarksman(ArcadeManager manager)
  {
    super(manager, "Marksman", KitAvailability.Green, new String[] {"Arrows send dragons running to the sky!" }, new Perk[] {new PerkFletcher(4, 4, true) }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    
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
