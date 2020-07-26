package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Intimidation extends Skill
{
  public Intimidation(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You intimidate nearby enemies;", 
      "Enemies within #3#3 blocks receive Slow 1." });
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = getLevel(cur);
      if (level != 0)
      {
        HashMap<Player, Double> targets = UtilPlayer.getInRadius(cur.getLocation(), 3 + level * 3);
        for (Player other : targets.keySet()) {
          if ((!other.equals(cur)) && 
            (this.Factory.Relation().CanHurt(cur, other)) && 
            (getLevel(other) < level))
          {
            double dist = ((Double)targets.get(other)).doubleValue();
            int mult = 0;
            


            this.Factory.Condition().Factory().Slow(GetName(), other, cur, 0.9D, mult, false, true, false, true);
          }
        }
      }
    }
  }
  
  public void Reset(Player player) {}
}
