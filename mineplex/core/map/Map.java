package mineplex.core.map;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.map.commands.MapImage;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Map extends MiniPlugin
{
  JavaPlugin Plugin;
  private Player _caller = null;
  private String _url = "http://chivebox.com/img/mc/news.png";
  private String _defaultUrl = null;
  
  public Map(JavaPlugin plugin)
  {
    super("Map", plugin);
    
    this.Plugin = plugin;
  }
  

  public void AddCommands()
  {
    AddCommand(new MapImage(this));
  }
  
  public ItemStack GetMap()
  {
    return ItemStackFactory.Instance.CreateStack(Material.MAP, (byte)127, 1, C.cAqua + C.Bold + "iMap 3.0");
  }
  
  public void SpawnMap(Player caller, String[] args)
  {
    if ((args == null) || (args.length == 0))
    {
      UtilPlayer.message(this._caller, F.main("Map Image", "Missing Image URL!"));
      return;
    }
    
    this._caller = caller;
    this._url = args[0];
    caller.getInventory().addItem(new ItemStack[] { GetMap() });
    
    if (args.length > 1)
    {
      if (args[1].equals("all"))
      {
        for (Player player : UtilServer.getPlayers())
        {
          if (!player.equals(caller))
          {

            player.getInventory().remove(Material.MAP);
            
            player.getInventory().addItem(new ItemStack[] { GetMap() });
          }
        }
      }
      else {
        Player target = UtilPlayer.searchOnline(caller, args[1], true);
        if (target != null)
        {
          target.getInventory().remove(Material.MAP);
          target.getInventory().addItem(new ItemStack[] { GetMap() });
        }
      }
    }
  }
  

  @EventHandler
  public void MapInit(MapInitializeEvent event)
  {
    final MapView map = event.getMap();
    
    for (MapRenderer rend : map.getRenderers()) {
      map.removeRenderer(rend);
    }
    if (this._defaultUrl != null)
    {
      this.Plugin.getServer().getScheduler().runTaskAsynchronously(this.Plugin, new Runnable()
      {
        public void run()
        {
          try
          {
            map.addRenderer(new ImageRenderer(Map.this._defaultUrl));
          }
          catch (Exception e)
          {
            System.out.println("Invalid Default Image: " + Map.this._defaultUrl);
          }
          
          Map.this._defaultUrl = null;
        }
        

      });
    } else if (this._url != null)
    {
      this.Plugin.getServer().getScheduler().runTaskAsynchronously(this.Plugin, new Runnable()
      {
        public void run()
        {
          try
          {
            map.addRenderer(new ImageRenderer(Map.this._url));
            UtilPlayer.message(Map.this._caller, F.main("Map Image", "Loaded Image: " + Map.this._url));
          }
          catch (Exception e)
          {
            UtilPlayer.message(Map.this._caller, F.main("Map Image", "Invalid Image URL: " + Map.this._url));
          }
        }
      });
    }
  }
  
  public void SetDefaultUrl(String string)
  {
    this._defaultUrl = string;
  }
}
