package mineplex.minecraft.game.classcombat.Skill.Mage;

import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LifeBondsData
{
  private Location _loc;
  private Player _target;
  private double _health;
  
  public LifeBondsData(Location loc, Player target, double amount)
  {
    this._loc = loc;
    this._target = target;
    this._health = amount;
  }
  
  public boolean Update()
  {
    if ((!this._target.isValid()) || (!this._target.isOnline())) {
      return true;
    }
    if (UtilMath.offset(this._loc, this._target.getLocation()) < 1.0D)
    {
      UtilPlayer.health(this._target, this._health);
      return true;
    }
    

    this._loc.add(UtilAlg.getTrajectory(this._loc, this._target.getLocation().add(0.0D, 0.8D, 0.0D)).multiply(0.5D));
    UtilParticle.PlayParticle(UtilParticle.ParticleType.HEART, this._loc, 0.0F, 0.0F, 0.0F, 0.0F, 1);
    
    return false;
  }
}
