package nautilus.game.arcade.game.games.smash.kits;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkKnockbackArrow;
import nautilus.game.arcade.kit.perks.PerkNotFinished;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KitKnight extends SmashKit
{
  private HashMap<Player, Horse> _mounts = new HashMap();
  private HashSet<Horse> _horses = new HashSet();
  
  private HashSet<CustomDamageEvent> _calledEvents = new HashSet();
  
















  public KitKnight(ArcadeManager manager)
  {
    super(manager, "Undead Knight", KitAvailability.Blue, new String[0], new Perk[] {new nautilus.game.arcade.kit.perks.PerkSmashStats(6.0D, 1.2D, 0.25D, 7.5D), new PerkFletcher(1, 2, false), new PerkKnockbackArrow(2.0D), new nautilus.game.arcade.kit.perks.PerkDoubleJumpHorse(), new nautilus.game.arcade.kit.perks.PerkHorseKick(), new PerkNotFinished() }, EntityType.HORSE, new ItemStack(Material.IRON_BARDING));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Horse Kick", 
      
      new String[0]) });
    


    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Coming Soon...", 
      
      new String[0]) });
    


    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.IRON_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.IRON_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.IRON_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.IRON_BOOTS));
    

    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
    

    this.Manager.GetGame().CreatureAllowOverride = true;
    Horse horse = (Horse)player.getWorld().spawn(player.getLocation(), Horse.class);
    this.Manager.GetGame().CreatureAllowOverride = false;
    

    horse.setTamed(true);
    horse.setOwner(player);
    horse.setMaxDomestication(1);
    

    horse.setColor(Horse.Color.DARK_BROWN);
    horse.setStyle(Horse.Style.WHITE_DOTS);
    horse.setVariant(Horse.Variant.UNDEAD_HORSE);
    horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
    horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
    

    horse.setAdult();
    horse.setJumpStrength(1.0D);
    horse.setMaxHealth(100.0D);
    horse.setHealth(horse.getMaxHealth());
    
    this._horses.add(horse);
    this._mounts.put(player, horse);
  }
  












  @EventHandler
  public void HorseUpdate(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    Iterator<Horse> horseIterator = this._horses.iterator();
    
    while (horseIterator.hasNext())
    {
      Horse horse = (Horse)horseIterator.next();
      
      if ((!horse.isValid()) || (!this._mounts.containsValue(horse)))
      {

        horseIterator.remove();
        horse.remove();
      }
    }
  }
  
  @EventHandler
  public void HorseUpdate(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof Horse)) {
      return;
    }
    Player player = event.getPlayer();
    Horse horse = (Horse)event.getRightClicked();
    
    if ((this._mounts.containsKey(player)) && (((Horse)this._mounts.get(player)).equals(horse))) {
      return;
    }
    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "This is not your " + F.elem("Skeletal Horse") + "!"));
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerDamage(CustomDamageEvent event)
  {
    if (this._calledEvents.contains(event)) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (damagee.getVehicle() == null) {
      return;
    }
    if (!(damagee.getVehicle() instanceof Horse)) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.SUFFOCATION)
    {
      event.SetCancelled("Horse Suffocation");
      return;
    }
    
    Horse horse = (Horse)damagee.getVehicle();
    

    CustomDamageEvent newEvent = new CustomDamageEvent(horse, event.GetDamagerEntity(true), event.GetProjectile(), 
      event.GetCause(), event.GetDamageInitial(), true, false, false, 
      UtilEnt.getName(event.GetDamagerPlayer(true)), event.GetReason(), false);
    
    this._calledEvents.add(newEvent);
    this.Manager.GetPlugin().getServer().getPluginManager().callEvent(newEvent);
    this._calledEvents.remove(newEvent);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void HorseDamage(CustomDamageEvent event)
  {
    if (this._calledEvents.contains(event)) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.THORNS) {
      return;
    }
    if (!(event.GetDamageeEntity() instanceof Horse)) {
      return;
    }
    Horse horse = (Horse)event.GetDamageeEntity();
    
    if (horse.getPassenger() == null) {
      return;
    }
    if (!(horse.getPassenger() instanceof Player)) {
      return;
    }
    Player player = (Player)horse.getPassenger();
    

    CustomDamageEvent newEvent = new CustomDamageEvent(player, event.GetDamagerEntity(true), event.GetProjectile(), 
      event.GetCause(), event.GetDamageInitial(), true, false, false, 
      UtilEnt.getName(event.GetDamagerPlayer(true)), event.GetReason(), false);
    
    this._calledEvents.add(newEvent);
    this.Manager.GetPlugin().getServer().getPluginManager().callEvent(newEvent);
    this._calledEvents.remove(newEvent);
    
    event.AddKnockback("Knockback Multiplier", 1.2D);
  }
  

  public org.bukkit.entity.Entity SpawnEntity(Location loc)
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
      horse.setColor(Horse.Color.DARK_BROWN);
      horse.setStyle(Horse.Style.WHITE_DOTS);
      horse.setVariant(Horse.Variant.UNDEAD_HORSE);
      horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
    }
    
    UtilEnt.Vegetate(entity);
    
    SpawnCustom(entity);
    
    return entity;
  }
}
