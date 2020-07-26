package mineplex.minecraft.game.core.condition.conditions;

import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;



public class Cloak
  extends Condition
{
  public Cloak(ConditionManager manager, String reason, LivingEntity ent, LivingEntity source, Condition.ConditionType type, int mult, int ticks, boolean add, Material visualType, byte visualData, boolean showIndicator)
  {
    super(manager, reason, ent, source, type, mult, ticks, add, visualType, visualData, showIndicator, false);
    
    this._informOn = "You are now invisible.";
    this._informOff = "You are no longer invisible.";
  }
  

  public void Add()
  {
    if (!(this._ent instanceof Player)) {
      return;
    }
    for (Player other : this._ent.getServer().getOnlinePlayers())
    {
      other.hidePlayer((Player)this._ent);
    }
    
    for (Entity ent : this._ent.getWorld().getEntities())
    {
      if ((ent instanceof Creature))
      {

        Creature creature = (Creature)ent;
        
        if ((creature.getTarget() == null) || (creature.getTarget().equals(this._ent)))
        {

          creature.setTarget(null);
        }
      }
    }
  }
  
  public void Remove() {
    super.Remove();
    
    for (Player other : this._ent.getServer().getOnlinePlayers())
    {
      other.hidePlayer((Player)this._ent);
      other.showPlayer((Player)this._ent);
    }
  }
}
