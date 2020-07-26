package mineplex.minecraft.game.classcombat.Skill.Mage;

import mineplex.core.common.util.UtilGear;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MagmaBlade extends Skill
{
  public MagmaBlade(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your sword deals an additional,", 
      "#0.5#0.5 damage to burning opponents,", 
      "but also extinguishes them." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (damagee.getFireTicks() <= 0) {
      return;
    }
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!UtilGear.isSword(damager.getItemInHand())) {
      return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    event.AddMod(damager.getName(), GetName(), 0.5D + 0.5D * level, true);
    

    damager.getWorld().playSound(damager.getLocation(), org.bukkit.Sound.FIZZ, 0.8F, 0.0F);
    
    this.Factory.Condition().EndCondition(damagee, Condition.ConditionType.BURNING, null);
    damagee.setFireTicks(0);
  }
  
  public void Reset(Player player) {}
}
