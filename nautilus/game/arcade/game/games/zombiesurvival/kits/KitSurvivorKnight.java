package nautilus.game.arcade.game.games.zombiesurvival.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkIronSkin;
import nautilus.game.arcade.kit.perks.PerkMammoth;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitSurvivorKnight
  extends Kit
{
  public KitSurvivorKnight(ArcadeManager manager)
  {
    super(manager, "Survivor Knight", KitAvailability.Free, new String[] {"Smash and kill through Zombies" }, new Perk[] {new PerkIronSkin(1.0D), new PerkMammoth() }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD) });
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
