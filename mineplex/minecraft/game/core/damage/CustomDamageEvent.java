package mineplex.minecraft.game.core.damage;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.C;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class CustomDamageEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private EntityDamageEvent.DamageCause _eventCause;
  
  private double _initialDamage;
  private ArrayList<DamageChange> _damageMult = new ArrayList();
  private ArrayList<DamageChange> _damageMod = new ArrayList();
  
  private ArrayList<String> _cancellers = new ArrayList();
  
  private HashMap<String, Double> _knockbackMod = new HashMap();
  
  private LivingEntity _damageeEntity;
  
  private Player _damageePlayer;
  
  private LivingEntity _damagerEntity;
  
  private Player _damagerPlayer;
  private Projectile _projectile;
  private boolean _ignoreArmor = false;
  private boolean _ignoreRate = false;
  private boolean _knockback = true;
  private boolean _damageeBrute = false;
  private boolean _damageToLevel = true;
  


  public CustomDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile projectile, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String initialSource, String initialReason, boolean cancelled)
  {
    this._eventCause = cause;
    
    if ((initialSource == null) || (initialReason == null)) {
      this._initialDamage = damage;
    }
    this._damageeEntity = damagee;
    if ((this._damageeEntity != null) && ((this._damageeEntity instanceof Player))) { this._damageePlayer = ((Player)this._damageeEntity);
    }
    this._damagerEntity = damager;
    if ((this._damagerEntity != null) && ((this._damagerEntity instanceof Player))) { this._damagerPlayer = ((Player)this._damagerEntity);
    }
    this._projectile = projectile;
    
    this._knockback = knockback;
    this._ignoreRate = ignoreRate;
    this._ignoreArmor = ignoreArmor;
    
    if ((initialSource != null) && (initialReason != null)) {
      AddMod(initialSource, initialReason, damage, true);
    }
    if (this._eventCause == EntityDamageEvent.DamageCause.FALL) {
      this._ignoreArmor = true;
    }
    if (cancelled) {
      SetCancelled("Pre-Cancelled");
    }
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public void AddMult(String source, String reason, double mod, boolean useAttackName)
  {
    this._damageMult.add(new DamageChange(source, reason, mod, useAttackName));
  }
  

  public void AddMod(String source, String reason, double mod, boolean useAttackName)
  {
    this._damageMod.add(new DamageChange(source, reason, mod, useAttackName));
  }
  
  public void AddKnockback(String reason, double d)
  {
    this._knockbackMod.put(reason, Double.valueOf(d));
  }
  
  public boolean IsCancelled()
  {
    return !this._cancellers.isEmpty();
  }
  
  public void SetCancelled(String reason)
  {
    this._cancellers.add(reason);
  }
  
  public double GetDamage()
  {
    double damage = GetDamageInitial();
    
    for (DamageChange mult : this._damageMult) {
      damage *= mult.GetDamage();
    }
    for (DamageChange mult : this._damageMod) {
      damage += mult.GetDamage();
    }
    return damage;
  }
  
  public LivingEntity GetDamageeEntity()
  {
    return this._damageeEntity;
  }
  
  public Player GetDamageePlayer()
  {
    return this._damageePlayer;
  }
  
  public LivingEntity GetDamagerEntity(boolean ranged)
  {
    if (ranged) {
      return this._damagerEntity;
    }
    if (this._projectile == null) {
      return this._damagerEntity;
    }
    return null;
  }
  
  public Player GetDamagerPlayer(boolean ranged)
  {
    if (ranged) {
      return this._damagerPlayer;
    }
    if (this._projectile == null) {
      return this._damagerPlayer;
    }
    return null;
  }
  
  public Projectile GetProjectile()
  {
    return this._projectile;
  }
  
  public EntityDamageEvent.DamageCause GetCause()
  {
    return this._eventCause;
  }
  
  public double GetDamageInitial()
  {
    return this._initialDamage;
  }
  
  public void SetIgnoreArmor(boolean ignore)
  {
    this._ignoreArmor = ignore;
  }
  
  public void SetIgnoreRate(boolean ignore)
  {
    this._ignoreRate = ignore;
  }
  
  public void SetKnockback(boolean knockback)
  {
    this._knockback = knockback;
  }
  
  public void SetBrute()
  {
    this._damageeBrute = true;
  }
  
  public boolean IsBrute()
  {
    return this._damageeBrute;
  }
  
  public String GetReason()
  {
    String reason = "";
    

    for (DamageChange change : this._damageMod) {
      if (change.UseReason()) {
        reason = reason + C.mSkill + change.GetReason() + ChatColor.GRAY + ", ";
      }
    }
    if (reason.length() > 0)
    {
      reason = reason.substring(0, reason.length() - 2);
      return reason;
    }
    
    return null;
  }
  
  public boolean IsKnockback()
  {
    return this._knockback;
  }
  
  public boolean IgnoreRate()
  {
    return this._ignoreRate;
  }
  
  public boolean IgnoreArmor()
  {
    return this._ignoreArmor;
  }
  
  public void SetDamager(LivingEntity ent)
  {
    if (ent == null) {
      return;
    }
    this._damagerEntity = ent;
    
    this._damagerPlayer = null;
    if ((ent instanceof Player)) {
      this._damagerPlayer = ((Player)ent);
    }
  }
  
  public ArrayList<DamageChange> GetDamageMod() {
    return this._damageMod;
  }
  
  public ArrayList<DamageChange> GetDamageMult()
  {
    return this._damageMult;
  }
  
  public HashMap<String, Double> GetKnockback()
  {
    return this._knockbackMod;
  }
  
  public ArrayList<String> GetCancellers()
  {
    return this._cancellers;
  }
  
  public void SetDamageToLevel(boolean val)
  {
    this._damageToLevel = val;
  }
  
  public boolean DisplayDamageToLevel()
  {
    return this._damageToLevel;
  }
}
