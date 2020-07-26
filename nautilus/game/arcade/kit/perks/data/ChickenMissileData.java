package nautilus.game.arcade.kit.perks.data;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ChickenMissileData
{
  public org.bukkit.entity.Player Player;
  public Entity Chicken;
  public Vector Direction;
  public long Time;
  public double LastX;
  public double LastY;
  public double LastZ;
  
  public ChickenMissileData(org.bukkit.entity.Player player, Entity chicken)
  {
    this.Player = player;
    this.Chicken = chicken;
    this.Direction = player.getLocation().getDirection().multiply(0.6D);
    this.Time = System.currentTimeMillis();
  }
  

  public boolean HasHitBlock()
  {
    if ((this.LastX != 0.0D) && (this.LastY != 0.0D) && (this.LastZ != 0.0D))
    {
      if (Math.abs(this.Chicken.getLocation().getX() - this.LastX) < Math.abs(this.Direction.getX() / 10.0D))
      {
        return true;
      }
      if (Math.abs(this.Chicken.getLocation().getY() - this.LastY) < Math.abs(this.Direction.getY() / 10.0D))
      {
        if ((this.Direction.getY() > 0.0D) || (-0.02D > this.Direction.getY()))
        {
          return true;
        }
      }
      if (Math.abs(this.Chicken.getLocation().getZ() - this.LastZ) < Math.abs(this.Direction.getZ() / 10.0D))
      {
        return true;
      }
    }
    
    this.LastX = this.Chicken.getLocation().getX();
    this.LastY = this.Chicken.getLocation().getY();
    this.LastZ = this.Chicken.getLocation().getZ();
    
    return false;
  }
}
