package mineplex.core.status;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ServerStatusRepository
{
  private String _connectionString;
  private String _userName;
  private String _password;
  private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ServerStatus (id INT NOT NULL AUTO_INCREMENT, serverName VARCHAR(256), serverGroup VARCHAR(256), address VARCHAR(256), updated LONG, lastTimeWithPlayers LONG, motd VARCHAR(256), players INT, maxPlayers INT, tps INT, ram INT, maxRam INT, PRIMARY KEY (id));";
  private static String INSERT_PLAYER_COUNT = "INSERT INTO ServerStatus (serverName, serverGroup, address, port, updated, motd, players, maxPlayers, tps, ram, maxRam) values(?, ?, ?, ?, now(), 'Configuring server.', ?, ?, 0, ?, ?);";
  private static String UPDATE_PLAYER_COUNT_WITH_PLAYERS = "UPDATE ServerStatus SET updated = now(), serverName = ?, serverGroup = ?, motd = ?, players = ?, maxPlayers = ?, tps = ?, ram = ?, maxRam = ?, lastTimeWithPlayers = now() WHERE id = ?;";
  private static String UPDATE_PLAYER_COUNT_WITHOUT_PLAYERS = "UPDATE ServerStatus SET updated = now(), serverName = ?, serverGroup = ?, motd = ?, players = ?, maxPlayers = ?, tps = ?, ram = ?, maxRam = ? WHERE id = ?;";
  private static String RETRIEVE_ID = "SELECT id FROM ServerStatus WHERE address = ? AND port = ?;";
  private static String RETRIEVE_SERVER_STATUSES = "SELECT ServerStatus.serverName, motd, players, maxPlayers, now(), updated FROM ServerStatus INNER JOIN DynamicServers ON ServerStatus.address = DynamicServers.privateAddress WHERE DynamicServers.US = ?";
  
  private int _id = -1;
  private boolean _us;
  private String _serverName;
  private String _serverGroup;
  private String _address;
  private String _port;
  private int _maxPlayers = 0;
  
  Connection _connection = null;
  
  public ServerStatusRepository(String connectionUrl, String username, String password, boolean us, String serverName, String serverGroup, String address, String port, int maxPlayers)
  {
    this._connectionString = connectionUrl;
    this._userName = username;
    this._password = password;
    this._us = us;
    this._serverName = serverName;
    this._serverGroup = serverGroup;
    this._address = address;
    this._port = port;
    this._maxPlayers = maxPlayers;
  }
  
  public void initialize()
  {
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatementRetrieve = null;
    PreparedStatement preparedStatementInsert = null;
    
    try
    {
      this._connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      

      preparedStatement = this._connection.prepareStatement(CREATE_TABLE);
      preparedStatement.execute();
      


      preparedStatementRetrieve = this._connection.prepareStatement(RETRIEVE_ID);
      preparedStatementRetrieve.setString(1, this._address);
      preparedStatementRetrieve.setString(2, this._port);
      resultSet = preparedStatementRetrieve.executeQuery();
      
      while (resultSet.next())
      {
        this._id = resultSet.getInt("id");
      }
      

      if (this._id == -1)
      {
        preparedStatementInsert = this._connection.prepareStatement(INSERT_PLAYER_COUNT, 1);
        
        preparedStatementInsert.setString(1, this._serverName);
        preparedStatementInsert.setString(2, this._serverGroup);
        preparedStatementInsert.setString(3, this._address);
        preparedStatementInsert.setString(4, this._port);
        preparedStatementInsert.setInt(5, 0);
        preparedStatementInsert.setInt(6, this._maxPlayers);
        preparedStatementInsert.setInt(7, (int)((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1048576L));
        preparedStatementInsert.setInt(8, (int)(Runtime.getRuntime().maxMemory() / 1048576L));
        
        int affectedRows = preparedStatementInsert.executeUpdate();
        
        if (affectedRows == 0)
        {
          throw new SQLException("Creating server status failed, no rows affected.");
        }
        
        resultSet.close();
        
        resultSet = preparedStatementInsert.getGeneratedKeys();
        
        if (resultSet.next())
        {
          this._id = resultSet.getInt(1);
        }
      }
      

      updatePlayerCountInDatabase("Configuring server.", 0, this._maxPlayers, 20);
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
      
      if (preparedStatementRetrieve != null)
      {
        try
        {
          preparedStatementRetrieve.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      
      if (preparedStatementInsert != null)
      {
        try
        {
          preparedStatementInsert.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      
      if (resultSet != null)
      {
        try
        {
          resultSet.close();
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
      
      if (preparedStatementRetrieve != null)
      {
        try
        {
          preparedStatementRetrieve.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      
      if (preparedStatementInsert != null)
      {
        try
        {
          preparedStatementInsert.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      
      if (resultSet != null)
      {
        try
        {
          resultSet.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public boolean updatePlayerCountInDatabase(String motd, int players, int maxPlayers, int tps)
  {
    PreparedStatement preparedStatement = null;
    
    try
    {
      if (this._connection.isClosed())
      {
        this._connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      
      preparedStatement = this._connection.prepareStatement(players != 0 ? UPDATE_PLAYER_COUNT_WITH_PLAYERS : UPDATE_PLAYER_COUNT_WITHOUT_PLAYERS, 1);
      
      preparedStatement.setString(1, this._serverName);
      preparedStatement.setString(2, this._serverGroup);
      preparedStatement.setString(3, motd);
      preparedStatement.setInt(4, players);
      preparedStatement.setInt(5, maxPlayers);
      preparedStatement.setInt(6, tps);
      preparedStatement.setInt(7, (int)((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1048576L));
      preparedStatement.setInt(8, (int)(Runtime.getRuntime().maxMemory() / 1048576L));
      preparedStatement.setInt(9, this._id);
      
      int affectedRows = preparedStatement.executeUpdate();
      
      if (affectedRows == 0)
      {
        throw new SQLException("Updating server status failed, no rows affected.");
      }
      
      return true;
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
      return false;
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
  
  public List<ServerStatusData> retrieveServerStatuses()
  {
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;
    List<ServerStatusData> serverData = new ArrayList();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    try
    {
      if (this._connection.isClosed())
      {
        this._connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      
      preparedStatement = this._connection.prepareStatement(RETRIEVE_SERVER_STATUSES);
      preparedStatement.setBoolean(1, this._us);
      
      resultSet = preparedStatement.executeQuery();
      
      while (resultSet.next())
      {
        ServerStatusData serverStatusData = new ServerStatusData();
        
        serverStatusData.Name = resultSet.getString(1);
        serverStatusData.Motd = resultSet.getString(2);
        serverStatusData.Players = resultSet.getInt(3);
        serverStatusData.MaxPlayers = resultSet.getInt(4);
        long current = dateFormat.parse(resultSet.getString(5)).getTime();
        long updated = dateFormat.parse(resultSet.getString(6)).getTime();
        
        if (current - updated < 10000L) {
          serverData.add(serverStatusData);
        }
      }
    }
    catch (Exception exception) {
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
      
      if (resultSet != null)
      {
        try
        {
          resultSet.close();
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
      
      if (resultSet != null)
      {
        try
        {
          resultSet.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    return serverData;
  }
}
