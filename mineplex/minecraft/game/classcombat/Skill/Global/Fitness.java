package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;

public class Fitness extends Skill
{
  public Fitness(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Maximum Energy is increased by #0#27 ( #0#15 %)." });
  }
  


  public void OnPlayerAdd(Player player)
  {
    this.Factory.Energy().AddEnergyMaxMod(player, GetName(), 18 * getLevel(player));
  }
  

  public void Reset(Player player)
  {
    this.Factory.Energy().RemoveEnergyMaxMod(player, GetName());
  }
}
