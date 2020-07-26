package nautilus.game.arcade.game.games.halloween.creatures;

import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

public class MobSpiderLeaper extends CreatureBase<CaveSpider> implements InterfaceMove
{
  public MobSpiderLeaper(Game game, Location loc)
  {
    super(game, null, CaveSpider.class, loc);
  }
  

  public void SpawnCustom(CaveSpider ent)
  {
    ent.setCustomName("Leaping Spider");
    
    this.Host.Manager.GetCondition().Factory().Speed("Speed", GetEntity(), GetEntity(), 99999.0D, 1, false, false, false);
  }
  



  public void Damage(CustomDamageEvent event) {}
  



  public void Target(EntityTargetEvent event) {}
  



  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.SEC) {
      Leap();
    }
  }
  
  private void Leap() {
    if (GetTarget() == null) {
      return;
    }
    if (Math.random() > 0.5D) {
      return;
    }
    if (!UtilEnt.isGrounded(GetEntity())) {
      return;
    }
    if (((CaveSpider)GetEntity()).getTarget() != null) {
      UtilAction.velocity(GetEntity(), UtilAlg.getTrajectory2d(GetEntity(), ((CaveSpider)GetEntity()).getTarget()), 1.0D, true, 0.6D, 0.0D, 10.0D, true);
    } else {
      UtilAction.velocity(GetEntity(), UtilAlg.getTrajectory2d(((CaveSpider)GetEntity()).getLocation(), GetTarget()), 1.0D, true, 0.6D, 0.0D, 10.0D, true);
    }
  }
  
  public void Move()
  {
    if ((GetTarget() == null) || 
      (UtilMath.offset(((CaveSpider)GetEntity()).getLocation(), GetTarget()) < 10.0D) || 
      (UtilMath.offset2d(((CaveSpider)GetEntity()).getLocation(), GetTarget()) < 6.0D) || 
      (mineplex.core.common.util.UtilTime.elapsed(GetTargetTime(), 10000L)))
    {
      SetTarget(GetRoamTarget());
      return;
    }
    

    if (((CaveSpider)GetEntity()).getTarget() != null)
    {
      if ((UtilMath.offset2d(GetEntity(), ((CaveSpider)GetEntity()).getTarget()) > 10.0D) || (
        ((((CaveSpider)GetEntity()).getTarget() instanceof Player)) && (this.Host.IsAlive((Player)((CaveSpider)GetEntity()).getTarget()))))
      {
        ((CaveSpider)GetEntity()).setTarget(null);
      }
      

    }
    else {
      DefaultMove();
    }
  }
}
