package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;
import org.bukkit.entity.Player;

public class Resistance extends Skill
{
  public Resistance(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your body and mind is exceptionally resistant.", 
      "Durations on you are #0#25 % shorter for;", 
      "Slow, Fire, Shock, Confusion, Poison, Blindness." });
  }
  

  @org.bukkit.event.EventHandler
  public void Resist(ConditionApplyEvent event)
  {
    if ((event.GetCondition().GetType() != Condition.ConditionType.BURNING) && 
      (event.GetCondition().GetType() != Condition.ConditionType.SLOW) && 
      (event.GetCondition().GetType() != Condition.ConditionType.SHOCK) && 
      (event.GetCondition().GetType() != Condition.ConditionType.CONFUSION) && 
      (event.GetCondition().GetType() != Condition.ConditionType.POISON) && 
      (event.GetCondition().GetType() != Condition.ConditionType.BLINDNESS)) {
      return;
    }
    int level = getLevel(event.GetCondition().GetEnt());
    if (level <= 0) {
      return;
    }
    double reduction = -(0.25F * level);
    
    event.GetCondition().ModifyTicks((int)(event.GetCondition().GetTicksTotal() * reduction));
  }
  
  public void Reset(Player player) {}
}
