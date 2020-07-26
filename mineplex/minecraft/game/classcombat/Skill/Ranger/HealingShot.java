package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;

public class HealingShot extends SkillActive
{
  private HashSet<Entity> _arrows = new HashSet();
  private HashSet<Player> _active = new HashSet();
  










  public HealingShot(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Prepare a healing shot;", 
      "Your next arrow will give its target", 
      "Regeneration 1 for #2#2 seconds,", 
      "and remove all negative effects." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._active.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 2.5F, 2.0F);
  }
  
  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._active.remove(player)) {
      return;
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You fired " + F.skill(GetName(getLevel(player))) + "."));
    
    this._arrows.add(event.getProjectile());
  }
  

  @EventHandler(priority=org.bukkit.event.EventPriority.NORMAL)
  public void ArrowHit(EntityDamageEvent event)
  {
    if (event.getCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    if (!(event instanceof EntityDamageByEntityEvent)) {
      return;
    }
    EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent)event;
    
    if (!(eventEE.getDamager() instanceof Projectile)) {
      return;
    }
    Projectile projectile = (Projectile)eventEE.getDamager();
    

    if (!this._arrows.contains(projectile)) {
      return;
    }
    if (!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    LivingEntity damagee = (LivingEntity)event.getEntity();
    
    if (projectile.getShooter() == null) {
      return;
    }
    if (!(projectile.getShooter() instanceof Player)) {
      return;
    }
    Player damager = (Player)projectile.getShooter();
    

    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    this._arrows.remove(projectile);
    projectile.remove();
    

    this.Factory.Condition().Factory().Regen(GetName(), damagee, damager, 2 + 2 * level, 0, false, true, true);
    

    damagee.setFireTicks(0);
    damagee.removePotionEffect(PotionEffectType.SLOW);
    damagee.removePotionEffect(PotionEffectType.POISON);
    damagee.removePotionEffect(PotionEffectType.CONFUSION);
    damagee.removePotionEffect(PotionEffectType.WEAKNESS);
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.LEVEL_UP, 1.0F, 1.5F);
    damagee.getWorld().playEffect(damagee.getLocation(), org.bukkit.Effect.STEP_SOUND, 115);
    damagee.playEffect(EntityEffect.HURT);
    

    UtilPlayer.message(damagee, F.main(GetClassType().name(), 
      F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    
    UtilPlayer.message(damager, F.main(GetClassType().name(), 
      "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
    

    UtilParticle.PlayParticle(UtilParticle.ParticleType.HEART, damagee.getLocation(), (float)(Math.random() - 0.5D), (float)(Math.random() + 0.5D), (float)(Math.random() - 0.5D), 2.0F, 12);
    

    projectile.remove();
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._arrows)
    {
      UtilParticle.PlayParticle(UtilParticle.ParticleType.HEART, ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    }
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Entity> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Entity arrow = (Entity)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid()) || (arrow.isOnGround())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
