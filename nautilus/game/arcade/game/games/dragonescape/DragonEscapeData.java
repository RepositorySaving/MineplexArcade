package nautilus.game.arcade.game.games.dragonescape;

import mineplex.core.common.util.UtilAlg;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.util.Vector;





public class DragonEscapeData
{
  public DragonEscape Host;
  public EnderDragon Dragon;
  public Location Target = null;
  public Location Location = null;
  
  public float Pitch = 0.0F;
  public Vector Velocity = new Vector(0, 0, 0);
  
  public DragonEscapeData(DragonEscape host, EnderDragon dragon, Location target)
  {
    this.Host = host;
    
    this.Dragon = dragon;
    
    Location temp = dragon.getLocation();
    temp.setPitch(UtilAlg.GetPitch(UtilAlg.getTrajectory(dragon.getLocation(), target)));
    dragon.teleport(temp);
    
    this.Velocity = dragon.getLocation().getDirection().setY(0).normalize();
    this.Pitch = UtilAlg.GetPitch(dragon.getLocation().getDirection());
    
    this.Location = dragon.getLocation();
  }
  
  public void Move()
  {
    Turn();
    

    double speed = 0.2D;
    















    speed *= this.Host.GetSpeedMult();
    
    this.Location.add(this.Velocity.clone().multiply(speed));
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
    desired.multiply(0.2D);
    
    this.Velocity.add(desired);
    

    UtilAlg.Normalize(this.Velocity);
  }
}
