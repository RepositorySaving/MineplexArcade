package nautilus.game.arcade.game.games.castlesiege.kits;

import mineplex.core.common.util.F;
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
import nautilus.game.arcade.kit.perks.PerkConstructor;
import nautilus.game.arcade.kit.perks.PerkMammoth;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class KitHumanKnight
  extends Kit
{
  public KitHumanKnight(ArcadeManager manager)
  {
    super(manager, "Castle Knight", KitAvailability.Green, new String[] {"Robust knight, also able to construct defenses." }, new Perk[] {new PerkMammoth(), new PerkConstructor("Constructor", 40.0D, 2, Material.FENCE, "Castle Barricade", true) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
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
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.ARROW, 64) });
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.FENCE, 0, 2, F.item("Castle Barricade")) });
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP) });
    
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
