package mineplex.core.antihack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mineplex.core.logger.Logger;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;



public class AntiHackRepository
{
  private String _serverName;
  private static Connection _connection;
  private String _connectionString = "jdbc:mysql://sqlstats.mineplex.com:3306/Mineplex?autoReconnect=true";
  private String _userName = "root";
  private String _password = "tAbechAk3wR7tuTh";
  
  private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS AntiHack_Kick_Log (id INT NOT NULL AUTO_INCREMENT, updated LONG, playerName VARCHAR(256), motd VARCHAR(56), gameType VARCHAR(56), map VARCHAR(256), serverName VARCHAR(256), report VARCHAR(256), ping VARCHAR(25), PRIMARY KEY (id));";
  private static String UPDATE_PLAYER_OFFENSES = "INSERT INTO AntiHack_Kick_Log (updated, playerName, motd, gameType, map, serverName, report, ping) VALUES (now(), ?, ?, ?, ?, ?, ?, ?);";
  
  public AntiHackRepository(String serverName)
  {
    this._serverName = serverName;
  }
  
  public void initialize()
  {
    PreparedStatement preparedStatement = null;
    
    try
    {
      if ((_connection == null) || (_connection.isClosed())) {
        _connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      
      preparedStatement = _connection.prepareStatement(CREATE_TABLE);
      preparedStatement.execute();
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
      Logger.Instance.log(exception);
      


      if (preparedStatement != null)
      {
        try
        {
          preparedStatement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (preparedStatement != null)
      {
        try
        {
          preparedStatement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  












































  public void saveOffense(final Player player, final String motd, final String game, final String map, final String report)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        PreparedStatement preparedStatement = null;
        
        try
        {
          if ((AntiHackRepository._connection == null) || (AntiHackRepository._connection.isClosed())) {
            AntiHackRepository._connection = DriverManager.getConnection(AntiHackRepository.this._connectionString, AntiHackRepository.this._userName, AntiHackRepository.this._password);
          }
          preparedStatement = AntiHackRepository._connection.prepareStatement(AntiHackRepository.UPDATE_PLAYER_OFFENSES);
          

          preparedStatement.setString(1, player.getName());
          preparedStatement.setString(2, motd);
          preparedStatement.setString(3, game);
          preparedStatement.setString(4, map);
          preparedStatement.setString(5, AntiHackRepository.this._serverName);
          preparedStatement.setString(6, report);
          preparedStatement.setString(7, ((CraftPlayer)player).getHandle().ping + "ms");
          
          preparedStatement.execute();
        }
        catch (Exception exception)
        {
          exception.printStackTrace();
          Logger.Instance.log(exception);
          


          if (preparedStatement != null)
          {
            try
            {
              preparedStatement.close();
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
          }
        }
        finally
        {
          if (preparedStatement != null)
          {
            try
            {
              preparedStatement.close();
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
          }
        }
      }
    })
    










































      .start();
  }
}
