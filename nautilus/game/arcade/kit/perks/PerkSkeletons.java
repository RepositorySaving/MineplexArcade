package nautilus.game.arcade.kit.perks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Perk;
import net.minecraft.server.v1_7_R3.EntitySkeleton;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftSkeleton;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkSkeletons extends Perk
{
  private HashMap<Player, ArrayList<Skeleton>> _minions = new HashMap();
  
  private boolean _name;
  private int _maxDist = 8;
  


  public PerkSkeletons(boolean name)
  {
    super("Skeleton Minons", new String[] {mineplex.core.common.util.C.cGray + "Killing an opponent summons a skeletal minion." });
    

    this._name = name;
  }
  
  @EventHandler
  public void MinionSpawn(CombatDeathEvent event)
  {
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killer = mineplex.core.common.util.UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (killer == null) {
      return;
    }
    if (!this.Kit.HasKit(killer)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    
    this.Manager.GetGame().CreatureAllowOverride = true;
    Skeleton skel = (Skeleton)killer.getWorld().spawn(killed.getLocation(), Skeleton.class);
    this.Manager.GetGame().CreatureAllowOverride = false;
    
    mineplex.core.common.util.UtilEnt.removeGoalSelectors(skel);
    
    skel.setMaxHealth(30.0D);
    skel.setHealth(skel.getMaxHealth());
    
    skel.getEquipment().setItemInHand(killed.getItemInHand());
    skel.getEquipment().setHelmet(killed.getInventory().getHelmet());
    skel.getEquipment().setChestplate(killed.getInventory().getChestplate());
    skel.getEquipment().setLeggings(killed.getInventory().getLeggings());
    skel.getEquipment().setBoots(killed.getInventory().getBoots());
    
    event.GetEvent().getDrops().remove(killed.getItemInHand());
    event.GetEvent().getDrops().remove(killed.getInventory().getHelmet());
    event.GetEvent().getDrops().remove(killed.getInventory().getChestplate());
    event.GetEvent().getDrops().remove(killed.getInventory().getLeggings());
    event.GetEvent().getDrops().remove(killed.getInventory().getBoots());
    
    skel.getEquipment().setItemInHandDropChance(1.0F);
    skel.getEquipment().setHelmetDropChance(1.0F);
    skel.getEquipment().setChestplateDropChance(1.0F);
    skel.getEquipment().setLeggingsDropChance(1.0F);
    skel.getEquipment().setBootsDropChance(1.0F);
    
    if (this._name)
    {
      skel.setCustomName("Skeletal " + mineplex.core.common.util.UtilEnt.getName(event.GetEvent().getEntity()));
      skel.setCustomNameVisible(true);
    }
    
    if (!this._minions.containsKey(killer)) {
      this._minions.put(killer, new ArrayList());
    }
    ((ArrayList)this._minions.get(killer)).add(skel);
    
    killer.playSound(killer.getLocation(), org.bukkit.Sound.SKELETON_HURT, 1.0F, 1.0F);
  }
  
  @EventHandler
  public void TargetCancel(EntityTargetEvent event)
  {
    if (!this._minions.containsKey(event.getTarget())) {
      return;
    }
    if (((ArrayList)this._minions.get(event.getTarget())).contains(event.getEntity()))
      event.setCancelled(true);
    Iterator localIterator2;
    for (Iterator localIterator1 = this._minions.keySet().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Player player = (Player)localIterator1.next();
      
      localIterator2 = ((ArrayList)this._minions.get(player)).iterator(); continue;Skeleton skel = (Skeleton)localIterator2.next();
      
      if (event.getEntity().equals(skel))
      {
        UtilMath.offset(skel, player);
      }
    }
  }
  




  @EventHandler
  public void MinionUpdate(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SEC)
      return;
    Iterator<Skeleton> skelIterator;
    for (Iterator localIterator = this._minions.keySet().iterator(); localIterator.hasNext(); 
        


        skelIterator.hasNext())
    {
      Player player = (Player)localIterator.next();
      
      skelIterator = ((ArrayList)this._minions.get(player)).iterator();
      
      continue;
      
      Skeleton skel = (Skeleton)skelIterator.next();
      

      if (!skel.isValid())
      {
        skelIterator.remove();

      }
      else
      {
        double range = 4.0D;
        if ((skel.getTarget() != null) || (((CraftSkeleton)skel).getHandle().getGoalTarget() != null))
        {
          range = this._maxDist;
        }
        
        if (UtilMath.offset(skel, player) > range)
        {
          float speed = 1.0F;
          if (player.isSprinting()) {
            speed = 1.4F;
          }
          
          Location target = skel.getLocation().add(mineplex.core.common.util.UtilAlg.getTrajectory(skel, player).multiply(3));
          
          net.minecraft.server.v1_7_R3.EntityCreature ec = ((CraftCreature)skel).getHandle();
          Navigation nav = ec.getNavigation();
          nav.a(target.getX(), target.getY(), target.getZ(), speed);
          
          skel.setTarget(null);
          ((CraftSkeleton)skel).getHandle().setGoalTarget(null);
        }
      }
    }
  }
  
  @EventHandler
  public void Heal(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SEC)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = this._minions.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      ArrayList<Skeleton> skels = (ArrayList)localIterator1.next();
      
      localIterator2 = skels.iterator(); continue;Skeleton skel = (Skeleton)localIterator2.next();
      
      if (skel.getHealth() > 0.0D) {
        skel.setHealth(Math.min(skel.getMaxHealth(), skel.getHealth() + 1.0D));
      }
    }
  }
  
  public boolean IsMinion(Entity ent) {
    Iterator localIterator2;
    for (Iterator localIterator1 = this._minions.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      ArrayList<Skeleton> skels = (ArrayList)localIterator1.next();
      
      localIterator2 = skels.iterator(); continue;Skeleton skel = (Skeleton)localIterator2.next();
      
      if (ent.equals(skel))
      {
        return true;
      }
    }
    

    return false;
  }
  
  @EventHandler
  public void Combust(EntityCombustEvent event)
  {
    if (IsMinion(event.getEntity())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event) {
    if (event.GetDamagerEntity(true) == null) {
      return;
    }
    if (!IsMinion(event.GetDamagerEntity(true))) {
      return;
    }
    double damage = 4.0D;
    
    if ((event.GetDamagerEntity(true) instanceof Skeleton))
    {
      Skeleton skel = (Skeleton)event.GetDamagerEntity(true);
      
      if (skel.getEquipment().getItemInHand() != null)
      {
        if (skel.getEquipment().getItemInHand().getType() == Material.STONE_SWORD) { damage = 5.0D;
        } else if (skel.getEquipment().getItemInHand().getType() == Material.IRON_SWORD) { damage = 6.0D;
        } else if (skel.getEquipment().getItemInHand().getType() == Material.GOLD_SWORD) { damage = 6.0D;
        } else if (skel.getEquipment().getItemInHand().getType() == Material.DIAMOND_SWORD) { damage = 7.0D;
        }
        else if (skel.getEquipment().getItemInHand().getType() == Material.IRON_AXE) { damage = 5.0D;
        } else if (skel.getEquipment().getItemInHand().getType() == Material.GOLD_AXE) { damage = 5.0D;
        } else if (skel.getEquipment().getItemInHand().getType() == Material.DIAMOND_AXE) { damage = 6.0D;
        }
      }
    }
    if (event.GetProjectile() != null) {
      damage = 6.0D;
    }
    event.AddMod("Skeleton Minion", "Negate", -event.GetDamageInitial(), false);
    event.AddMod("Skeleton Minion", "Damage", damage, false);
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    ArrayList<Skeleton> skels = (ArrayList)this._minions.remove(event.getEntity());
    
    if (skels == null) {
      return;
    }
    for (Skeleton skel : skels) {
      skel.remove();
    }
    skels.clear();
  }
}
