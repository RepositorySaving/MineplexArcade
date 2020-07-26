package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseHorse;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkBoneRush;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkHorseKick;
import nautilus.game.arcade.kit.perks.PerkInfernalHorror;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class KitSkeletalHorse
  extends SmashKit
{
  public KitSkeletalHorse(ArcadeManager manager)
  {
    super(manager, "Skeletal Horse", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(6.0D, 1.4D, 0.35D, 6.0D), new PerkDoubleJump("Double Jump", 1.0D, 1.0D, false), new PerkHorseKick(), new PerkBoneRush(), new PerkInfernalHorror() }, EntityType.HORSE, new ItemStack(Material.MILK_BUCKET));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Bone Kick", 
      
      new String[] {
      ChatColor.RESET + "Stand on your hind legs and maul enemies", 
      ChatColor.RESET + "infront of you with your front legs, dealing", 
      ChatColor.RESET + "damage and large knockback." }) });
    


    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Bone Rush", 
      
      new String[] {
      ChatColor.RESET + "Charge forth in a deadly wave of bones.", 
      ChatColor.RESET + "Bones deal small damage and knockback.", 
      ChatColor.RESET, 
      ChatColor.RESET + "Holding Crouch will prevent you from", 
      ChatColor.RESET + "moving forward with the bones." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.FIRE, 0, 1, 
        C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Infernal Horror", 
        
        new String[] {
        ChatColor.RESET + "Charge your Rage by taking/dealing damage.", 
        ChatColor.RESET + "When your Rage hits 100%, you transform", 
        ChatColor.RESET + "into Infernal Horror.", 
        ChatColor.RESET, 
        ChatColor.RESET + "Infernal Horror has Speed 2, 1 Bonus Damage", 
        ChatColor.RESET + "and improved Bone Rush and Bone Kick." }) });
    }
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseHorse disguise = new DisguiseHorse(player);
    disguise.setType(Horse.Variant.SKELETON_HORSE);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
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
    
    if (type == EntityType.HORSE)
    {
      Horse horse = (Horse)entity;
      horse.setAdult();
      horse.setVariant(Horse.Variant.SKELETON_HORSE);
    }
    
    UtilEnt.Vegetate(entity);
    
    SpawnCustom(entity);
    
    return entity;
  }
}
