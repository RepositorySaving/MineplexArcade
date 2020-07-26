package nautilus.game.arcade.managers;

import nautilus.game.arcade.ArcadeManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class GameStatsManager implements Listener
{
  ArcadeManager Manager;
  
  public GameStatsManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
}
