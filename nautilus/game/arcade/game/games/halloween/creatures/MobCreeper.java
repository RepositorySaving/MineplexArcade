package nautilus.game.arcade.game.games.halloween.creatures;

import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

public class MobCreeper
  extends CreatureBase<Creeper> implements InterfaceMove
{
  public MobCreeper(Game game, Location loc)
  {
    super(game, null, Creeper.class, loc);
  }
  

  public void SpawnCustom(Creeper ent)
  {
    ent.setCustomName("Creeper");
  }
  




  public void Damage(CustomDamageEvent event) {}
  



  public void Target(EntityTargetEvent event) {}
  



  public void Update(UpdateEvent event) {}
  



  public void Move()
  {
    if ((GetTarget() == null) || 
      (UtilMath.offset(((Creeper)GetEntity()).getLocation(), GetTarget()) < 10.0D) || 
      (UtilMath.offset2d(((Creeper)GetEntity()).getLocation(), GetTarget()) < 6.0D) || 
      (UtilTime.elapsed(GetTargetTime(), 10000L)))
    {
      SetTarget(GetRoamTarget());
      return;
    }
    

    if (((Creeper)GetEntity()).getTarget() != null)
    {
      if ((UtilMath.offset2d(GetEntity(), ((Creeper)GetEntity()).getTarget()) > 10.0D) || (
        ((((Creeper)GetEntity()).getTarget() instanceof Player)) && (this.Host.IsAlive((Player)((Creeper)GetEntity()).getTarget()))))
      {
        ((Creeper)GetEntity()).setTarget(null);
      }
      

    }
    else {
      DefaultMove();
    }
  }
}
