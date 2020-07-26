package mineplex.core.common.util;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;





public class UtilAlg
{
  public static TreeSet<String> sortKey(Set<String> toSort)
  {
    TreeSet<String> sortedSet = new TreeSet();
    for (String cur : toSort) {
      sortedSet.add(cur);
    }
    return sortedSet;
  }
  
  public static Vector getTrajectory(Entity from, Entity to)
  {
    return getTrajectory(from.getLocation().toVector(), to.getLocation().toVector());
  }
  
  public static Vector getTrajectory(Location from, Location to)
  {
    return getTrajectory(from.toVector(), to.toVector());
  }
  
  public static Vector getTrajectory(Vector from, Vector to)
  {
    return to.subtract(from).normalize();
  }
  
  public static Vector getTrajectory2d(Entity from, Entity to)
  {
    return getTrajectory2d(from.getLocation().toVector(), to.getLocation().toVector());
  }
  
  public static Vector getTrajectory2d(Location from, Location to)
  {
    return getTrajectory2d(from.toVector(), to.toVector());
  }
  
  public static Vector getTrajectory2d(Vector from, Vector to)
  {
    return to.subtract(from).setY(0).normalize();
  }
  
  public static boolean HasSight(Location from, Player to)
  {
    return (HasSight(from, to.getLocation())) || (HasSight(from, to.getEyeLocation()));
  }
  

  public static boolean HasSight(Location from, Location to)
  {
    Location cur = new Location(from.getWorld(), from.getX(), from.getY(), from.getZ());
    
    double rate = 0.1D;
    Vector vec = getTrajectory(from, to).multiply(0.1D);
    
    while (UtilMath.offset(cur, to) > rate)
    {
      cur.add(vec);
      
      if (!UtilBlock.airFoliage(cur.getBlock())) {
        return false;
      }
    }
    return true;
  }
  
  public static float GetPitch(Vector vec)
  {
    double x = vec.getX();
    double y = vec.getY();
    double z = vec.getZ();
    double xz = Math.sqrt(x * x + z * z);
    
    double pitch = Math.toDegrees(Math.atan(xz / y));
    if (y <= 0.0D) pitch += 90.0D; else {
      pitch -= 90.0D;
    }
    return (float)pitch;
  }
  
  public static float GetYaw(Vector vec)
  {
    double x = vec.getX();
    double z = vec.getZ();
    
    double yaw = Math.toDegrees(Math.atan(-x / z));
    if (z < 0.0D) { yaw += 180.0D;
    }
    return (float)yaw;
  }
  
  public static Vector Normalize(Vector vec)
  {
    if (vec.length() > 0.0D) {
      vec.normalize();
    }
    return vec;
  }
  
  public static Vector Clone(Vector vec)
  {
    return new Vector(vec.getX(), vec.getY(), vec.getZ());
  }
  
  public static <T> T Random(List<T> list)
  {
    return list.get(UtilMath.r(list.size()));
  }
}
