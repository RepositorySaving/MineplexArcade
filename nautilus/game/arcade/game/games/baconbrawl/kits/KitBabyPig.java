package nautilus.game.arcade.game.games.baconbrawl.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguisePig;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBaconBlast;
import nautilus.game.arcade.kit.perks.PerkSpeed;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitBabyPig
  extends Kit
{
  public KitBabyPig(ArcadeManager manager)
  {
    super(manager, "Bebe Piggles", KitAvailability.Green, new String[] {"Tiny pig runs so fast!" }, new Perk[] {new PerkBaconBlast(), new PerkSpeed(1) }, EntityType.PIG, new ItemStack(Material.PORK));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    

    DisguisePig disguise = new DisguisePig(player);
    disguise.SetName(C.cYellow + player.getName());
    disguise.SetCustomNameVisible(true);
    disguise.setBaby();
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
    
    if (type == EntityType.PIG)
    {
      Pig sheep = (Pig)entity;
      sheep.setBaby();
    }
    
    UtilEnt.Vegetate(entity);
    
    SpawnCustom(entity);
    
    return entity;
  }
}
