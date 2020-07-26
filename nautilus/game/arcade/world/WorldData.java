package nautilus.game.arcade.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.WorldUtil;
import mineplex.core.common.util.ZipUtil;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.managers.GameCreationManager;
import nautilus.game.arcade.managers.GameWorldManager;
import net.minecraft.server.v1_7_R3.ChunkPreLoadEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class WorldData
{
  public Game Host;
  public int Id = -1;
  
  public String File = null;
  public String Folder = null;
  
  public World World;
  public int MinX = 0;
  public int MinZ = 0;
  public int MaxX = 0;
  public int MaxZ = 0;
  public int CurX = 0;
  public int CurZ = 0;
  
  public int MinY = -1;
  public int MaxY = 256;
  
  public String MapName = "Null";
  public String MapAuthor = "Null";
  
  public HashMap<String, ArrayList<Location>> SpawnLocs = new HashMap();
  private HashMap<String, ArrayList<Location>> DataLocs = new HashMap();
  private HashMap<String, ArrayList<Location>> CustomLocs = new HashMap();
  
  public WorldData(Game game)
  {
    this.Host = game;
    
    Initialize();
    
    this.Id = GetNewId();
  }
  
  public void Initialize()
  {
    final WorldData worldData = this;
    
    UtilServer.getServer().getScheduler().runTaskAsynchronously(this.Host.Manager.GetPlugin(), new Runnable()
    {

      public void run()
      {
        worldData.UnzipWorld();
        

        UtilServer.getServer().getScheduler().runTask(WorldData.this.Host.Manager.GetPlugin(), new Runnable()
        {

          public void run()
          {
            WorldData.this.World = WorldUtil.LoadWorld(new org.bukkit.WorldCreator(WorldData.this.GetFolder()));
            WorldData.this.World.setDifficulty(org.bukkit.Difficulty.HARD);
            

            this.val$worldData.LoadWorldConfig();
          }
        });
      }
    });
  }
  
  protected String GetFile()
  {
    if (this.File == null)
    {
      this.File = ((String)this.Host.GetFiles().get(UtilMath.r(this.Host.GetFiles().size())));
      

      if (this.Host.GetFiles().size() > 1)
      {
        while (this.File.equals(this.Host.Manager.GetGameCreationManager().GetLastMap()))
        {
          this.File = ((String)this.Host.GetFiles().get(UtilMath.r(this.Host.GetFiles().size())));
        }
      }
    }
    
    this.Host.Manager.GetGameCreationManager().SetLastMap(this.File);
    
    return this.File;
  }
  
  protected String GetFolder()
  {
    if (this.Folder == null) {
      this.Folder = ("Game" + this.Id + "_" + this.Host.GetName() + "_" + GetFile());
    }
    return this.Folder;
  }
  
  protected void UnzipWorld()
  {
    String folder = GetFolder();
    new File(folder).mkdir();
    new File(folder + File.separator + "region").mkdir();
    new File(folder + File.separator + "data").mkdir();
    ZipUtil.UnzipToDirectory("../../update/maps/" + this.Host.GetName() + "/" + GetFile() + ".zip", folder);
  }
  

  public void LoadWorldConfig()
  {
    String line = null;
    
    try
    {
      FileInputStream fstream = new FileInputStream(GetFolder() + File.separator + "WorldConfig.dat");
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      
      ArrayList<Location> currentTeam = null;
      ArrayList<Location> currentData = null;
      
      int currentDirection = 0;
      
      while ((line = br.readLine()) != null)
      {
        String[] tokens = line.split(":");
        
        if (tokens.length >= 2)
        {

          if (tokens[0].length() != 0)
          {


            if (tokens[0].equalsIgnoreCase("MAP_NAME"))
            {
              this.MapName = tokens[1];
            }
            else if (tokens[0].equalsIgnoreCase("MAP_AUTHOR"))
            {
              this.MapAuthor = tokens[1];


            }
            else if (tokens[0].equalsIgnoreCase("TEAM_NAME"))
            {
              this.SpawnLocs.put(tokens[1], new ArrayList());
              currentTeam = (ArrayList)this.SpawnLocs.get(tokens[1]);
              currentDirection = 0;
            }
            else if (tokens[0].equalsIgnoreCase("TEAM_DIRECTION"))
            {
              currentDirection = Integer.parseInt(tokens[1]);
            }
            else if (tokens[0].equalsIgnoreCase("TEAM_SPAWNS"))
            {
              for (int i = 1; i < tokens.length; i++)
              {
                Location loc = StrToLoc(tokens[i]);
                if (loc != null)
                {
                  loc.setYaw(currentDirection);
                  
                  currentTeam.add(loc);
                }
                
              }
            }
            else if (tokens[0].equalsIgnoreCase("DATA_NAME"))
            {
              this.DataLocs.put(tokens[1], new ArrayList());
              currentData = (ArrayList)this.DataLocs.get(tokens[1]);
            }
            else if (tokens[0].equalsIgnoreCase("DATA_LOCS"))
            {
              for (int i = 1; i < tokens.length; i++)
              {
                Location loc = StrToLoc(tokens[i]);
                if (loc != null)
                {
                  currentData.add(loc);
                }
                
              }
            }
            else if (tokens[0].equalsIgnoreCase("CUSTOM_NAME"))
            {
              this.CustomLocs.put(tokens[1], new ArrayList());
              currentData = (ArrayList)this.CustomLocs.get(tokens[1]);
            }
            else if (tokens[0].equalsIgnoreCase("CUSTOM_LOCS"))
            {
              for (int i = 1; i < tokens.length; i++)
              {
                Location loc = StrToLoc(tokens[i]);
                if (loc != null)
                {
                  currentData.add(loc);
                }
                
              }
            }
            else if (tokens[0].equalsIgnoreCase("MIN_X"))
            {
              try
              {
                this.MinX = Integer.parseInt(tokens[1]);
                this.CurX = this.MinX;
              }
              catch (Exception e)
              {
                System.out.println("World Data Read Error: Invalid MinX [" + tokens[1] + "]");
              }
              
            }
            else if (tokens[0].equalsIgnoreCase("MAX_X"))
            {
              try
              {
                this.MaxX = Integer.parseInt(tokens[1]);
              }
              catch (Exception e)
              {
                System.out.println("World Data Read Error: Invalid MaxX [" + tokens[1] + "]");
              }
            }
            else if (tokens[0].equalsIgnoreCase("MIN_Z"))
            {
              try
              {
                this.MinZ = Integer.parseInt(tokens[1]);
                this.CurZ = this.MinZ;
              }
              catch (Exception e)
              {
                System.out.println("World Data Read Error: Invalid MinZ [" + tokens[1] + "]");
              }
            }
            else if (tokens[0].equalsIgnoreCase("MAX_Z"))
            {
              try
              {
                this.MaxZ = Integer.parseInt(tokens[1]);
              }
              catch (Exception e)
              {
                System.out.println("World Data Read Error: Invalid MaxZ [" + tokens[1] + "]");
              }
            }
            else if (tokens[0].equalsIgnoreCase("MIN_Y"))
            {
              try
              {
                this.MinY = Integer.parseInt(tokens[1]);
              }
              catch (Exception e)
              {
                System.out.println("World Data Read Error: Invalid MinY [" + tokens[1] + "]");
              }
            }
            else if (tokens[0].equalsIgnoreCase("MAX_Y"))
            {
              try
              {
                this.MaxY = Integer.parseInt(tokens[1]);
              }
              catch (Exception e)
              {
                System.out.println("World Data Read Error: Invalid MaxY [" + tokens[1] + "]");
              }
            } }
        }
      }
      in.close();
      
      this.Host.Manager.GetGameWorldManager().RegisterWorld(this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println("Line: " + line);
    }
  }
  
  protected Location StrToLoc(String loc)
  {
    String[] coords = loc.split(",");
    
    try
    {
      return new Location(this.World, Integer.valueOf(coords[0]).intValue() + 0.5D, Integer.valueOf(coords[1]).intValue(), Integer.valueOf(coords[2]).intValue() + 0.5D);
    }
    catch (Exception e)
    {
      System.out.println("World Data Read Error: Invalid Location String [" + loc + "]");
    }
    
    return null;
  }
  
  public boolean LoadChunks(long maxMilliseconds)
  {
    long startTime = System.currentTimeMillis();
    for (; 
        this.CurX <= this.MaxX; this.CurX += 16)
    {
      for (; this.CurZ <= this.MaxZ; this.CurZ += 16)
      {
        if (System.currentTimeMillis() - startTime >= maxMilliseconds) {
          return false;
        }
        this.World.getChunkAt(new Location(this.World, this.CurX, 0.0D, this.CurZ));
      }
      
      this.CurZ = this.MinZ;
    }
    
    return true;
  }
  
  public void Uninitialize()
  {
    if (this.World == null) {
      return;
    }
    
    MapUtil.UnloadWorld(this.Host.Manager.GetPlugin(), this.World);
    MapUtil.ClearWorldReferences(this.World.getName());
    mineplex.core.common.util.FileUtil.DeleteFolder(new File(this.World.getName()));
    
    this.World = null;
  }
  
  public void ChunkUnload(ChunkUnloadEvent event)
  {
    if (this.World == null) {
      return;
    }
    if (!event.getWorld().equals(this.World)) {
      return;
    }
    event.setCancelled(true);
  }
  
  public void ChunkLoad(ChunkPreLoadEvent event)
  {
    if (this.World == null) {
      return;
    }
    if (!event.GetWorld().equals(this.World)) {
      return;
    }
    int x = event.GetX();
    int z = event.GetZ();
    

    if ((x >= this.MinX >> 4) && (x <= this.MaxX >> 4) && (z >= this.MinZ >> 4) && (z <= this.MaxZ >> 4))
    {
      return;
    }
    

    event.setCancelled(true);
  }
  
  public int GetNewId()
  {
    File file = new File("GameId.dat");
    

    if (!file.exists())
    {
      try
      {
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        
        out.write("0");
        
        out.close();
      }
      catch (Exception e)
      {
        System.out.println("Error: Game World GetId Write Exception");
      }
    }
    
    int id = 0;
    

    try
    {
      FileInputStream fstream = new FileInputStream(file);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line = br.readLine();
      
      id = Integer.parseInt(line);
      
      in.close();
    }
    catch (Exception e)
    {
      System.out.println("Error: Game World GetId Read Exception");
      id = 0;
    }
    
    try
    {
      FileWriter fstream = new FileWriter(file);
      BufferedWriter out = new BufferedWriter(fstream);
      
      out.write(id + 1);
      
      out.close();
    }
    catch (Exception e)
    {
      System.out.println("Error: Game World GetId Re-Write Exception");
    }
    
    return id;
  }
  
  public ArrayList<Location> GetDataLocs(String data)
  {
    if (!this.DataLocs.containsKey(data)) {
      return new ArrayList();
    }
    return (ArrayList)this.DataLocs.get(data);
  }
  
  public ArrayList<Location> GetCustomLocs(String id)
  {
    if (!this.CustomLocs.containsKey(id)) {
      return new ArrayList();
    }
    return (ArrayList)this.CustomLocs.get(id);
  }
  
  public HashMap<String, ArrayList<Location>> GetAllCustomLocs()
  {
    return this.CustomLocs;
  }
  
  public Location GetRandomXZ()
  {
    Location loc = new Location(this.World, 0.0D, 250.0D, 0.0D);
    
    int xVar = this.MaxX - this.MinX;
    int zVar = this.MaxZ - this.MinZ;
    
    loc.setX(this.MinX + UtilMath.r(xVar));
    loc.setZ(this.MinZ + UtilMath.r(zVar));
    
    return loc;
  }
}
