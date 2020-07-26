package mineplex.core.common.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class UtilAction
{
  public static void velocity(Entity ent, double str, double yAdd, double yMax, boolean groundBoost)
  {
    velocity(ent, ent.getLocation().getDirection(), str, false, 0.0D, yAdd, yMax, groundBoost);
  }
  
  public static void velocity(Entity ent, Vector vec, double str, boolean ySet, double yBase, double yAdd, double yMax, boolean groundBoost)
  {
    if ((Double.isNaN(vec.getX())) || (Double.isNaN(vec.getY())) || (Double.isNaN(vec.getZ())) || (vec.length() == 0.0D)) {
      return;
    }
    
    if (ySet) {
      vec.setY(yBase);
    }
    
    vec.normalize();
    vec.multiply(str);
    

    vec.setY(vec.getY() + yAdd);
    

    if (vec.getY() > yMax) {
      vec.setY(yMax);
    }
    if ((groundBoost) && 
      (UtilEnt.isGrounded(ent))) {
      vec.setY(vec.getY() + 0.2D);
    }
    
    ent.setFallDistance(0.0F);
    ent.setVelocity(vec);
  }
}
