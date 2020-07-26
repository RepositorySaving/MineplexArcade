package mineplex.minecraft.game.classcombat.Skill.Assassin;

import mineplex.core.common.util.UtilGear;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ShockingStrikes extends Skill
{
  public ShockingStrikes(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your attacks shock targets for", 
      "#0#1 seconds, giving them Slow 1 and", 
      "Screen-Shake." });
  }
  

  @org.bukkit.event.EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
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
    if (!UtilGear.isWeapon(damager.getItemInHand())) {
      return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    this.Factory.Condition().Factory().Shock(GetName(), damagee, damager, level, false, false);
    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, level, 0, false, false, true, false);
    

    event.AddMod(damager.getName(), GetName(), 0.0D, true);
  }
  
  public void Reset(Player player) {}
}
