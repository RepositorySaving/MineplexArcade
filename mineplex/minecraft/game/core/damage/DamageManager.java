package mineplex.minecraft.game.core.damage;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.npc.NpcManager;
import mineplex.minecraft.game.core.combat.ClientCombat;
import mineplex.minecraft.game.core.combat.CombatManager;
import net.minecraft.server.v1_7_R3.DamageSource;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityLiving;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DamageManager extends MiniPlugin
{
  private CombatManager _combatManager;
  private DisguiseManager _disguiseManager;
  protected Field _lastDamageByPlayerTime;
  protected Method _k;
  public boolean UseSimpleWeaponDamage = false;
  public boolean DisableDamageChanges = false;
  
  private boolean _enabled = true;
  
  public DamageManager(JavaPlugin plugin, CombatManager combatManager, NpcManager npcManager, DisguiseManager disguiseManager)
  {
    super("Damage Manager", plugin);
    
    this._combatManager = combatManager;
    this._disguiseManager = disguiseManager;
    
    try
    {
      this._lastDamageByPlayerTime = EntityLiving.class.getDeclaredField("lastDamageByPlayerTime");
      this._lastDamageByPlayerTime.setAccessible(true);
      this._k = EntityLiving.class.getDeclaredMethod("h", new Class[] { Float.TYPE });
      this._k.setAccessible(true);
    }
    catch (Exception e)
    {
      System.out.println("Problem getting access to EntityLiving: " + e.getMessage());
    }
    
    RegisterEvents(new mineplex.minecraft.game.core.damage.compatibility.NpcProtectListener(npcManager));
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void StartDamageEvent(EntityDamageEvent event)
  {
    if (!this._enabled) {
      return;
    }
    boolean preCancel = false;
    if (event.isCancelled()) {
      preCancel = true;
    }
    if (!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    
    LivingEntity damagee = GetDamageeEntity(event);
    LivingEntity damager = UtilEvent.GetDamagerEntity(event, true);
    Projectile projectile = GetProjectile(event);
    
    if ((projectile instanceof org.bukkit.entity.Fish)) {
      return;
    }
    
    if (!this.DisableDamageChanges) {
      WeaponDamage(event, damager);
    }
    
    NewDamageEvent(damagee, damager, projectile, event.getCause(), event.getDamage(), true, false, false, null, null, preCancel);
    
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void removeDemArrowsCrazyMan(EntityDamageEvent event)
  {
    if (event.isCancelled())
    {
      Projectile projectile = GetProjectile(event);
      
      if ((projectile instanceof Arrow))
      {
        projectile.teleport(new Location(projectile.getWorld(), 0.0D, 0.0D, 0.0D));
        projectile.remove();
      }
    }
  }
  























  public void NewDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile proj, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String source, String reason)
  {
    NewDamageEvent(damagee, damager, proj, 
      cause, damage, knockback, ignoreRate, ignoreArmor, 
      source, reason, false);
  }
  


  public void NewDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile proj, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String source, String reason, boolean cancelled)
  {
    this._plugin.getServer().getPluginManager().callEvent(
      new CustomDamageEvent(damagee, damager, proj, cause, damage, 
      knockback, ignoreRate, ignoreArmor, 
      source, reason, cancelled));
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void CancelDamageEvent(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity().getHealth() <= 0.0D)
    {
      event.SetCancelled("0 Health");
      return;
    }
    
    if (event.GetDamageePlayer() != null)
    {
      Player damagee = event.GetDamageePlayer();
      

      if (damagee.getGameMode() != GameMode.SURVIVAL)
      {
        event.SetCancelled("Damagee in Creative");
        return;
      }
      

      if (!event.IgnoreRate())
      {
        if (!this._combatManager.Get(damagee.getName()).CanBeHurtBy(event.GetDamagerEntity(true)))
        {
          event.SetCancelled("World/Monster Damage Rate");
          return;
        }
      }
    }
    
    if (event.GetDamagerPlayer(true) != null)
    {
      Player damager = event.GetDamagerPlayer(true);
      

      if (damager.getGameMode() != GameMode.SURVIVAL)
      {
        event.SetCancelled("Damager in Creative");
        return;
      }
      

      if ((!event.IgnoreRate()) && 
        (!this._combatManager.Get(damager.getName()).CanHurt(event.GetDamageeEntity())))
      {
        event.SetCancelled("PvP Damage Rate");
        return;
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void EndDamageEvent(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetDamage() < 1.0D) {
      return;
    }
    Damage(event);
    

    if ((event.GetProjectile() != null) && ((event.GetProjectile() instanceof Arrow)))
    {
      Player player = event.GetDamagerPlayer(true);
      if (player != null)
      {
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5F, 0.5F);
      }
    }
  }
  
  private void Damage(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity() == null) {
      return;
    }
    if (event.GetDamageeEntity().getHealth() <= 0.0D) {
      return;
    }
    
    if (event.GetDamageePlayer() != null)
    {

      this._combatManager.AddAttack(event);
    }
    
    if ((event.GetDamagerPlayer(true) != null) && (event.DisplayDamageToLevel()))
    {

      if (event.GetCause() != EntityDamageEvent.DamageCause.THORNS) {
        event.GetDamagerPlayer(true).setLevel((int)event.GetDamage());
      }
    }
    try
    {
      double bruteBonus = 0.0D;
      if (event.IsBrute())
      {
        if ((event.GetCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) || 
          (event.GetCause() == EntityDamageEvent.DamageCause.PROJECTILE) || 
          (event.GetCause() == EntityDamageEvent.DamageCause.CUSTOM))
        {
          bruteBonus = Math.min(8.0D, event.GetDamage());
        }
      }
      HandleDamage(event.GetDamageeEntity(), event.GetDamagerEntity(true), event.GetCause(), (float)(event.GetDamage() + bruteBonus), event.IgnoreArmor());
      

      event.GetDamageeEntity().playEffect(EntityEffect.HURT);
      

      if (event.GetCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
        ((CraftLivingEntity)event.GetDamageeEntity()).getHandle().p(((CraftLivingEntity)event.GetDamageeEntity()).getHandle().aY() + 1);
      }
      
      double knockback = event.GetDamage();
      if (knockback < 2.0D) knockback = 2.0D;
      knockback = Math.log10(knockback);
      
      for (Iterator localIterator = event.GetKnockback().values().iterator(); localIterator.hasNext();) { double cur = ((Double)localIterator.next()).doubleValue();
        knockback *= cur;
      }
      if ((event.IsKnockback()) && 
        (event.GetDamagerEntity(true) != null))
      {
        Vector trajectory = mineplex.core.common.util.UtilAlg.getTrajectory2d(event.GetDamagerEntity(true), event.GetDamageeEntity());
        trajectory.multiply(0.6D * knockback);
        trajectory.setY(Math.abs(trajectory.getY()));
        
        UtilAction.velocity(event.GetDamageeEntity(), 
          trajectory, 0.2D + trajectory.length() * 0.8D, false, 0.0D, Math.abs(0.2D * knockback), 0.4D + 0.04D * knockback, true);
      }
      
      DisplayDamage(event);
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
  }
  
  private void DisplayDamage(CustomDamageEvent event)
  {
    for (Player player : )
    {
      if (UtilGear.isMat(player.getItemInHand(), Material.BOOK))
      {

        UtilPlayer.message(player, " ");
        UtilPlayer.message(player, "=====================================");
        UtilPlayer.message(player, F.elem("Reason ") + event.GetReason());
        UtilPlayer.message(player, F.elem("Cause ") + event.GetCause());
        UtilPlayer.message(player, F.elem("Damager ") + UtilEnt.getName(event.GetDamagerEntity(true)));
        UtilPlayer.message(player, F.elem("Damagee ") + UtilEnt.getName(event.GetDamageeEntity()));
        UtilPlayer.message(player, F.elem("Projectile ") + UtilEnt.getName(event.GetProjectile()));
        UtilPlayer.message(player, F.elem("Damage ") + event.GetDamage());
        UtilPlayer.message(player, F.elem("Damage Initial ") + event.GetDamageInitial());
        for (DamageChange cur : event.GetDamageMod()) {
          UtilPlayer.message(player, F.elem("Mod ") + cur.GetDamage() + " - " + cur.GetReason() + " by " + cur.GetSource());
        }
        for (DamageChange cur : event.GetDamageMult()) {
          UtilPlayer.message(player, F.elem("Mult ") + cur.GetDamage() + " - " + cur.GetReason() + " by " + cur.GetSource());
        }
        for (String cur : event.GetKnockback().keySet()) {
          UtilPlayer.message(player, F.elem("Knockback ") + cur + " = " + event.GetKnockback().get(cur));
        }
        for (String cur : event.GetCancellers()) {
          UtilPlayer.message(player, F.elem("Cancel ") + cur);
        }
      }
    }
  }
  
  private void HandleDamage(LivingEntity damagee, LivingEntity damager, EntityDamageEvent.DamageCause cause, float damage, boolean ignoreArmor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    EntityLiving entityDamagee = ((CraftLivingEntity)damagee).getHandle();
    EntityLiving entityDamager = null;
    
    if (damager != null) {
      entityDamager = ((CraftLivingEntity)damager).getHandle();
    }
    entityDamagee.aG = 1.5F;
    
    if (entityDamagee.noDamageTicks > entityDamagee.maxNoDamageTicks / 2.0F)
    {
      if (damage <= entityDamagee.lastDamage)
      {
        return;
      }
      
      ApplyDamage(entityDamagee, damage - entityDamagee.lastDamage, ignoreArmor);
      entityDamagee.lastDamage = damage;
    }
    else
    {
      entityDamagee.lastDamage = damage;
      entityDamagee.aw = entityDamagee.getHealth();
      
      ApplyDamage(entityDamagee, damage, ignoreArmor);
    }
    

    if (entityDamager != null) {
      entityDamagee.b(entityDamager);
    }
    if ((entityDamager != null) && 
      ((entityDamager instanceof EntityHuman)))
    {
      this._lastDamageByPlayerTime.setInt(entityDamagee, 100);
      entityDamagee.killer = ((EntityHuman)entityDamager);
    }
    
    if (entityDamagee.getHealth() <= 0.0F)
    {
      if (entityDamager != null)
      {
        if ((entityDamager instanceof EntityHuman)) { entityDamagee.die(DamageSource.playerAttack((EntityHuman)entityDamager));
        } else if ((entityDamager instanceof EntityLiving)) entityDamagee.die(DamageSource.mobAttack(entityDamager)); else {
          entityDamagee.die(DamageSource.GENERIC);
        }
      } else {
        entityDamagee.die(DamageSource.GENERIC);
      }
    }
  }
  
  @EventHandler
  public void DamageSound(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) && (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE)) {
      return;
    }
    
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    if (this._disguiseManager.isDisguised(damagee))
    {
      this._disguiseManager.getDisguise(damagee).playHurtSound();
      return;
    }
    

    Sound sound = Sound.HURT_FLESH;
    float vol = 1.0F;
    float pitch = 1.0F;
    

    if ((damagee instanceof Player))
    {
      Player player = (Player)damagee;
      
      double r = Math.random();
      
      ItemStack stack = null;
      
      if (r > 0.5D) { stack = player.getInventory().getChestplate();
      } else if (r > 0.25D) { stack = player.getInventory().getLeggings();
      } else if (r > 0.1D) stack = player.getInventory().getHelmet(); else {
        stack = player.getInventory().getBoots();
      }
      if (stack != null)
      {
        if (stack.getType().toString().contains("LEATHER_"))
        {
          sound = Sound.SHOOT_ARROW;
          pitch = 2.0F;
        }
        else if (stack.getType().toString().contains("CHAINMAIL_"))
        {
          sound = Sound.ITEM_BREAK;
          pitch = 1.4F;
        }
        else if (stack.getType().toString().contains("GOLD_"))
        {
          sound = Sound.ITEM_BREAK;
          pitch = 1.8F;
        }
        else if (stack.getType().toString().contains("IRON_"))
        {
          sound = Sound.BLAZE_HIT;
          pitch = 0.7F;
        }
        else if (stack.getType().toString().contains("DIAMOND_"))
        {
          sound = Sound.BLAZE_HIT;
          pitch = 0.9F;
        }
        
      }
    }
    else
    {
      UtilEnt.PlayDamageSound(damagee);
      return;
    }
    
    damagee.getWorld().playSound(damagee.getLocation(), sound, vol, pitch);
  }
  
  private void ApplyDamage(EntityLiving entityLiving, float damage, boolean ignoreArmor) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
    if (!ignoreArmor)
    {
      int j = 25 - entityLiving.aU();
      float k = damage * j;
      
      this._k.invoke(entityLiving, new Object[] { Float.valueOf(damage) });
      damage = k / 25.0F;
    }
    












    entityLiving.setHealth(entityLiving.getHealth() - damage);
  }
  
  private void WeaponDamage(EntityDamageEvent event, LivingEntity ent)
  {
    if (!(ent instanceof Player)) {
      return;
    }
    if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = (Player)ent;
    
    if (this.UseSimpleWeaponDamage)
    {
      if (event.getDamage() > 1.0D) {
        event.setDamage(event.getDamage() - 1.0D);
      }
      if (damager.getItemInHand().getType().name().contains("GOLD_")) {
        event.setDamage(event.getDamage() + 2.0D);
      }
      return;
    }
    
    if ((damager.getItemInHand() == null) || (!UtilGear.isWeapon(damager.getItemInHand())))
    {
      event.setDamage(1);
      return;
    }
    
    Material mat = damager.getItemInHand().getType();
    
    int damage = 6;
    
    if (mat.name().contains("WOOD")) { damage -= 3;
    } else if (mat.name().contains("STONE")) { damage -= 2;
    } else if (mat.name().contains("DIAMOND")) { damage++;
    } else if (mat.name().contains("GOLD")) { damage += 0;
    }
    event.setDamage(damage);
  }
  
  private LivingEntity GetDamageeEntity(EntityDamageEvent event)
  {
    if ((event.getEntity() instanceof LivingEntity)) {
      return (LivingEntity)event.getEntity();
    }
    return null;
  }
  
  private Projectile GetProjectile(EntityDamageEvent event)
  {
    if (!(event instanceof EntityDamageByEntityEvent)) {
      return null;
    }
    EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent)event;
    
    if ((eventEE.getDamager() instanceof Projectile)) {
      return (Projectile)eventEE.getDamager();
    }
    return null;
  }
  
  public void SetEnabled(boolean var)
  {
    this._enabled = var;
  }
  
  public CombatManager GetCombatManager()
  {
    return this._combatManager;
  }
}
