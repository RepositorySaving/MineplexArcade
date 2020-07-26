package mineplex.core.task;

import java.util.List;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.task.repository.TaskRepository;
import mineplex.core.task.repository.TaskToken;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class TaskManager extends MiniClientPlugin<TaskClient>
{
  private TaskRepository _repository;
  
  public TaskManager(JavaPlugin plugin, String webServerAddress)
  {
    super("Task Manager", plugin);
    
    this._repository = new TaskRepository(webServerAddress);
  }
  

  protected TaskClient AddPlayer(String playerName)
  {
    return new TaskClient(playerName);
  }
  
  @EventHandler
  public void OnClientWebResponse(ClientWebResponseEvent event)
  {
    TaskToken token = (TaskToken)new Gson().fromJson(event.GetResponse(), TaskToken.class);
    TaskClient client = new TaskClient(token.Name);
    
    if (token.TasksCompleted != null) {
      client.TasksCompleted = token.TasksCompleted;
    }
    Set(token.Name, client);
  }
  
  public boolean hasCompletedTask(Player player, String taskName)
  {
    return ((TaskClient)Get(player.getName())).TasksCompleted.contains(taskName);
  }
  
  public void completedTask(Player player, String taskName)
  {
    TaskClient client = (TaskClient)Get(player.getName());
    client.TasksCompleted.add(taskName);
    
    this._repository.AddTask(client.Name, taskName);
  }
}
