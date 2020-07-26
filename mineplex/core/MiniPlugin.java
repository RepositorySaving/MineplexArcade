package mineplex.core;

import mineplex.core.command.CommandCenter;
import mineplex.core.command.ICommand;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class MiniPlugin implements Listener
{
  protected String _moduleName = "Default";
  
  protected JavaPlugin _plugin;
  
  protected NautHashMap<String, ICommand> _commands;
  
  public MiniPlugin(String moduleName, JavaPlugin plugin)
  {
    this._moduleName = moduleName;
    this._plugin = plugin;
    
    this._commands = new NautHashMap();
    
    onEnable();
    
    RegisterEvents(this);
  }
  
  public PluginManager GetPluginManager()
  {
    return this._plugin.getServer().getPluginManager();
  }
  
  public BukkitScheduler GetScheduler()
  {
    return this._plugin.getServer().getScheduler();
  }
  
  public JavaPlugin GetPlugin()
  {
    return this._plugin;
  }
  
  public void RegisterEvents(Listener listener)
  {
    this._plugin.getServer().getPluginManager().registerEvents(listener, this._plugin);
  }
  
  public final void onEnable()
  {
    long epoch = System.currentTimeMillis();
    Log("Initializing...");
    Enable();
    AddCommands();
    Log("Enabled in " + UtilTime.convertString(System.currentTimeMillis() - epoch, 1, UtilTime.TimeUnit.FIT) + ".");
  }
  
  public final void onDisable()
  {
    Disable();
    
    Log("Disabled.");
  }
  
  public void Enable() {}
  
  public void Disable() {}
  
  public void AddCommands() {}
  
  public final String GetName()
  {
    return this._moduleName;
  }
  
  public final void AddCommand(ICommand command)
  {
    CommandCenter.Instance.AddCommand(command);
  }
  
  public final void RemoveCommand(ICommand command)
  {
    CommandCenter.Instance.RemoveCommand(command);
  }
  
  protected void Log(String message)
  {
    System.out.println(F.main(this._moduleName, message));
  }
}
