package nautilus.game.arcade.game.games.halloween.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkBarrage;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkQuickshotRobinHood;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitRobinHood
  extends SmashKit
{
  public KitRobinHood(ArcadeManager manager)
  {
    super(manager, "Robin Hood", KitAvailability.Free, new String[] {"Trick or treating from the rich...", "", "Nearby allies receive " + C.cGreen + "Regeneration 1" }, new Perk[] {new PerkFletcher(1, 8, true), new PerkBarrage(8, 125L, true, true), new PerkQuickshotRobinHood() }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, "Sword") });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW, 0, 1, "Bow") });
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    

    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.JACK_O_LANTERN));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
    ent.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
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
  public void Aura(UpdateEvent event) {
    if (event.getType() == UpdateType.FAST)
    {
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

                this.Manager.GetCondition().Factory().Regen("Aura", other, player, 1.9D, 0, false, false, false); } }
          }
        }
      }
    }
    if (event.getType() == UpdateType.SLOW)
    {
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

                UtilPlayer.health(other, 1.0D);
              }
            }
          }
        }
      }
    }
  }
}
