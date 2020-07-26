package mineplex.core.task;

import java.util.ArrayList;
import java.util.List;

public class TaskClient
{
  public String Name;
  public List<String> TasksCompleted;
  
  public TaskClient(String name)
  {
    this.Name = name;
    this.TasksCompleted = new ArrayList();
  }
  
  public String toString()
  {
    return this.Name + " Tasks: {" + this.TasksCompleted.toString() + "}";
  }
}
