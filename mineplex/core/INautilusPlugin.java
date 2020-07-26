package mineplex.core;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract interface INautilusPlugin
{
  public abstract JavaPlugin GetPlugin();
  
  public abstract String GetWebServerAddress();
  
  public abstract Server GetRealServer();
  
  public abstract PluginManager GetPluginManager();
}
