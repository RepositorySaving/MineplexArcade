package nautilus.game.arcade.game.games.christmas.content;

import java.util.HashMap;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.explosion.Explosion;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import nautilus.game.arcade.game.games.christmas.parts.Part4;
import org.bukkit.Location;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CaveGiant
{
  private Part4 Host;
  private Giant _ent;
  private Location _target;
  private Location _tpLoc;
  
  public CaveGiant(Part4 host, Location loc)
  {
    this.Host = host;
    
    this.Host.Host.CreatureAllowOverride = true;
    this._ent = ((Giant)loc.getWorld().spawn(loc, Giant.class));
    this.Host.Host.CreatureAllowOverride = false;
    UtilEnt.Vegetate(this._ent);
    UtilEnt.ghost(this._ent, true, false);
    
    this._tpLoc = this._ent.getLocation();
    
    for (Player player : mineplex.core.common.util.UtilServer.getPlayers()) {
      player.playSound(this._ent.getLocation(), org.bukkit.Sound.ZOMBIE_PIG_ANGRY, 10.0F, 0.5F);
    }
  }
  
  public boolean IsDead() {
    return (this._ent != null) && (!this._ent.isValid());
  }
  
  public void SetTarget(Location loc)
  {
    this._target = loc;
  }
  
  public Location GetTarget()
  {
    return this._target;
  }
  
  public Giant GetEntity()
  {
    return this._ent;
  }
  
  public void MoveUpdate()
  {
    if (IsDead()) {
      return;
    }
    Destroy();
    
    SetTarget(this.Host.Host.GetSleigh().GetLocation());
    

    Vector dir = UtilAlg.getTrajectory2d(GetEntity().getLocation(), GetTarget());
    
    this._tpLoc.setPitch(UtilAlg.GetPitch(dir));
    this._tpLoc.setYaw(UtilAlg.GetYaw(dir));
    
    this._tpLoc.add(dir.multiply(0.075D));
    
    GetEntity().teleport(this._tpLoc);
    

    for (Player player : this.Host.Host.GetPlayers(true))
    {
      if (UtilMath.offset(player, this._ent) <= 5.0D)
      {

        if (Recharge.Instance.usable(player, "Giant Damage"))
        {


          this.Host.Host.Manager.GetDamage().NewDamageEvent(player, this._ent, null, 
            org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK, 6.0D, true, false, false, 
            UtilEnt.getName(this._ent), null);
          
          Recharge.Instance.useForce(player, "Giant Damage", 1000L);
        } }
    }
    if (UtilMath.offset(this._ent.getLocation(), this.Host.Host.GetSleigh().GetLocation()) < 8.0D)
    {
      this.Host.Host.End();
    }
  }
  
  private void Destroy()
  {
    this.Host.Host.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(GetEntity().getLocation().add(0.0D, 8.0D, 0.0D), 6.0D).keySet(), GetEntity().getLocation().add(0.0D, 8.0D, 0.0D), false);
    this.Host.Host.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(GetEntity().getLocation().add(0.0D, 2.0D, 0.0D), 5.0D).keySet(), GetEntity().getLocation(), true);
    this.Host.Host.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(GetEntity().getLocation().add(0.0D, 0.0D, 0.0D), 5.0D).keySet(), GetEntity().getLocation(), true);
  }
}
