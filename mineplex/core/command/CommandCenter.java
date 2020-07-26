package mineplex.core.command;

import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.recharge.Recharge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandCenter implements Listener
{
  public static CommandCenter Instance;
  protected JavaPlugin Plugin;
  protected CoreClientManager ClientManager;
  protected NautHashMap<String, ICommand> Commands;
  
  public static void Initialize(JavaPlugin plugin, CoreClientManager clientManager)
  {
    if (Instance == null) {
      Instance = new CommandCenter(plugin, clientManager);
    }
  }
  
  public CoreClientManager GetClientManager() {
    return this.ClientManager;
  }
  
  private CommandCenter(JavaPlugin instance, CoreClientManager manager)
  {
    this.Plugin = instance;
    this.ClientManager = manager;
    this.Commands = new NautHashMap();
    this.Plugin.getServer().getPluginManager().registerEvents(this, this.Plugin);
  }
  
  @EventHandler
  public void OnPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
    String commandName = event.getMessage().substring(1);
    String[] args = null;
    
    if (commandName.contains(" "))
    {
      commandName = commandName.split(" ")[0];
      args = event.getMessage().substring(event.getMessage().indexOf(' ') + 1).split(" ");
    }
    
    ICommand command = (ICommand)this.Commands.get(commandName.toLowerCase());
    
    if ((command != null) && (this.ClientManager.Get(event.getPlayer()).GetRank().Has(event.getPlayer(), command.GetRequiredRank(), true)))
    {
      if (!Recharge.Instance.use(event.getPlayer(), "Command", 500L, false, false))
      {
        event.getPlayer().sendMessage(mineplex.core.common.util.F.main("Command Center", "You can't spam commands that fast."));
        event.setCancelled(true);
        return;
      }
      
      command.SetAliasUsed(commandName.toLowerCase());
      command.Execute(event.getPlayer(), args);
      
      event.setCancelled(true);
    }
  }
  
  public void AddCommand(ICommand command)
  {
    for (String commandRoot : command.Aliases())
    {
      this.Commands.put(commandRoot.toLowerCase(), command);
      command.SetCommandCenter(this);
    }
  }
  
  public void RemoveCommand(ICommand command)
  {
    for (String commandRoot : command.Aliases())
    {
      this.Commands.remove(commandRoot.toLowerCase());
      command.SetCommandCenter(null);
    }
  }
}
