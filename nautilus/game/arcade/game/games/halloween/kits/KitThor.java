package nautilus.game.arcade.game.games.halloween.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkHammerThrow;
import nautilus.game.arcade.kit.perks.PerkKnockbackAttack;
import nautilus.game.arcade.kit.perks.PerkSeismicHammer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitThor
  extends SmashKit
{
  public KitThor(ArcadeManager manager)
  {
    super(manager, "Thor", KitAvailability.Free, new String[] {"Smash and kill with your Thor Hammer!", "", "Nearby allies receive " + C.cGreen + "Strength 1" }, new Perk[] {new PerkKnockbackAttack(2.0D), new PerkFletcher(1, 4, true), new PerkSeismicHammer(), new PerkHammerThrow() }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, "Seismic Hammer") });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.DIAMOND_AXE, 0, 1, "Thor Hammer") });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW, 0, 1, "Bow") });
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.JACK_O_LANTERN));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
    ent.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
  }
  
  @EventHandler
  public void FireItemResist(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (HasKit(player))
      {

        this.Manager.GetCondition().Factory().FireItemImmunity(GetName(), player, player, 1.9D, false);
      }
    }
  }
  
  @EventHandler
  public void DamageBoost(CustomDamageEvent event) {
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (HasKit(damagee)) {
      event.AddMod("Thor Boost", "Thor Boost", 4.0D, false);
    }
  }
  
  @EventHandler
  public void Aura(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (HasKit(player))
      {

        for (Player other : this.Manager.GetGame().GetPlayers(true))
        {
          if (!other.equals(player))
          {

            if (UtilMath.offset(player, other) <= 8.0D)
            {

              this.Manager.GetCondition().Factory().Strength("Aura", other, player, 1.9D, 0, false, false, false);
            }
          }
        }
      }
    }
  }
}
