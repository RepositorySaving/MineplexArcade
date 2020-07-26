package nautilus.game.arcade.game.games.dragons.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkSparkler;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitPyrotechnic
  extends Kit
{
  public KitPyrotechnic(ArcadeManager manager)
  {
    super(manager, "Pyrotechnic", KitAvailability.Blue, new String[] {"Dragons love sparklers, following them!" }, new Perk[] {new PerkSparkler(20, 2) }, EntityType.ZOMBIE, new ItemStack(Material.EMERALD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.GOLD_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.GOLD_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.GOLD_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.GOLD_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
  }
}
