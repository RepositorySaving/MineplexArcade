package mineplex.minecraft.game.classcombat.Skill.Knight;

import mineplex.core.energy.event.EnergyEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Knight extends Skill
{
  public Knight(SkillFactory skills, String name, IPvpClass.ClassType classType, mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "25% reduction in Arrow Velocity." });
  }
  

  @EventHandler
  public void BowShoot(EntityShootBowEvent event)
  {
    if (getLevel(event.getEntity()) == 0) {
      return;
    }
    event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(0.75D));
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
