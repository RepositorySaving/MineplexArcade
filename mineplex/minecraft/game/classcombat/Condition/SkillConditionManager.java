package mineplex.minecraft.game.classcombat.Condition;

import mineplex.minecraft.game.core.condition.ConditionEffect;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillConditionManager
  extends ConditionManager
{
  public SkillConditionManager(JavaPlugin plugin)
  {
    super(plugin);
  }
  
  public ConditionEffect Effect()
  {
    if (this.Effect == null) {
      this.Effect = new SkillConditionEffect(this);
    }
    return this.Effect;
  }
}
