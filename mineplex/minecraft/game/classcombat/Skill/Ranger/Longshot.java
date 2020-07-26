package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Longshot extends Skill
{
  private HashMap<Entity, Location> _arrows = new HashMap();
  
  public Longshot(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Arrows do an additional 1 damage", 
      "for every #4#-0.5 Blocks they travelled,", 
      "however, their base damage is", 
      "reduced by 3.", 
      "", 
      "Maximum of #5#5 additional damage." });
  }
  

  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    int level = getLevel((Player)event.getEntity());
    if (level == 0) { return;
    }
    
    this._arrows.put(event.getProjectile(), event.getProjectile().getLocation());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
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
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    int level = getLevel(damager);
    
    Location loc = (Location)this._arrows.remove(projectile);
    double length = UtilMath.offset(loc, projectile.getLocation());
    

    double damage = Math.min(5 + 5 * level, length / (4.0D - 0.5D * level) - 3.0D);
    
    event.AddMod(damager.getName(), GetName(), damage, damage > 0.0D);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Entity> arrowIterator = this._arrows.keySet().iterator(); arrowIterator.hasNext();)
    {
      Entity arrow = (Entity)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player) {}
}
