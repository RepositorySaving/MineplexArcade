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
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkLeap;
import nautilus.game.arcade.kit.perks.PerkPowershot;
import nautilus.game.arcade.kit.perks.PerkRegeneration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitHumanAssassin
  extends Kit
{
  public KitHumanAssassin(ArcadeManager manager)
  {
    super(manager, "Castle Assassin", KitAvailability.Blue, new String[] {"Able to kill with a single shot!" }, new Perk[] {new PerkFletcher(2, 4, false), new PerkLeap("Leap", 1.2D, 1.0D, 8000L), new PerkPowershot(5, 400L), new PerkRegeneration(0) }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
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
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.DIAMOND_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.LEATHER_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.LEATHER_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.LEATHER_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.LEATHER_BOOTS));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
  }
}
