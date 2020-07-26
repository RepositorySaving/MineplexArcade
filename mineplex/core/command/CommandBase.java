package mineplex.core.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.common.Rank;


public abstract class CommandBase<PluginType extends MiniPlugin>
  implements ICommand
{
  private Rank _requiredRank;
  private List<String> _aliases;
  protected PluginType Plugin;
  protected String AliasUsed;
  protected CommandCenter CommandCenter;
  
  public CommandBase(PluginType plugin, Rank requiredRank, String... aliases)
  {
    this.Plugin = plugin;
    this._requiredRank = requiredRank;
    this._aliases = Arrays.asList(aliases);
  }
  
  public Collection<String> Aliases()
  {
    return this._aliases;
  }
  
  public void SetAliasUsed(String alias)
  {
    this.AliasUsed = alias;
  }
  
  public Rank GetRequiredRank()
  {
    return this._requiredRank;
  }
  
  public void SetCommandCenter(CommandCenter commandCenter)
  {
    this.CommandCenter = commandCenter;
  }
}
