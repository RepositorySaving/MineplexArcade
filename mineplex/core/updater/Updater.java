package mineplex.core.updater;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Updater implements Runnable
{
  private JavaPlugin _plugin;
  
  public Updater(JavaPlugin plugin)
  {
    this._plugin = plugin;
    this._plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this._plugin, this, 0L, 1L);
  }
  

  public void run()
  {
    for (UpdateType updateType : )
    {
      if (updateType.Elapsed())
      {
        this._plugin.getServer().getPluginManager().callEvent(new mineplex.core.updater.event.UpdateEvent(updateType));
      }
    }
  }
}
