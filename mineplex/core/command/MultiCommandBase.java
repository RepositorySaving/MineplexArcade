package mineplex.core.command;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.NautHashMap;
import org.bukkit.entity.Player;

public abstract class MultiCommandBase<PluginType extends MiniPlugin> extends CommandBase<PluginType>
{
  protected NautHashMap<String, ICommand> Commands;
  
  public MultiCommandBase(PluginType plugin, Rank rank, String... aliases)
  {
    super(plugin, rank, aliases);
    
    this.Commands = new NautHashMap();
  }
  
  public void AddCommand(ICommand command)
  {
    for (String commandRoot : command.Aliases())
    {
      this.Commands.put(commandRoot, command);
      command.SetCommandCenter(this.CommandCenter);
    }
  }
  
  public void Execute(Player caller, String[] args)
  {
    String commandName = null;
    String[] newArgs = null;
    
    if ((args != null) && (args.length > 0))
    {
      commandName = args[0];
      
      if (args.length > 1)
      {
        newArgs = new String[args.length - 1];
        
        for (int i = 0; i < newArgs.length; i++)
        {
          newArgs[i] = args[(i + 1)];
        }
      }
    }
    
    ICommand command = (ICommand)this.Commands.get(commandName);
    
    if ((command != null) && (this.CommandCenter.ClientManager.Get(caller).GetRank().Has(caller, command.GetRequiredRank(), true)))
    {
      command.SetAliasUsed(commandName);
      
      command.Execute(caller, newArgs);
    }
    else
    {
      Help(caller, args);
    }
  }
  
  protected abstract void Help(Player paramPlayer, String[] paramArrayOfString);
}
