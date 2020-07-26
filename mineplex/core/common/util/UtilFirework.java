package mineplex.core.common.util;

import java.lang.reflect.Method;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class UtilFirework
{
  private static Method world_getHandle = null;
  private static Method nms_world_broadcastEntityEffect = null;
  private static Method firework_getHandle = null;
  
  public static void playFirework(Location loc, FireworkEffect fe)
  {
    try
    {
      Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
      
      Object nms_world = null;
      Object nms_firework = null;
      

      if (world_getHandle == null)
      {
        world_getHandle = getMethod(loc.getWorld().getClass(), "getHandle");
        firework_getHandle = getMethod(fw.getClass(), "getHandle");
      }
      
      nms_world = world_getHandle.invoke(loc.getWorld(), null);
      nms_firework = firework_getHandle.invoke(fw, null);
      
      if (nms_world_broadcastEntityEffect == null)
      {
        nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
      }
      
      FireworkMeta data = fw.getFireworkMeta();
      data.clearEffects();
      data.setPower(1);
      data.addEffect(fe);
      fw.setFireworkMeta(data);
      
      nms_world_broadcastEntityEffect.invoke(nms_world, new Object[] { nms_firework, Byte.valueOf(17) });
      
      fw.remove();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private static Method getMethod(Class<?> cl, String method)
  {
    for (Method m : cl.getMethods())
    {
      if (m.getName().equals(method))
      {
        return m;
      }
    }
    return null;
  }
  
  public static Firework launchFirework(Location loc, FireworkEffect fe, Vector dir, int power)
  {
    try
    {
      Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
      
      FireworkMeta data = fw.getFireworkMeta();
      data.clearEffects();
      data.setPower(power);
      data.addEffect(fe);
      fw.setFireworkMeta(data);
      
      if (dir != null) {
        fw.setVelocity(dir);
      }
      return fw;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public void detonateFirework(Firework fw)
  {
    try
    {
      Object nms_world = null;
      Object nms_firework = null;
      

      if (world_getHandle == null)
      {
        world_getHandle = getMethod(fw.getWorld().getClass(), "getHandle");
        firework_getHandle = getMethod(fw.getClass(), "getHandle");
      }
      
      nms_world = world_getHandle.invoke(fw.getWorld(), null);
      nms_firework = firework_getHandle.invoke(fw, null);
      
      if (nms_world_broadcastEntityEffect == null)
      {
        nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
      }
      
      nms_world_broadcastEntityEffect.invoke(nms_world, new Object[] { nms_firework, Byte.valueOf(17) });
      
      fw.remove();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
