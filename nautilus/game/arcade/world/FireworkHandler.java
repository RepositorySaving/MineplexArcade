package nautilus.game.arcade.world;

import java.lang.reflect.Method;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class FireworkHandler
{
  private Method world_getHandle = null;
  private Method nms_world_broadcastEntityEffect = null;
  private Method firework_getHandle = null;
  
  public void playFirework(Location loc, FireworkEffect fe) throws Exception
  {
    Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
    
    Object nms_world = null;
    Object nms_firework = null;
    

    if (this.world_getHandle == null)
    {
      this.world_getHandle = getMethod(loc.getWorld().getClass(), "getHandle");
      this.firework_getHandle = getMethod(fw.getClass(), "getHandle");
    }
    
    nms_world = this.world_getHandle.invoke(loc.getWorld(), null);
    nms_firework = this.firework_getHandle.invoke(fw, null);
    
    if (this.nms_world_broadcastEntityEffect == null)
    {
      this.nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
    }
    
    FireworkMeta data = fw.getFireworkMeta();
    data.clearEffects();
    data.setPower(1);
    data.addEffect(fe);
    fw.setFireworkMeta(data);
    
    this.nms_world_broadcastEntityEffect.invoke(nms_world, new Object[] { nms_firework, Byte.valueOf(17) });
    
    fw.remove();
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
  
  public Firework launchFirework(Location loc, FireworkEffect fe, Vector dir) throws Exception
  {
    Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
    
    FireworkMeta data = fw.getFireworkMeta();
    data.clearEffects();
    data.setPower(1);
    data.addEffect(fe);
    fw.setFireworkMeta(data);
    
    fw.setVelocity(dir);
    
    return fw;
  }
  
  public void detonateFirework(Firework fw) throws Exception
  {
    Object nms_world = null;
    Object nms_firework = null;
    

    if (this.world_getHandle == null)
    {
      this.world_getHandle = getMethod(fw.getWorld().getClass(), "getHandle");
      this.firework_getHandle = getMethod(fw.getClass(), "getHandle");
    }
    
    nms_world = this.world_getHandle.invoke(fw.getWorld(), null);
    nms_firework = this.firework_getHandle.invoke(fw, null);
    
    if (this.nms_world_broadcastEntityEffect == null)
    {
      this.nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
    }
    
    this.nms_world_broadcastEntityEffect.invoke(nms_world, new Object[] { nms_firework, Byte.valueOf(17) });
    
    fw.remove();
  }
}
