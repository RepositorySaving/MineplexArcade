package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.core.energy.ClientEnergy;
import mineplex.core.energy.event.EnergyEvent;
import mineplex.core.energy.event.EnergyEvent.EnergyChangeReason;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;

public class Recharge extends Skill
{
  public Recharge(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "For every second since you", 
      "last used Energy, you receive", 
      "+ #0#20 % Energy regenerate rate.", 
      "", 
      "Maximum of + #0#100 % bonus." });
  }
  

  @org.bukkit.event.EventHandler
  public void Skill(EnergyEvent event)
  {
    if (event.GetReason() != EnergyEvent.EnergyChangeReason.Recharge) {
      return;
    }
    int level = getLevel(event.GetPlayer());
    if (level <= 0) {
      return;
    }
    long duration = System.currentTimeMillis() - ((ClientEnergy)this.Factory.Energy().Get(event.GetPlayer())).LastEnergy;
    
    int bonus = (int)(duration / 1000L);
    
    if (bonus > 5) {
      bonus = 5;
    }
    event.AddMod(bonus * (level * 0.08D));
  }
  
  public void Reset(Player player) {}
}
