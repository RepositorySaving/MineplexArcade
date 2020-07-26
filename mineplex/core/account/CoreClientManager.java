package mineplex.core.account;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.UUID;
import mineplex.core.account.event.AsyncClientLoadEvent;
import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.account.event.RetrieveClientInformationEvent;
import mineplex.core.account.repository.AccountRepository;
import mineplex.core.account.repository.token.ClientToken;
import mineplex.core.common.Rank;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreClientManager implements Listener
{
  private static CoreClientManager _instance;
  private JavaPlugin _plugin;
  private AccountRepository _repository;
  private NautHashMap<String, CoreClient> _clientList;
  private HashSet<String> _dontRemoveList;
  private Object _clientLock = new Object();
  
  protected CoreClientManager(JavaPlugin plugin, String webServer)
  {
    _instance = this;
    
    this._plugin = plugin;
    this._repository = new AccountRepository(webServer);
    this._clientList = new NautHashMap();
    this._dontRemoveList = new HashSet();
    
    this._plugin.getServer().getPluginManager().registerEvents(this, this._plugin);
  }
  
  public static CoreClientManager Initialize(JavaPlugin plugin, String webServer)
  {
    if (_instance == null)
    {
      _instance = new CoreClientManager(plugin, webServer);
    }
    
    return _instance;
  }
  
  public CoreClient Add(String name)
  {
    CoreClient newClient = null;
    
    if (newClient == null)
    {
      newClient = new CoreClient(name);
    }
    
    CoreClient oldClient = null;
    
    synchronized (this._clientLock)
    {
      oldClient = (CoreClient)this._clientList.put(name, newClient);
    }
    
    if (oldClient != null)
    {
      oldClient.Delete();
    }
    
    return newClient;
  }
  
  public void Del(String name)
  {
    synchronized (this._clientLock)
    {
      this._clientList.remove(name);
    }
    
    this._plugin.getServer().getPluginManager().callEvent(new ClientUnloadEvent(name));
  }
  
  public CoreClient Get(String name)
  {
    synchronized (this._clientLock)
    {
      return (CoreClient)this._clientList.get(name);
    }
  }
  
  public CoreClient Get(Player player)
  {
    synchronized (this._clientLock)
    {
      return (CoreClient)this._clientList.get(player.getName());
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void AsyncLogin(AsyncPlayerPreLoginEvent event)
  {
    try
    {
      LoadClient(Add(event.getName()), event.getUniqueId(), event.getAddress().getHostAddress());
    }
    catch (Exception exception)
    {
      Logger.Instance.log(exception);
      
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error retrieving information from web, please retry in a minute.");
    }
    
    if ((Bukkit.hasWhitelist()) && (!Get(event.getName()).GetRank().Has(Rank.MODERATOR)))
    {
      for (OfflinePlayer player : Bukkit.getWhitelistedPlayers())
      {
        if (player.getName().equalsIgnoreCase(event.getName()))
        {
          return;
        }
      }
      
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted my friend.");
    }
  }
  


  private void LoadClient(CoreClient client, UUID uuid, String ipAddress)
  {
    ClientToken token = null;
    Gson gson = new Gson();
    
    String response = this._repository.GetClient(client.GetPlayerName(), uuid, ipAddress);
    token = (ClientToken)gson.fromJson(response, ClientToken.class);
    
    client.SetAccountId(token.AccountId);
    client.SetRank(Rank.valueOf(token.Rank));
    

    Bukkit.getServer().getPluginManager().callEvent(new ClientWebResponseEvent(response));
    

    Bukkit.getServer().getPluginManager().callEvent(new AsyncClientLoadEvent(token, client));
    

    try
    {
      Bukkit.getServer().getPluginManager().callEvent(new RetrieveClientInformationEvent(client.GetPlayerName(), uuid));
    }
    catch (Exception exception)
    {
      Logger.Instance.log(exception);
      System.out.println("Error running RetrieveClientInformationEvent" + exception.getMessage());
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void Login(PlayerLoginEvent event)
  {
    synchronized (this._clientLock)
    {
      if (!this._clientList.containsKey(event.getPlayer().getName()))
      {
        this._clientList.put(event.getPlayer().getName(), new CoreClient(event.getPlayer().getName()));
      }
    }
    
    CoreClient client = Get(event.getPlayer().getName());
    client.SetPlayer(event.getPlayer());
    

    if (Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers())
    {
      if (client.GetRank().Has(event.getPlayer(), Rank.ULTRA, false))
      {
        event.allow();
        event.setResult(PlayerLoginEvent.Result.ALLOWED);
        return;
      }
      
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Full > Purchase Ultra at www.mineplex.com/shop");
    }
  }
  
  @EventHandler
  public void Kick(PlayerKickEvent event)
  {
    if (event.getReason().equalsIgnoreCase("You logged in from another location"))
    {
      this._dontRemoveList.add(event.getPlayer().getName());
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void Quit(PlayerQuitEvent event)
  {
    if (!this._dontRemoveList.contains(event.getPlayer().getName()))
    {
      Del(event.getPlayer().getName());
    }
    
    this._dontRemoveList.remove(event.getPlayer().getName());
  }
  
  public void SaveRank(final String name, Rank rank, boolean perm)
  {
    this._repository.SaveRank(new Callback()
    {
      public void run(Rank newRank)
      {
        if (CoreClientManager.this._plugin.getServer().getPlayer(name) != null)
        {
          CoreClient client = CoreClientManager.this.Get(name);
          
          client.SetRank(newRank);
        }
      }
    }, name, rank, perm);
  }
}
