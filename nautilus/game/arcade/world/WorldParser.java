package nautilus.game.arcade.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.UtilWorld;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

public class WorldParser
{
  public void Parse(Player caller, String[] args)
  {
    HashSet<Integer> dataId = new HashSet();
    
    if (args != null) {
      for (String arg : args)
      {
        try
        {
          dataId.add(Integer.valueOf(Integer.parseInt(arg)));
        }
        catch (Exception e)
        {
          caller.sendMessage("Invalid Data ID: " + arg);
        }
      }
    }
    HashMap<String, ArrayList<Location>> TeamLocs = new HashMap();
    Object DataLocs = new HashMap();
    Object CustomLocs = new HashMap();
    
    Location cornerA = null;
    Location cornerB = null;
    
    int processed = 0;
    
    caller.sendMessage("Scanning for Blocks...");
    Block block; for (int x = -600; x < 600; x++)
      for (int z = -600; z < 600; z++)
        for (int y = 0; y < 256; y++)
        {
          processed++;
          if (processed % 20000000 == 0) {
            caller.sendMessage("Processed: " + processed);
          }
          block = caller.getWorld().getBlockAt(caller.getLocation().getBlockX() + x, caller.getLocation().getBlockY() + y, caller.getLocation().getBlockZ() + z);
          

          if (dataId.contains(Integer.valueOf(block.getTypeId())))
          {
            String key = block.getTypeId();
            
            if (!((HashMap)CustomLocs).containsKey(key)) {
              ((HashMap)CustomLocs).put(key, new ArrayList());
            }
            ((ArrayList)((HashMap)CustomLocs).get(key)).add(block.getLocation());

          }
          else
          {
            if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN))
            {
              if (block.getRelative(BlockFace.DOWN).getType() == Material.SPONGE)
              {
                Sign s = (Sign)block.getState();
                
                String name = "";
                
                try
                {
                  name = s.getLine(0);
                }
                catch (Exception e)
                {
                  caller.sendMessage("Invalid Sign Data: " + UtilWorld.locToStr(block.getLocation()));
                }
                

                if (!((HashMap)CustomLocs).containsKey(name)) {
                  ((HashMap)CustomLocs).put(name, new ArrayList());
                }
                ((ArrayList)((HashMap)CustomLocs).get(name)).add(block.getRelative(BlockFace.DOWN).getLocation());
                

                block.setTypeId(0);
                block.getRelative(BlockFace.DOWN).setTypeId(0);
              }
            }
            

            if (block.getTypeId() == 147)
            {
              Block wool = block.getRelative(BlockFace.DOWN);
              if (wool == null) {
                continue;
              }
              if (wool.getType() == Material.WOOL)
              {
                if (wool.getData() == 14)
                {
                  if (!TeamLocs.containsKey("Red")) {
                    TeamLocs.put("Red", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Red")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 4)
                {
                  if (!TeamLocs.containsKey("Yellow")) {
                    TeamLocs.put("Yellow", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Yellow")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 13)
                {
                  if (!TeamLocs.containsKey("Green")) {
                    TeamLocs.put("Green", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Green")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 11)
                {
                  if (!TeamLocs.containsKey("Blue")) {
                    TeamLocs.put("Blue", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Blue")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 1)
                {
                  if (!TeamLocs.containsKey("Orange")) {
                    TeamLocs.put("Orange", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Orange")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 2)
                {
                  if (!TeamLocs.containsKey("Magenta")) {
                    TeamLocs.put("Magenta", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Magenta")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 3)
                {
                  if (!TeamLocs.containsKey("Sky")) {
                    TeamLocs.put("Sky", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Sky")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 5)
                {
                  if (!TeamLocs.containsKey("Lime")) {
                    TeamLocs.put("Lime", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Lime")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 6)
                {
                  if (!TeamLocs.containsKey("Pink")) {
                    TeamLocs.put("Pink", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Pink")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 7)
                {
                  if (!TeamLocs.containsKey("Gray")) {
                    TeamLocs.put("Gray", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Gray")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 8)
                {
                  if (!TeamLocs.containsKey("LGray")) {
                    TeamLocs.put("LGray", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("LGray")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 9)
                {
                  if (!TeamLocs.containsKey("Cyan")) {
                    TeamLocs.put("Cyan", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Cyan")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 10)
                {
                  if (!TeamLocs.containsKey("Purple")) {
                    TeamLocs.put("Purple", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Purple")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 11)
                {
                  if (!TeamLocs.containsKey("DBlue")) {
                    TeamLocs.put("DBlue", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("DBlue")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 12)
                {
                  if (!TeamLocs.containsKey("Brown")) {
                    TeamLocs.put("Brown", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Brown")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 15)
                {
                  if (!TeamLocs.containsKey("Black")) {
                    TeamLocs.put("Black", new ArrayList());
                  }
                  ((ArrayList)TeamLocs.get("Black")).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
                
                if (wool.getData() == 0)
                {
                  if (cornerA == null) { cornerA = wool.getLocation();
                  } else if (cornerB == null) cornerB = wool.getLocation(); else {
                    caller.sendMessage("More than 2 Corner Locations found!");
                  }
                  
                  block.setTypeId(0);
                  wool.setTypeId(0);
                }
              }
            }
            
            if (block.getTypeId() == 148)
            {

              Block wool = block.getRelative(BlockFace.DOWN);
              if (wool != null)
              {

                if (wool.getType() == Material.WOOL)
                {

                  Wool woolData = new Wool(wool.getType(), wool.getData());
                  
                  String dataType = woolData.getColor().name();
                  
                  if (!((HashMap)DataLocs).containsKey(dataType)) {
                    ((HashMap)DataLocs).put(dataType, new ArrayList());
                  }
                  ((ArrayList)((HashMap)DataLocs).get(dataType)).add(wool.getLocation());
                  

                  block.setTypeId(0);
                  wool.setTypeId(0);
                } }
            } } }
    if ((cornerA == null) || (cornerB == null))
    {
      caller.sendMessage("Missing Corner Locations!");
      return;
    }
    

    try
    {
      FileWriter fstream = new FileWriter(caller.getWorld().getName() + File.separator + "WorldConfig.dat");
      BufferedWriter out = new BufferedWriter(fstream);
      
      out.write("MAP_NAME:");
      out.write("\n");
      out.write("MAP_AUTHOR:");
      out.write("\n");
      out.write("\n");
      out.write("MIN_X:" + Math.min(cornerA.getBlockX(), cornerB.getBlockX()));
      out.write("\n");
      out.write("MAX_X:" + Math.max(cornerA.getBlockX(), cornerB.getBlockX()));
      out.write("\n");
      out.write("MIN_Z:" + Math.min(cornerA.getBlockZ(), cornerB.getBlockZ()));
      out.write("\n");
      out.write("MAX_Z:" + Math.max(cornerA.getBlockZ(), cornerB.getBlockZ()));
      out.write("\n");
      out.write("\n");
      if (cornerA.getBlockY() == cornerB.getBlockY())
      {
        out.write("MIN_Y:0");
        out.write("\n");
        out.write("MAX_Y:256");
      }
      else
      {
        out.write("MIN_Y:" + Math.min(cornerA.getBlockY(), cornerB.getBlockY()));
        out.write("\n");
        out.write("MAX_Y:" + Math.max(cornerA.getBlockY(), cornerB.getBlockY()));
      }
      

      for (String team : TeamLocs.keySet())
      {
        out.write("\n");
        out.write("\n");
        out.write("TEAM_NAME:" + team);
        out.write("\n");
        out.write("TEAM_SPAWNS:" + LocationsToString((ArrayList)TeamLocs.get(team)));
      }
      

      for (String data : ((HashMap)DataLocs).keySet())
      {
        out.write("\n");
        out.write("\n");
        out.write("DATA_NAME:" + data);
        out.write("\n");
        out.write("DATA_LOCS:" + LocationsToString((ArrayList)((HashMap)DataLocs).get(data)));
      }
      

      for (String data : ((HashMap)CustomLocs).keySet())
      {
        out.write("\n");
        out.write("\n");
        out.write("CUSTOM_NAME:" + data);
        out.write("\n");
        out.write("CUSTOM_LOCS:" + LocationsToString((ArrayList)((HashMap)CustomLocs).get(data)));
      }
      
      out.close();
    }
    catch (Exception e)
    {
      caller.sendMessage("Error: File Write Error");
    }
    

    caller.sendMessage("World Data Saved.");
  }
  
  public String LocationsToString(ArrayList<Location> locs)
  {
    String out = "";
    
    for (Location loc : locs) {
      out = out + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ":";
    }
    return out;
  }
  
  public String LocationSignsToString(HashMap<Location, String> locs)
  {
    String out = "";
    
    for (Location loc : locs.keySet()) {
      out = out + (String)locs.get(loc) + "@" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ":";
    }
    return out;
  }
}
