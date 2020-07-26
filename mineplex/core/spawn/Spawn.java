package mineplex.core.spawn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.spawn.command.SpawnCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Spawn extends MiniPlugin
{
  public ArrayList<Location> spawnList;
  
  public Spawn(JavaPlugin plugin)
  {
    super("Spawn", plugin);
    
    ReadSpawns();
  }
  

  public void AddCommands()
  {
    AddCommand(new SpawnCommand(this));
  }
  
  public Location getSpawn()
  {
    if (this.spawnList.isEmpty()) {
      return UtilServer.getServer().getWorld("world").getSpawnLocation();
    }
    return (Location)this.spawnList.get(UtilMath.r(this.spawnList.size()));
  }
  

  public void AddSpawn(Player player)
  {
    Location loc = player.getLocation();
    

    player.getWorld().setSpawnLocation((int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
    

    this.spawnList.add(loc);
    

    WriteSpawns();
    

    UtilPlayer.message(player, F.main(this._moduleName, "You added a Spawn Node."));
    

    Log("Added Spawn [" + UtilWorld.locToStr(loc) + "] by [" + player.getName() + "].");
  }
  

  public void ClearSpawn(Player player)
  {
    this.spawnList.clear();
    

    WriteSpawns();
    

    UtilPlayer.message(player, F.main(this._moduleName, "You cleared all Spawn Nodes."));
    

    Log("Cleared Spawn [ALL] by [" + player.getName() + "].");
  }
  
  @EventHandler
  public void handleRespawn(PlayerRespawnEvent event)
  {
    event.setRespawnLocation(getSpawn());
  }
  
  private void ReadSpawns()
  {
    this.spawnList = new ArrayList();
    
    FileInputStream fstream = null;
    DataInputStream in = null;
    BufferedReader br = null;
    
    if (!new File("data/spawns.dat").exists()) {
      return;
    }
    try
    {
      fstream = new FileInputStream("data/spawns.dat");
      in = new DataInputStream(fstream);
      br = new BufferedReader(new InputStreamReader(in));
      String strLine = br.readLine();
      
      while (strLine != null)
      {
        System.out.println(strLine);
        try
        {
          Location spawn = UtilWorld.strToLoc(strLine);
          this.spawnList.add(spawn);
        }
        catch (Exception localException1) {}
        



        strLine = br.readLine();
      }
    }
    catch (Exception e)
    {
      System.err.println("Spawn Read Error: " + e.getMessage());
      e.printStackTrace();
      


      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void WriteSpawns()
  {
    FileWriter fstream = null;
    BufferedWriter out = null;
    
    try
    {
      fstream = new FileWriter("data/spawns.dat");
      out = new BufferedWriter(fstream);
      
      for (Location loc : this.spawnList)
      {
        out.write(UtilWorld.locToStr(loc) + "\n");
      }
      
      out.close();
    }
    catch (Exception e)
    {
      System.err.println("Spawn Write Error: " + e.getMessage());
      


      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
}
