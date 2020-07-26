package nautilus.game.arcade.game.games.castlesiege.kits;

import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkCleave;
import nautilus.game.arcade.kit.perks.PerkSeismicSlamCS;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitHumanBrawler
  extends Kit
{
  public KitHumanBrawler(ArcadeManager manager)
  {
    super(manager, "Castle Brawler", KitAvailability.Blue, new String[] {"Extremely tanky, can smash the undead around." }, new Perk[] {new PerkSeismicSlamCS(), new PerkCleave(0.75D) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_AXE));
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
  
  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.ARROW, 16) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
  }
}
