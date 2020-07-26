package mineplex.core.punish;

import java.io.PrintStream;
import java.util.HashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.punish.Tokens.PunishClientToken;
import mineplex.core.punish.Tokens.PunishmentToken;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Punish extends MiniPlugin
{
  private HashMap<String, PunishClient> _punishClients;
  private PunishRepository _repository;
  private CoreClientManager _clientManager;
  
  public Punish(JavaPlugin plugin, String webServerAddress, CoreClientManager clientManager)
  {
    super("Punish", plugin);
    
    this._punishClients = new HashMap();
    this._clientManager = clientManager;
    this._repository = new PunishRepository(webServerAddress);
  }
  
  public PunishRepository GetRepository()
  {
    return this._repository;
  }
  

  public void AddCommands()
  {
    AddCommand(new mineplex.core.punish.Command.PunishCommand(this));
  }
  











































  @EventHandler
  public void OnClientWebResponse(ClientWebResponseEvent event)
  {
    PunishClientToken token = (PunishClientToken)new Gson().fromJson(event.GetResponse(), PunishClientToken.class);
    LoadClient(token);
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    this._punishClients.remove(event.getPlayer().getName().toLowerCase());
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void PlayerLogin(AsyncPlayerPreLoginEvent event)
  {
    if (this._punishClients.containsKey(event.getName().toLowerCase()))
    {
      PunishClient client = GetClient(event.getName());
      
      if (client.IsBanned())
      {
        Punishment punishment = client.GetPunishment(PunishmentSentence.Ban);
        String time = F.time(UtilTime.convertString((punishment.GetHours() * 3600000.0D), 0, UtilTime.TimeUnit.FIT));
        
        if (punishment.GetHours() == -1.0D) {
          time = C.cRed + "Permanent";
        }
        String reason = C.consoleHead + F.main(GetName(), new StringBuilder(String.valueOf(punishment.GetAdmin())).append(" banned you because of '").append(F.elem(punishment.GetReason())).append("' for ").append(time).toString());
        
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PunishChatEvent(AsyncPlayerChatEvent event)
  {
    PunishClient client = GetClient(event.getPlayer().getName());
    
    if ((client != null) && (client.IsMuted()))
    {
      event.getPlayer().sendMessage(F.main(GetName(), "Shh, you're muted for " + C.cGreen + UtilTime.convertString(client.GetPunishment(PunishmentSentence.Mute).GetRemaining(), 1, UtilTime.TimeUnit.FIT) + "."));
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PunishChatEvent(PlayerCommandPreprocessEvent event)
  {
    PunishClient client = GetClient(event.getPlayer().getName());
    
    if ((client != null) && (client.IsMuted()))
    {
      event.getPlayer().sendMessage(F.main(GetName(), "Shh, you're muted for " + C.cGreen + UtilTime.convertString(client.GetPunishment(PunishmentSentence.Mute).GetRemaining(), 1, UtilTime.TimeUnit.FIT) + "."));
      event.setMessage(" ");
      event.setCancelled(true);
    }
  }
  
  public void Help(Player caller)
  {
    UtilPlayer.message(caller, F.main(this._moduleName, "Commands List:"));
    UtilPlayer.message(caller, F.help("/punish", "<player> <reason>", Rank.MODERATOR));
  }
  
  public void AddPunishment(final String playerName, Category category, final String reason, final Player caller, int severity, boolean ban, long duration)
  {
    if (!this._punishClients.containsKey(playerName.toLowerCase()))
    {
      this._punishClients.put(playerName.toLowerCase(), new PunishClient());
    }
    
    PunishmentSentence sentence = !ban ? PunishmentSentence.Mute : PunishmentSentence.Ban;
    
    final long finalDuration = duration;
    
    this._repository.Punish(new Callback()
    {
      public void run(String result)
      {
        PunishmentResponse banResult = PunishmentResponse.valueOf(result);
        
        if (banResult == PunishmentResponse.AccountDoesNotExist)
        {
          if (caller != null) {
            caller.sendMessage(F.main(Punish.this.GetName(), "Account with name " + F.elem(playerName) + " does not exist."));
          } else {
            System.out.println(F.main(Punish.this.GetName(), "Account with name " + F.elem(playerName) + " does not exist."));
          }
        } else if (banResult == PunishmentResponse.InsufficientPrivileges)
        {
          if (caller != null) {
            caller.sendMessage(F.main(Punish.this.GetName(), "You have insufficient rights to punish " + F.elem(playerName) + "."));
          } else {
            System.out.println(F.main(Punish.this.GetName(), "You have insufficient rights to punish " + F.elem(playerName) + "."));
          }
        } else if (banResult == PunishmentResponse.Punished)
        {
          final String durationString = F.time(UtilTime.convertString(finalDuration < 0L ? -1L : finalDuration * 3600000L, 1, UtilTime.TimeUnit.FIT));
          
          if (reason == PunishmentSentence.Ban)
          {
            if (caller == null) {
              System.out.println(F.main(Punish.this.GetName(), F.elem(caller == null ? "Mineplex Anti-Cheat" : caller.getName()) + " banned " + F.elem(playerName) + " because of " + F.elem(this.val$reason) + " for " + durationString + "."));
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Punish.this.GetPlugin(), new Runnable()
            {
              public void run()
              {
                UtilPlayer.kick(UtilPlayer.searchOnline(null, this.val$playerName, false), Punish.this.GetName(), this.val$caller.getName() + " banned you because of " + F.elem(this.val$reason) + " for " + 
                  durationString + ".");
              }
              
            });
            UtilServer.broadcast(F.main(Punish.this.GetName(), F.elem(caller == null ? "Mineplex Anti-Cheat" : caller.getName()) + " banned " + F.elem(playerName) + " because of " + F.elem(this.val$reason) + " for " + durationString + "."));
          }
          else
          {
            if (caller == null) {
              System.out.println(F.main(Punish.this.GetName(), F.elem(caller == null ? "Mineplex Anti-Cheat" : caller.getName()) + " muted " + F.elem(playerName) + " because of " + F.elem(this.val$reason) + " for " + 
                durationString + "."));
            }
            UtilServer.broadcast(F.main(Punish.this.GetName(), F.elem(caller == null ? "Mineplex Anti-Cheat" : caller.getName()) + " muted " + F.elem(playerName) + " because of " + F.elem(this.val$reason) + " for " + 
              durationString + "."));
            
            Punish.this._repository.LoadPunishClient(playerName, new Callback()
            {
              public void run(PunishClientToken token)
              {
                Punish.this.LoadClient(token);
              }
            });
          }
        }
      }
    }, playerName, category.toString(), sentence, reason, duration, caller == null ? "Mineplex Anti-Cheat" : caller.getName(), severity, System.currentTimeMillis());
  }
  
  public void LoadClient(PunishClientToken token)
  {
    PunishClient client = new PunishClient();
    
    for (PunishmentToken punishment : token.Punishments)
    {
      client.AddPunishment(Category.valueOf(punishment.Category), new Punishment(punishment.PunishmentId, PunishmentSentence.valueOf(punishment.Sentence), Category.valueOf(punishment.Category), punishment.Reason, punishment.Admin, punishment.Duration, punishment.Severity, punishment.Time, punishment.Active, punishment.Removed, punishment.RemoveAdmin, punishment.RemoveReason));
    }
    
    this._punishClients.put(token.Name.toLowerCase(), client);
  }
  
  public PunishClient GetClient(String name)
  {
    synchronized (this)
    {
      return (PunishClient)this._punishClients.get(name.toLowerCase());
    }
  }
  
  public void RemovePunishment(int punishmentId, String target, Player admin, String reason, Callback<String> callback)
  {
    this._repository.RemovePunishment(callback, punishmentId, target, reason, admin.getName());
  }
  
  public void RemoveBan(String name, String reason)
  {
    this._repository.RemoveBan(name, reason);
  }
  
  public CoreClientManager GetClients()
  {
    return this._clientManager;
  }
}
