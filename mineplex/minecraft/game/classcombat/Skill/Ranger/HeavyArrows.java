package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class HeavyArrows extends Skill
{
  private HashSet<Entity> _arrows = new HashSet();
  
  public HeavyArrows(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your arrows are extremely heavy,", 
      "moving #5#5 % slower and dealing", 
      "an additional #10#10 % knockback", 
      "as well as #1#1 additional damage.", 
      "", 
      "You also receive #10#10 % knockback", 
      "when firing arrows." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void ShootBow(EntityShootBowEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getEntity();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    if (Recharge.Instance.use(player, GetName(), 500L, false, false))
    {
      double vel = event.getProjectile().getVelocity().length() * (0.1D + 0.1D * level);
      UtilAction.velocity(player, player.getLocation().getDirection().multiply(-1), vel, 
        false, 0.0D, 0.2D, 0.8D, true);
    }
    

    event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(0.95D - level * 0.05D));
    
    this._arrows.add(event.getProjectile());
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
    if (!this._arrows.contains(projectile)) {
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
    
    event.AddKnockback(GetName(), 1.1D + 0.1D * level);
    event.AddMod(GetName(), GetName(), 1 + level, true);
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._arrows)
    {
      mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.CRIT, ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
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
  
  public void Reset(Player player) {}
}
