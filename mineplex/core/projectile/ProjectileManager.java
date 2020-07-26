package mineplex.core.projectile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ProjectileManager
  extends MiniPlugin
{
  private WeakHashMap<Entity, ProjectileUser> _thrown = new WeakHashMap();
  
  public ProjectileManager(JavaPlugin plugin)
  {
    super("Throw", plugin);
  }
  

  public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, double hitboxMult)
  {
    this._thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
      expireTime, hitPlayer, hitBlock, idle, false, null, 1.0F, 1.0F, null, 0, null, null, hitboxMult, null));
  }
  


  public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, double hitboxMult, DisguiseManager disguise)
  {
    this._thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
      expireTime, hitPlayer, hitBlock, idle, false, 
      null, 1.0F, 1.0F, null, 0, null, null, hitboxMult, disguise));
  }
  

  public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, boolean pickup, double hitboxMult)
  {
    this._thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
      expireTime, hitPlayer, hitBlock, idle, pickup, 
      null, 1.0F, 1.0F, null, 0, null, null, hitboxMult, null));
  }
  


  public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, Sound sound, float soundVolume, float soundPitch, Effect effect, int effectData, UpdateType effectRate, double hitboxMult)
  {
    this._thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
      expireTime, hitPlayer, hitBlock, idle, false, 
      sound, soundVolume, soundPitch, effect, effectData, effectRate, null, hitboxMult, null));
  }
  


  public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, Sound sound, float soundVolume, float soundPitch, UtilParticle.ParticleType particle, Effect effect, int effectData, UpdateType effectRate, double hitboxMult)
  {
    this._thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
      expireTime, hitPlayer, hitBlock, idle, false, 
      sound, soundVolume, soundPitch, effect, effectData, effectRate, particle, hitboxMult, null));
  }
  


  public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, Sound sound, float soundVolume, float soundPitch, UtilParticle.ParticleType particle, UpdateType effectRate, double hitboxMult)
  {
    this._thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
      expireTime, hitPlayer, hitBlock, idle, false, 
      sound, soundVolume, soundPitch, null, 0, effectRate, particle, hitboxMult, null));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    Entity cur;
    if (event.getType() == UpdateType.TICK)
    {
      HashSet<Entity> remove = new HashSet();
      
      for (Entity cur : this._thrown.keySet()) {
        if (((ProjectileUser)this._thrown.get(cur)).Collision()) {
          remove.add(cur);
        } else if ((cur.isDead()) || (!cur.isValid()))
          remove.add(cur);
      }
      for (??? = remove.iterator(); ???.hasNext();) { cur = (Entity)???.next();
        this._thrown.remove(cur);
      }
    }
    
    for (ProjectileUser cur : this._thrown.values()) {
      cur.Effect(event);
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Pickup(PlayerPickupItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if ((this._thrown.containsKey(event.getItem())) && 
      (!((ProjectileUser)this._thrown.get(event.getItem())).CanPickup(event.getPlayer()))) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void HopperPickup(InventoryPickupItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (this._thrown.containsKey(event.getItem())) {
      event.setCancelled(true);
    }
  }
}
