package mineplex.minecraft.game.core.condition.conditions;

import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;




public class Silence
  extends Condition
{
  public Silence(ConditionManager manager, String reason, LivingEntity ent, LivingEntity source, Condition.ConditionType type, int mult, int ticks, boolean add, Material visualType, byte visualData, boolean showIndicator)
  {
    super(manager, reason, ent, source, type, mult, ticks, add, visualType, visualData, showIndicator, false);
  }
  

  public void Add()
  {
    if ((this._ent instanceof Player)) {
      ((Player)this._ent).playSound(this._ent.getLocation(), Sound.BAT_HURT, 0.8F, 0.8F);
    }
  }
  
  public void Remove() {}
}
