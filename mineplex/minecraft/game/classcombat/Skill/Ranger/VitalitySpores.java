package mineplex.minecraft.game.classcombat.Skill.Ranger;

import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class VitalitySpores extends Skill
{
  public VitalitySpores(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "After #12#-2 seconds of not taking damage,", 
      "forest spores surround you, giving", 
      "you Regeneration 1 for #3#2 seconds.", 
      "", 
      "This remains until you take damage." });
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = getLevel(cur);
      if (level != 0)
      {
        if (mineplex.core.common.util.UtilTime.elapsed(this.Factory.Combat().Get(cur).GetLastDamaged(), 12000 - 2000 * level))
        {
          this.Factory.Condition().Factory().Regen(GetName(), cur, cur, 3.9D + 2 * level, 0, false, true, true);
          UtilPlayer.health(cur, 0.5D);
          
          if (Recharge.Instance.use(cur, GetName(), 2000L, false, false)) {
            UtilParticle.PlayParticle(UtilParticle.ParticleType.HEART, cur.getEyeLocation(), 0.0F, 0.2F, 0.0F, 0.0F, 1);
          }
        }
      }
    }
  }
  
  public void Reset(Player player) {}
}
