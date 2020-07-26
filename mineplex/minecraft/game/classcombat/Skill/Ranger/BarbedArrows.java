package mineplex.minecraft.game.classcombat.Skill.Ranger;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class BarbedArrows extends Skill
{
  public BarbedArrows(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your arrows are barbed, and give", 
      "opponents Slow 1 for #2#1 seconds.", 
      "If opponent is sprinting, they", 
      "receive Slow 3 instead.", 
      "", 
      "Duration scales with arrow velocity." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile projectile = event.GetProjectile();
    if (projectile == null) { return;
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
    Player damageePlayer = event.GetDamageePlayer();
    

    int str = 0;
    if ((damageePlayer != null) && 
      (damageePlayer.isSprinting())) {
      str = 3;
    }
    
    event.AddMod(damager.getName(), GetName(), 0.0D, false);
    

    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, projectile.getVelocity().length() / 3.0D * (2 + level), str, false, true, true, true);
  }
  
  public void Reset(Player player) {}
}
