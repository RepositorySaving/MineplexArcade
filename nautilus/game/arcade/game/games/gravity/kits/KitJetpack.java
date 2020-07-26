package nautilus.game.arcade.game.games.gravity.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitJetpack
  extends Kit
{
  public KitJetpack(ArcadeManager manager)
  {
    super(manager, "Astronaut", KitAvailability.Free, new String[] {"SPAAAAAAAAAAAAAACE" }, new Perk[0], EntityType.ZOMBIE, new ItemStack(Material.IRON_AXE));
  }
  



  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, "Space Suit") });
    



    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.GLASS));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.GOLD_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.GOLD_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.GOLD_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.GLASS));
    ent.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
  }
}
