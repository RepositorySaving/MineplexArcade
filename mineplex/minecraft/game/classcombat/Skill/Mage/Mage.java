package mineplex.minecraft.game.classcombat.Skill.Mage;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Mage extends Skill
{
  public Mage(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "25% reduction in Arrow Velocity." });
  }
  

  @org.bukkit.event.EventHandler
  public void BowShoot(EntityShootBowEvent event)
  {
    if (getLevel(event.getEntity()) == 0) {
      return;
    }
    event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(0.75D));
  }
  
  public void Reset(Player player) {}
}
