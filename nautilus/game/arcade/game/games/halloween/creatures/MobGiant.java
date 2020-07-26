package nautilus.game.arcade.game.games.halloween.creatures;

import java.util.HashMap;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.explosion.Explosion;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Giant;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;

public class MobGiant extends CreatureBase<Giant>
{
  private Location _tpLoc;
  
  public MobGiant(Game game, Location loc)
  {
    super(game, null, Giant.class, loc);
  }
  

  public void SpawnCustom(Giant ent)
  {
    this._tpLoc = ent.getLocation();
    
    ent.setMaxHealth(600.0D);
    ent.setHealth(600.0D);
    
    ent.setCustomName("Giant");
  }
  

  public void Damage(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity().equals(GetEntity())) {
      event.SetKnockback(false);
    }
    if (event.GetCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.SUFFOCATION) {
      event.SetCancelled("Suffocation Cancel");
    }
  }
  


  public void Target(EntityTargetEvent event) {}
  


  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.TICK) {
      Move();
    }
    if (event.getType() == UpdateType.SEC) {
      Destroy();
    }
  }
  
  private void Destroy()
  {
    this.Host.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(((Giant)GetEntity()).getLocation().add(0.0D, 8.0D, 0.0D), 6.0D).keySet(), ((Giant)GetEntity()).getLocation().add(0.0D, 8.0D, 0.0D), false);
    this.Host.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(((Giant)GetEntity()).getLocation().add(0.0D, 2.0D, 0.0D), 5.0D).keySet(), ((Giant)GetEntity()).getLocation(), true);
    this.Host.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(((Giant)GetEntity()).getLocation().add(0.0D, 0.0D, 0.0D), 5.0D).keySet(), ((Giant)GetEntity()).getLocation(), true);
  }
  

  private void Move()
  {
    if ((GetTarget() == null) || 
      (UtilMath.offset2d(((Giant)GetEntity()).getLocation(), GetTarget()) < 0.5D) || 
      (mineplex.core.common.util.UtilTime.elapsed(GetTargetTime(), 20000L)))
    {
      SetTarget(GetPlayerTarget());
      return;
    }
    
    if (this._tpLoc == null) {
      this._tpLoc = ((Giant)GetEntity()).getLocation();
    }
    Vector dir = UtilAlg.getTrajectory2d(((Giant)GetEntity()).getLocation(), GetTarget());
    
    this._tpLoc.setPitch(UtilAlg.GetPitch(dir));
    this._tpLoc.setYaw(UtilAlg.GetYaw(dir));
    
    double speed = Math.min(0.35D, 0.1D + ((Giant)GetEntity()).getTicksLived() / 12000.0D);
    

    this._tpLoc.add(dir.multiply(speed));
    

    ((Giant)GetEntity()).teleport(this._tpLoc);
  }
}
