package mineplex.core.status;

import java.io.File;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.Callback;
import mineplex.core.monitor.LagMeter;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ServerStatusManager extends MiniPlugin
{
  private ServerStatusRepository _repository;
  private LagMeter _lagMeter;
  private String _name;
  private boolean _alternateSeconds;
  private boolean _enabled = true;
  
  public ServerStatusManager(JavaPlugin plugin, LagMeter lagMeter)
  {
    super("Server Status Manager", plugin);
    
    this._lagMeter = lagMeter;
    
    if (new File("IgnoreUpdates.dat").exists()) {
      this._enabled = false;
    }
    ServerListPingEvent event = new ServerListPingEvent(null, plugin.getServer().getMotd(), plugin.getServer().getOnlinePlayers().length, plugin.getServer().getMaxPlayers());
    
    GetPluginManager().callEvent(event);
    
    setupConfigValues();
    
    String address = Bukkit.getServer().getIp().isEmpty() ? "localhost" : Bukkit.getServer().getIp();
    
    this._name = plugin.getConfig().getString("serverstatus.name");
    
    try
    {
      this._repository = new ServerStatusRepository(
        plugin.getConfig().getString("serverstatus.connectionurl"), 
        plugin.getConfig().getString("serverstatus.username"), 
        plugin.getConfig().getString("serverstatus.password"), 
        plugin.getConfig().getBoolean("serverstatus.us"), 
        this._name, 
        plugin.getConfig().getString("serverstatus.group"), 
        address, 
        this._plugin.getServer().getPort(), 
        event.getMaxPlayers());
      

      if (this._enabled) {
        this._repository.initialize();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  private void setupConfigValues()
  {
    try
    {
      GetPlugin().getConfig().addDefault("serverstatus.connectionurl", "jdbc:mysql://db.mineplex.com:3306/ServerStatus");
      GetPlugin().getConfig().set("serverstatus.connectionurl", GetPlugin().getConfig().getString("serverstatus.connectionurl"));
      
      GetPlugin().getConfig().addDefault("serverstatus.username", "root");
      GetPlugin().getConfig().set("serverstatus.username", GetPlugin().getConfig().getString("serverstatus.username"));
      
      GetPlugin().getConfig().addDefault("serverstatus.password", "tAbechAk3wR7tuTh");
      GetPlugin().getConfig().set("serverstatus.password", GetPlugin().getConfig().getString("serverstatus.password"));
      
      GetPlugin().getConfig().addDefault("serverstatus.us", Boolean.valueOf(true));
      GetPlugin().getConfig().set("serverstatus.us", Boolean.valueOf(GetPlugin().getConfig().getBoolean("serverstatus.us")));
      
      GetPlugin().getConfig().addDefault("serverstatus.name", "TEST-1");
      GetPlugin().getConfig().set("serverstatus.name", GetPlugin().getConfig().getString("serverstatus.name"));
      
      GetPlugin().getConfig().addDefault("serverstatus.group", "Testing");
      GetPlugin().getConfig().set("serverstatus.group", GetPlugin().getConfig().getString("serverstatus.group"));
      
      GetPlugin().saveConfig();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void retrieveServerStatuses(final Callback<List<ServerStatusData>> callback)
  {
    if (!this._enabled) {
      return;
    }
    GetPlugin().getServer().getScheduler().runTaskAsynchronously(GetPlugin(), new Runnable()
    {
      public void run()
      {
        if (callback != null)
        {
          callback.run(ServerStatusManager.this._repository.retrieveServerStatuses());
        }
      }
    });
  }
  
  @EventHandler
  public void saveServerStatus(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!this._enabled) {
      return;
    }
    this._alternateSeconds = (!this._alternateSeconds);
    
    if (!this._alternateSeconds) {
      return;
    }
    final ServerListPingEvent listPingEvent = new ServerListPingEvent(null, GetPlugin().getServer().getMotd(), GetPlugin().getServer().getOnlinePlayers().length, GetPlugin().getServer().getMaxPlayers());
    
    GetPluginManager().callEvent(listPingEvent);
    
    GetPlugin().getServer().getScheduler().runTaskAsynchronously(GetPlugin(), new Runnable()
    {
      public void run()
      {
        ServerStatusManager.this._repository.updatePlayerCountInDatabase(listPingEvent.getMotd(), Bukkit.getOnlinePlayers().length, listPingEvent.getMaxPlayers(), (int)ServerStatusManager.this._lagMeter.getTicksPerSecond());
      }
    });
  }
  
  public String getCurrentServerName()
  {
    return this._name;
  }
}
