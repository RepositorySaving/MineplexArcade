package mineplex.minecraft.game.classcombat.Condition;

import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionEffect;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class SkillConditionEffect
  extends ConditionEffect
{
  public SkillConditionEffect(ConditionManager manager)
  {
    super(manager);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Silence(SkillTriggerEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (!this.Manager.IsSilenced(event.GetPlayer(), event.GetSkillName())) {
      return;
    }
    
    event.SetCancelled(true);
  }
}
