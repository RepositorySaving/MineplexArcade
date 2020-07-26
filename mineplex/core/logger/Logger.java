package mineplex.core.logger;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class Logger
{
  public static Logger Instance;
  private LoggerRepository _repository;
  
  public static void initialize(JavaPlugin plugin)
  {
    Instance = new Logger(plugin);
  }
  
  public Logger(JavaPlugin plugin)
  {
    setupConfigValues(plugin);
    
    this._repository = new LoggerRepository(plugin.getConfig().getString("log.connectionurl"), plugin.getConfig().getString("serverstatus.name"));
    
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
    {

      public void uncaughtException(Thread t, Throwable e)
      {
        Logger.this.log(e);
        e.printStackTrace();
      }
    });
  }
  
  private void setupConfigValues(JavaPlugin plugin)
  {
    try
    {
      plugin.getConfig().addDefault("log.connectionurl", "jdbc:mysql://sqlstats.mineplex.com:3306/Mineplex");
      plugin.getConfig().set("log.connectionurl", plugin.getConfig().getString("log.connectionurl"));
      plugin.saveConfig();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void log(String message)
  {
    this._repository.saveLog(new String[] { message });
  }
  
  public void log(Throwable exception)
  {
    List<String> messages = new ArrayList();
    
    messages.add("[Exception Start]" + exception.getMessage());
    
    for (StackTraceElement element : exception.getStackTrace())
    {
      messages.add(element.toString());
    }
    
    messages.add("[Exception End]");
    
    this._repository.saveLog((String[])messages.toArray(new String[0]));
  }
}
