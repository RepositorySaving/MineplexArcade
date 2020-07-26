package mineplex.core.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RepositoryBase
{
  private String _connectionString;
  private String _userName;
  private String _password;
  
  public RepositoryBase(JavaPlugin plugin)
  {
    this._connectionString = plugin.getConfig().getString("serverstatus.connectionurl");
    this._userName = plugin.getConfig().getString("serverstatus.username");
    this._password = plugin.getConfig().getString("serverstatus.password");
    
    initialize();
    update();
  }
  
  protected abstract void initialize();
  
  protected abstract void update();
  
  protected Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(this._connectionString, this._userName, this._password);
  }
  
  protected int executeQuery(String query)
  {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    
    int affectedRows = 0;
    
    try
    {
      connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      preparedStatement = connection.prepareStatement(query);
      affectedRows = preparedStatement.executeUpdate();
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
      


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
      
      if (connection != null)
      {
        try
        {
          connection.close();
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
      
      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    return affectedRows;
  }
  
  protected int executeStatement(PreparedStatement preparedStatement)
  {
    Connection connection = null;
    
    int affectedRows = 0;
    
    try
    {
      connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      affectedRows = preparedStatement.executeUpdate();
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
      


      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    return affectedRows;
  }
}
