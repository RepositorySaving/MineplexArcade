package mineplex.minecraft.game.classcombat.Skill.Ranger;

import mineplex.core.energy.event.EnergyEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Ranger extends Skill
{
  public Ranger(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[0]);
  }
  


  @EventHandler
  public void CancelEnergy(EnergyEvent event)
  {
    if (getLevel(event.GetPlayer()) > 0) {
      event.setCancelled(true);
    }
  }
  
  public void Reset(Player player) {}
}
