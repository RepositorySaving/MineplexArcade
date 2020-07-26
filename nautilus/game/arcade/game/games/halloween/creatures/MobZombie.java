package nautilus.game.arcade.game.games.halloween.creatures;

import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetEvent;

public class MobZombie extends CreatureBase<Zombie> implements InterfaceMove
{
  public MobZombie(Game game, Location loc)
  {
    super(game, null, Zombie.class, loc);
  }
  

  public void SpawnCustom(Zombie ent)
  {
    ent.setCustomName("Zombie");
  }
  



  public void Damage(CustomDamageEvent event) {}
  


  public void Update(UpdateEvent event)
  {
    if (event.getType() == mineplex.core.updater.UpdateType.SLOW) {
      Speed();
    }
  }
  
  public void Move()
  {
    if ((GetTarget() == null) || 
      (UtilMath.offset(((Zombie)GetEntity()).getLocation(), GetTarget()) < 10.0D) || 
      (UtilMath.offset2d(((Zombie)GetEntity()).getLocation(), GetTarget()) < 6.0D) || 
      (mineplex.core.common.util.UtilTime.elapsed(GetTargetTime(), 10000L)))
    {
      SetTarget(GetRoamTarget());
      return;
    }
    

    if (((Zombie)GetEntity()).getTarget() != null)
    {
      if ((UtilMath.offset2d(GetEntity(), ((Zombie)GetEntity()).getTarget()) > 10.0D) || (
        ((((Zombie)GetEntity()).getTarget() instanceof Player)) && (this.Host.IsAlive((Player)((Zombie)GetEntity()).getTarget()))))
      {
        ((Zombie)GetEntity()).setTarget(null);
      }
      

    }
    else {
      DefaultMove();
    }
  }
  


  public void Target(EntityTargetEvent event) {}
  


  public void Speed()
  {
    if (((Zombie)GetEntity()).getTicksLived() > 2400) {
      this.Host.Manager.GetCondition().Factory().Speed("Speed", GetEntity(), GetEntity(), 10.0D, 3, false, false, false);
    } else if (((Zombie)GetEntity()).getTicksLived() > 1800) {
      this.Host.Manager.GetCondition().Factory().Speed("Speed", GetEntity(), GetEntity(), 10.0D, 2, false, false, false);
    } else if (((Zombie)GetEntity()).getTicksLived() > 1200) {
      this.Host.Manager.GetCondition().Factory().Speed("Speed", GetEntity(), GetEntity(), 10.0D, 1, false, false, false);
    } else if (((Zombie)GetEntity()).getTicksLived() > 600) {
      this.Host.Manager.GetCondition().Factory().Speed("Speed", GetEntity(), GetEntity(), 10.0D, 0, false, false, false);
    }
  }
}
