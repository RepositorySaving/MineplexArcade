package mineplex.core.account.repository;

import mineplex.core.mysql.RepositoryBase;
import org.bukkit.plugin.java.JavaPlugin;

public class MysqlAccountRepository
  extends RepositoryBase
{
  private static String CREATE_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS Accounts (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(40), gems INT, rank VARCHAR(40), rankPerm BOOL, rankExpire LONG, lastLogin LONG, totalPlayTime LONG, PRIMARY KEY (id));";
  
  public MysqlAccountRepository(JavaPlugin plugin)
  {
    super(plugin);
  }
  

















  protected void initialize()
  {
    executeQuery(CREATE_ACCOUNT_TABLE);
  }
  
  protected void update() {}
}
