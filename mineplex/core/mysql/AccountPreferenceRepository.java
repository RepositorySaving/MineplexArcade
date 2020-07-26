package mineplex.core.mysql;

import org.bukkit.plugin.java.JavaPlugin;

public class AccountPreferenceRepository extends RepositoryBase
{
  private static String CREATE_ACCOUNT_PREFERENCE_TABLE = "CREATE TABLE IF NOT EXISTS AccountPreferences (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id));";
  
  public AccountPreferenceRepository(JavaPlugin plugin)
  {
    super(plugin);
  }
  

  protected void initialize()
  {
    executeQuery(CREATE_ACCOUNT_PREFERENCE_TABLE);
  }
  
  protected void update() {}
}
