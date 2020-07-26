package nautilus.game.arcade.game.games.baconbrawl.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSheep;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBackstabKnockback;
import nautilus.game.arcade.kit.perks.PerkPigCloak;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitSheepPig
  extends Kit
{
  public KitSheepPig(ArcadeManager manager)
  {
    super(manager, "'Pig'", KitAvailability.Blue, new String[] {"\"...Oink?\"" }, new Perk[] {new PerkPigCloak(), new PerkBackstabKnockback() }, EntityType.SHEEP, new ItemStack(Material.WOOL));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    

    DisguiseSheep disguise = new DisguiseSheep(player);
    disguise.SetName(C.cYellow + player.getName());
    disguise.SetCustomNameVisible(true);
    disguise.setColor(DyeColor.PINK);
    this.Manager.GetDisguise().disguise(disguise);
  }
  

  public Entity SpawnEntity(Location loc)
  {
    EntityType type = this._entityType;
    if (type == EntityType.PLAYER) {
      type = EntityType.ZOMBIE;
    }
    LivingEntity entity = (LivingEntity)this.Manager.GetCreature().SpawnEntity(loc, type);
    
    entity.setRemoveWhenFarAway(false);
    entity.setCustomName(GetAvailability().GetColor() + GetName() + " Kit" + (GetAvailability() == KitAvailability.Blue ? ChatColor.GRAY + " (" + ChatColor.WHITE + "Ultra" + ChatColor.GRAY + ")" : ""));
    entity.setCustomNameVisible(true);
    entity.getEquipment().setItemInHand(this._itemInHand);
    
    if (type == EntityType.SHEEP)
    {
      Sheep sheep = (Sheep)entity;
      sheep.setColor(DyeColor.PINK);
    }
    
    UtilEnt.Vegetate(entity);
    
    SpawnCustom(entity);
    
    return entity;
  }
}
