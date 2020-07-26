package nautilus.game.arcade.game.games.hideseek.kits;

import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBomber;
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











public class KitSeekerTNT
  extends Kit
{
  public KitSeekerTNT(ArcadeManager manager)
  {
    super(manager, "TNT Hunter", KitAvailability.Green, new String[] {"Throw TNT to flush out the Hiders!" }, new Perk[] {new PerkBomber(15, 2, -1) }, EntityType.ZOMBIE, new ItemStack(Material.TNT));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD) });
    

    ItemStack bow = ItemStackFactory.Instance.CreateStack(Material.BOW);
    bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
    player.getInventory().setItem(1, bow);
    player.getInventory().setItem(28, ItemStackFactory.Instance.CreateStack(Material.ARROW));
    
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
