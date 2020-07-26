package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillChargeBow;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Overcharge
  extends SkillChargeBow
{
  private WeakHashMap<Arrow, Double> _arrows = new WeakHashMap();
  


  public Overcharge(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel)
  {
    super(skills, name, classType, skillType, cost, maxLevel, 0.01F, 0.005F, false, true);
    
    SetDesc(
      new String[] {
      "Charge your bow to deal bonus damage.", 
      "", 
      GetChargeString(), 
      "", 
      "Deals up to #1#1 bonus damage." });
  }
  


  public void DoSkillCustom(Player player, float charge, Arrow arrow)
  {
    double damage = charge * (1 + 1 * getLevel(player));
    this._arrows.put(arrow, Double.valueOf(damage));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void ArrowHit(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile projectile = event.GetProjectile();
    if (projectile == null) { return;
    }
    if (!this._arrows.containsKey(projectile)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    double damage = ((Double)this._arrows.remove(projectile)).doubleValue();
    

    event.AddMod(damager.getName(), GetName(), damage, true);
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.HURT_FLESH, 1.0F, 0.5F);
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._arrows.keySet())
    {
      UtilParticle.PlayParticle(UtilParticle.ParticleType.RED_DUST, ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    }
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Arrow> arrowIterator = this._arrows.keySet().iterator(); arrowIterator.hasNext();)
    {
      Arrow arrow = (Arrow)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid()) || (arrow.isOnGround())) {
        arrowIterator.remove();
      }
    }
  }
}
