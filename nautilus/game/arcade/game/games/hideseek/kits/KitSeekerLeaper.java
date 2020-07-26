package nautilus.game.arcade.game.games.hideseek.kits;

import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkLeap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitSeekerLeaper
  extends Kit
{
  public KitSeekerLeaper(ArcadeManager manager)
  {
    super(manager, "Leaper Hunter", KitAvailability.Free, new String[] {"Leap after those pretty blocks!" }, new Perk[] {new PerkLeap("Leap", 1.1D, 1.0D, 8000L) }, EntityType.ZOMBIE, new ItemStack(Material.COMPASS));
  }
  



  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    

    ItemStack bow = ItemStackFactory.Instance.CreateStack(Material.BOW);
    bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
    player.getInventory().setItem(1, bow);
    player.getInventory().setItem(28, ItemStackFactory.Instance.CreateStack(Material.ARROW));
    
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
  
  @EventHandler(priority=EventPriority.LOW)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (HasKit(damagee)) {
      event.SetCancelled("TNT Resistant");
    }
  }
}
