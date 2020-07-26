package nautilus.game.arcade.game.games.dragons;

import java.util.ArrayList;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;



public class DragonData
{
  public Dragons Host;
  public EnderDragon Dragon;
  public Entity TargetEntity = null;
  
  public Location Target = null;
  public Location Location = null;
  
  public float Pitch = 0.0F;
  public Vector Velocity = new Vector(0, 0, 0);
  
  public double RangeBest = 1000.0D;
  public long RangeTime = 0L;
  
  public DragonData(Dragons host, EnderDragon dragon)
  {
    this.Host = host;
    
    this.Dragon = dragon;
    
    this.Velocity = dragon.getLocation().getDirection().setY(0).normalize();
    this.Pitch = UtilAlg.GetPitch(dragon.getLocation().getDirection());
    
    this.Location = dragon.getLocation();
  }
  
  public void Move()
  {
    Turn();
    
    this.Location.add(this.Velocity);
    this.Location.add(0.0D, -this.Pitch, 0.0D);
    
    this.Location.setPitch(-1.0F * this.Pitch);
    this.Location.setYaw(180.0F + UtilAlg.GetYaw(this.Velocity));
    
    this.Dragon.teleport(this.Location);
  }
  

  private void Turn()
  {
    float desiredPitch = UtilAlg.GetPitch(UtilAlg.getTrajectory(this.Location, this.Target));
    if (desiredPitch < this.Pitch) this.Pitch = ((float)(this.Pitch - 0.05D));
    if (desiredPitch > this.Pitch) this.Pitch = ((float)(this.Pitch + 0.05D));
    if (this.Pitch > 0.5D) this.Pitch = 0.5F;
    if (this.Pitch < -0.5D) { this.Pitch = -0.5F;
    }
    
    Vector desired = UtilAlg.getTrajectory2d(this.Location, this.Target);
    desired.subtract(UtilAlg.Normalize(new Vector(this.Velocity.getX(), 0.0D, this.Velocity.getZ())));
    desired.multiply(0.075D);
    
    this.Velocity.add(desired);
    

    UtilAlg.Normalize(this.Velocity);
  }
  
  public void Target()
  {
    if (this.TargetEntity != null)
    {
      if (!this.TargetEntity.isValid())
      {
        this.TargetEntity = null;
      }
      else
      {
        this.Target = this.TargetEntity.getLocation().subtract(0.0D, 8.0D, 0.0D);
      }
      
      return;
    }
    
    if (this.Target == null)
    {
      TargetSky();
    }
    
    if (UtilMath.offset(this.Location, this.Target) < 4.0D)
    {

      if (this.Target.getY() >= this.Host.GetSpectatorLocation().getY())
      {
        TargetPlayer();

      }
      else
      {
        TargetSky();
      }
    }
    
    TargetTimeout();
  }
  
  public void TargetTimeout()
  {
    if (UtilMath.offset(this.Location, this.Target) + 1.0D < this.RangeBest)
    {
      this.RangeTime = System.currentTimeMillis();
      this.RangeBest = UtilMath.offset(this.Location, this.Target);


    }
    else if (UtilTime.elapsed(this.RangeTime, 10000L))
    {
      TargetSky();
    }
  }
  

  public void TargetSky()
  {
    this.RangeBest = 9000.0D;
    this.RangeTime = System.currentTimeMillis();
    
    this.Target = this.Host.GetSpectatorLocation().clone().add(50 - UtilMath.r(100), 20 + UtilMath.r(30), 50 - UtilMath.r(100));
  }
  
  public void TargetPlayer()
  {
    this.RangeBest = 9000.0D;
    this.RangeTime = System.currentTimeMillis();
    
    Player player = (Player)this.Host.GetPlayers(true).get(UtilMath.r(this.Host.GetPlayers(true).size()));
    this.Target = player.getLocation();
    
    this.Target.add(UtilAlg.getTrajectory(this.Location, this.Target).multiply(4));
  }
  
  public void HitByArrow()
  {
    TargetSky();
  }
}
