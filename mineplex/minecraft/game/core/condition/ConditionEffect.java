package mineplex.minecraft.game.core.condition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffectType;

public class ConditionEffect implements org.bukkit.event.Listener
{
  protected ConditionManager Manager;
  
  public ConditionEffect(ConditionManager manager)
  {
    this.Manager = manager;
    this.Manager.GetPlugin().getServer().getPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Invulnerable(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    if (!this.Manager.IsInvulnerable(ent)) {
      return;
    }
    
    event.SetCancelled("Invulnerable");
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Cloak(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    if (!this.Manager.IsCloaked(ent)) {
      return;
    }
    
    event.SetCancelled("Cloak");
  }
  
  @EventHandler
  public void Cloak(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (LivingEntity ent : this.Manager.GetActiveConditions().keySet())
    {
      if ((ent instanceof Player))
      {

        Player player = (Player)ent;
        

        if (this.Manager.IsCloaked(ent)) {
          for (Player other : Bukkit.getServer().getOnlinePlayers()) {
            ((CraftPlayer)other).hidePlayer(player, true, false);
          }
        } else {
          for (Player other : Bukkit.getServer().getOnlinePlayers())
          {
            other.showPlayer(player); }
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void Cloak(EntityTargetEvent event) {
    if (!(event.getTarget() instanceof Player)) {
      return;
    }
    if (!this.Manager.HasCondition((LivingEntity)event.getTarget(), Condition.ConditionType.CLOAK, null)) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Protection(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!damagee.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      return;
    }
    Condition cond = this.Manager.GetActiveCondition(damagee, Condition.ConditionType.DAMAGE_RESISTANCE);
    if (cond == null) { return;
    }
    event.AddMod(UtilEnt.getName(cond.GetSource()), cond.GetReason(), -1 * (cond.GetMult() + 1), false);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void VulnerabilityDamagee(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!damagee.hasPotionEffect(PotionEffectType.WITHER)) {
      return;
    }
    Condition cond = this.Manager.GetActiveCondition(damagee, Condition.ConditionType.WITHER);
    if (cond == null) { return;
    }
    event.AddMod(UtilEnt.getName(cond.GetSource()), cond.GetReason(), cond.GetMult() + 1, false);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void VulnerabilityDamager(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!damager.hasPotionEffect(PotionEffectType.WITHER)) {
      return;
    }
    Condition cond = this.Manager.GetActiveCondition(damager, Condition.ConditionType.WITHER);
    if (cond == null) { return;
    }
    event.AddMod(UtilEnt.getName(cond.GetSource()), cond.GetReason(), -1 * (cond.GetMult() + 1), false);
  }
  
  @EventHandler
  public void VulnerabilityEffect(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (LivingEntity ent : this.Manager.GetActiveConditions().keySet())
    {
      if (!ent.isDead())
      {

        if (ent.hasPotionEffect(PotionEffectType.WITHER))
        {

          if (!this.Manager.HasCondition(ent, Condition.ConditionType.CLOAK, null))
          {

            ent.getWorld().playEffect(ent.getLocation(), Effect.SMOKE, 1);
            ent.getWorld().playEffect(ent.getLocation(), Effect.SMOKE, 3);
            ent.getWorld().playEffect(ent.getLocation(), Effect.SMOKE, 5);
            ent.getWorld().playEffect(ent.getLocation(), Effect.SMOKE, 7);
          } } }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void VulnerabilityWitherCancel(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.WITHER) {
      event.SetCancelled("Vulnerability Wither");
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Strength(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
      return;
    }
    Condition cond = this.Manager.GetActiveCondition(damager, Condition.ConditionType.INCREASE_DAMAGE);
    if (cond == null) { return;
    }
    event.AddMod(damager.getName(), cond.GetReason(), cond.GetMult() + 1, true);
  }
  
  @EventHandler
  public void Shock(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = this.Manager.GetActiveConditions().keySet().iterator(); localIterator1.hasNext(); 
        localIterator2.hasNext())
    {
      LivingEntity ent = (LivingEntity)localIterator1.next();
      localIterator2 = ((LinkedList)this.Manager.GetActiveConditions().get(ent)).iterator(); continue;ConditionActive ind = (ConditionActive)localIterator2.next();
      if (ind.GetCondition().GetType() == Condition.ConditionType.SHOCK)
        ent.playEffect(org.bukkit.EntityEffect.HURT);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void Lightning(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.LIGHTNING) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    Condition condition = this.Manager.GetActiveCondition(ent, Condition.ConditionType.LIGHTNING);
    if (condition == null) { return;
    }
    
    event.SetDamager(condition.GetSource());
    event.AddMod(UtilEnt.getName(condition.GetSource()), condition.GetReason(), 0.0D, true);
    
    if (condition.GetMult() != 0) {
      event.AddMod("Lightning Modifier", UtilEnt.getName(condition.GetSource()), condition.GetMult(), false);
    }
    event.SetKnockback(false);
  }
  
  @EventHandler
  public void Explosion(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) && (event.GetCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    Condition condition = this.Manager.GetActiveCondition(ent, Condition.ConditionType.EXPLOSION);
    if (condition == null) { return;
    }
    
    event.SetDamager(condition.GetSource());
    
    event.AddMod("Negate", condition.GetReason(), -event.GetDamageInitial(), false);
    event.AddMod(UtilEnt.getName(condition.GetSource()), condition.GetReason(), Math.min(event.GetDamageInitial(), condition.GetMult()), true);
    
    event.SetKnockback(false);
  }
  
  @EventHandler
  public void Fire(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.FIRE_TICK) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    
    if (ent.getFireTicks() > 160) {
      ent.setFireTicks(160);
    }
    Condition condition = this.Manager.GetActiveCondition(ent, Condition.ConditionType.BURNING);
    if (condition == null) { return;
    }
    
    event.SetDamager(condition.GetSource());
    event.AddMod(UtilEnt.getName(condition.GetSource()), condition.GetReason(), 0.0D, true);
    event.SetIgnoreArmor(true);
    event.SetKnockback(false);
  }
  
  @EventHandler
  public void FireDouse(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (LivingEntity ent : this.Manager.GetActiveConditions().keySet()) {
      if (ent.getFireTicks() <= 0)
        this.Manager.EndCondition(ent, Condition.ConditionType.BURNING, null);
    }
  }
  
  @EventHandler
  public void Poison(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.POISON) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    Condition condition = this.Manager.GetActiveCondition(ent, Condition.ConditionType.POISON);
    if (condition == null) { return;
    }
    
    event.SetDamager(condition.GetSource());
    event.AddMod(UtilEnt.getName(condition.GetSource()), condition.GetReason(), 0.0D, true);
    event.SetIgnoreArmor(true);
    event.SetKnockback(false);
  }
  
  @EventHandler
  public void Fall(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    Condition condition = this.Manager.GetActiveCondition(ent, Condition.ConditionType.FALLING);
    if (condition == null) { return;
    }
    
    event.SetDamager(condition.GetSource());
    event.AddMod(UtilEnt.getName(condition.GetSource()), condition.GetReason(), 0.0D, true);
    event.SetIgnoreArmor(true);
    event.SetKnockback(false);
  }
  
  @EventHandler
  public void Fall(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (LivingEntity ent : this.Manager.GetActiveConditions().keySet())
    {
      if (UtilEnt.isGrounded(ent))
      {

        Condition condition = this.Manager.GetActiveCondition(ent, Condition.ConditionType.FALLING);
        if (condition == null) { return;
        }
        if (mineplex.core.common.util.UtilTime.elapsed(condition.GetTime(), 250L))
        {

          this.Manager.EndCondition(ent, Condition.ConditionType.FALLING, null);
        }
      }
    }
  }
}
