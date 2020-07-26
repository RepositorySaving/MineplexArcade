package mineplex.core.common.util;

import java.util.Collection;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.Vector;


public class UtilWorld
{
  public static World getWorld(String world)
  {
    return UtilServer.getServer().getWorld(world);
  }
  
  public static String chunkToStr(Chunk chunk)
  {
    if (chunk == null) {
      return "";
    }
    return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
  }
  
  public static String chunkToStrClean(Chunk chunk)
  {
    if (chunk == null) {
      return "";
    }
    return "(" + chunk.getX() + "," + chunk.getZ() + ")";
  }
  
  public static Chunk strToChunk(String string)
  {
    try
    {
      String[] tokens = string.split(",");
      
      return getWorld(tokens[0]).getChunkAt(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
    }
    catch (Exception e) {}
    
    return null;
  }
  

  public static String locToStr(Location loc)
  {
    if (loc == null) {
      return "";
    }
    return 
    

      loc.getWorld().getName() + "," + UtilMath.trim(1, loc.getX()) + "," + UtilMath.trim(1, loc.getY()) + "," + UtilMath.trim(1, loc.getZ());
  }
  
  public static String locToStrClean(Location loc)
  {
    if (loc == null) {
      return "Null";
    }
    return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
  }
  
  public static Location strToLoc(String string)
  {
    if (string.length() == 0) {
      return null;
    }
    String[] tokens = string.split(",");
    
    try
    {
      for (World cur : UtilServer.getServer().getWorlds())
      {
        if (cur.getName().equalsIgnoreCase(tokens[0]))
        {
          return new Location(cur, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
        }
      }
    }
    catch (Exception e)
    {
      return null;
    }
    
    return null;
  }
  
  public static Location locMerge(Location a, Location b)
  {
    a.setX(b.getX());
    a.setY(b.getY());
    a.setZ(b.getZ());
    return a;
  }
  
  public static String envToStr(World.Environment env)
  {
    if (env == World.Environment.NORMAL) return "Overworld";
    if (env == World.Environment.NETHER) return "Nether";
    if (env == World.Environment.THE_END) return "The End";
    return "Unknown";
  }
  
  public static World getWorldType(World.Environment env)
  {
    for (World cur : UtilServer.getServer().getWorlds()) {
      if (cur.getEnvironment() == env)
        return cur;
    }
    return null;
  }
  
  public static Location averageLocation(Collection<Location> locs)
  {
    if (locs.isEmpty()) {
      return null;
    }
    Vector vec = new Vector(0, 0, 0);
    double count = 0.0D;
    
    World world = null;
    
    for (Location spawn : locs)
    {
      count += 1.0D;
      vec.add(spawn.toVector());
      
      world = spawn.getWorld();
    }
    
    vec.multiply(1.0D / count);
    
    return vec.toLocation(world);
  }
}
