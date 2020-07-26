package nautilus.game.arcade.game.games.halloween.creatures;

import mineplex.core.common.util.UtilMath;
import mineplex.core.disguise.disguises.DisguisePigZombie;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetEvent;

public class MobPigZombie extends CreatureBase<Zombie> implements InterfaceMove
{
  public MobPigZombie(Game game, Location loc)
  {
    super(game, null, Zombie.class, loc);
  }
  

  public void SpawnCustom(Zombie ent)
  {
    DisguisePigZombie disguise = new DisguisePigZombie(ent);
    this.Host.Manager.GetDisguise().disguise(disguise);
    
    this.Host.Manager.GetCondition().Factory().Speed("Speed", ent, ent, 99999.0D, 1, false, false, false);
    
    ent.setCustomName("Nether Zombie");
  }
  




  public void Damage(CustomDamageEvent event) {}
  



  public void Target(EntityTargetEvent event) {}
  



  public void Update(UpdateEvent event) {}
  



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
}
