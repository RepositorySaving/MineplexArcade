package mineplex.core.chat.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ChatRepository
{
  private static Object _connectionLock = new Object();
  
  private String _connectionString;
  private String _userName = "root";
  private String _password = "tAbechAk3wR7tuTh";
  
  private static String CREATE_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS accountPreferences (id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(256), filterChat BOOL, PRIMARY KEY (id), UNIQUE INDEX uuid_index (uuid));";
  private static String SAVE_FILTER_VALUE = "UPDATE accountPreferences SET filterChat = ? WHERE uuid = ?;";
  private static String INSERT_ACCOUNT = "INSERT INTO accountPreferences (uuid, filterChat) VALUES (?, '1') ON DUPLICATE KEY UPDATE uuid=uuid;";
  private static String RETRIEVE_FILTER_VALUE = "SELECT filterChat FROM accountPreferences WHERE uuid = ?;";
  
  private Connection _connection = null;
  
  public ChatRepository(String connectionUrl)
  {
    this._connectionString = connectionUrl;
    
    initialize();
  }
  
  public void initialize()
  {
    PreparedStatement preparedStatement = null;
    
    try
    {
      this._connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      

      preparedStatement = this._connection.prepareStatement(CREATE_ACCOUNT_TABLE);
      preparedStatement.execute();
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
  
  /* Error */
  public void saveFilterChat(String uuid, boolean filterChat)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: iconst_0
    //   3: istore 4
    //   5: getstatic 23	mineplex/core/chat/repository/ChatRepository:_connectionLock	Ljava/lang/Object;
    //   8: dup
    //   9: astore 5
    //   11: monitorenter
    //   12: aload_0
    //   13: getfield 52	mineplex/core/chat/repository/ChatRepository:_connection	Ljava/sql/Connection;
    //   16: invokeinterface 102 1 0
    //   21: ifeq +22 -> 43
    //   24: aload_0
    //   25: aload_0
    //   26: getfield 54	mineplex/core/chat/repository/ChatRepository:_connectionString	Ljava/lang/String;
    //   29: aload_0
    //   30: getfield 46	mineplex/core/chat/repository/ChatRepository:_userName	Ljava/lang/String;
    //   33: aload_0
    //   34: getfield 50	mineplex/core/chat/repository/ChatRepository:_password	Ljava/lang/String;
    //   37: invokestatic 62	java/sql/DriverManager:getConnection	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;
    //   40: putfield 52	mineplex/core/chat/repository/ChatRepository:_connection	Ljava/sql/Connection;
    //   43: aload_0
    //   44: getfield 52	mineplex/core/chat/repository/ChatRepository:_connection	Ljava/sql/Connection;
    //   47: getstatic 31	mineplex/core/chat/repository/ChatRepository:SAVE_FILTER_VALUE	Ljava/lang/String;
    //   50: invokeinterface 68 2 0
    //   55: astore_3
    //   56: aload_3
    //   57: iconst_1
    //   58: iload_2
    //   59: invokeinterface 105 3 0
    //   64: aload_3
    //   65: iconst_2
    //   66: aload_1
    //   67: invokeinterface 109 3 0
    //   72: aload_3
    //   73: invokeinterface 113 1 0
    //   78: istore 4
    //   80: iload 4
    //   82: ifne +11 -> 93
    //   85: getstatic 117	java/lang/System:out	Ljava/io/PrintStream;
    //   88: ldc 123
    //   90: invokevirtual 125	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   93: aload 5
    //   95: monitorexit
    //   96: goto +62 -> 158
    //   99: aload 5
    //   101: monitorexit
    //   102: athrow
    //   103: astore 5
    //   105: aload 5
    //   107: invokevirtual 80	java/lang/Exception:printStackTrace	()V
    //   110: aload_3
    //   111: ifnull +67 -> 178
    //   114: aload_3
    //   115: invokeinterface 85 1 0
    //   120: goto +58 -> 178
    //   123: astore 7
    //   125: aload 7
    //   127: invokevirtual 88	java/sql/SQLException:printStackTrace	()V
    //   130: goto +48 -> 178
    //   133: astore 6
    //   135: aload_3
    //   136: ifnull +19 -> 155
    //   139: aload_3
    //   140: invokeinterface 85 1 0
    //   145: goto +10 -> 155
    //   148: astore 7
    //   150: aload 7
    //   152: invokevirtual 88	java/sql/SQLException:printStackTrace	()V
    //   155: aload 6
    //   157: athrow
    //   158: aload_3
    //   159: ifnull +19 -> 178
    //   162: aload_3
    //   163: invokeinterface 85 1 0
    //   168: goto +10 -> 178
    //   171: astore 7
    //   173: aload 7
    //   175: invokevirtual 88	java/sql/SQLException:printStackTrace	()V
    //   178: return
    // Line number table:
    //   Java source line #74	-> byte code offset #0
    //   Java source line #76	-> byte code offset #2
    //   Java source line #80	-> byte code offset #5
    //   Java source line #82	-> byte code offset #12
    //   Java source line #84	-> byte code offset #24
    //   Java source line #87	-> byte code offset #43
    //   Java source line #89	-> byte code offset #56
    //   Java source line #90	-> byte code offset #64
    //   Java source line #92	-> byte code offset #72
    //   Java source line #94	-> byte code offset #80
    //   Java source line #96	-> byte code offset #85
    //   Java source line #80	-> byte code offset #93
    //   Java source line #100	-> byte code offset #103
    //   Java source line #102	-> byte code offset #105
    //   Java source line #106	-> byte code offset #110
    //   Java source line #110	-> byte code offset #114
    //   Java source line #111	-> byte code offset #120
    //   Java source line #112	-> byte code offset #123
    //   Java source line #114	-> byte code offset #125
    //   Java source line #105	-> byte code offset #133
    //   Java source line #106	-> byte code offset #135
    //   Java source line #110	-> byte code offset #139
    //   Java source line #111	-> byte code offset #145
    //   Java source line #112	-> byte code offset #148
    //   Java source line #114	-> byte code offset #150
    //   Java source line #117	-> byte code offset #155
    //   Java source line #106	-> byte code offset #158
    //   Java source line #110	-> byte code offset #162
    //   Java source line #111	-> byte code offset #168
    //   Java source line #112	-> byte code offset #171
    //   Java source line #114	-> byte code offset #173
    //   Java source line #118	-> byte code offset #178
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	179	0	this	ChatRepository
    //   0	179	1	uuid	String
    //   0	179	2	filterChat	boolean
    //   1	162	3	preparedStatement	PreparedStatement
    //   3	78	4	affectedRows	int
    //   9	91	5	Ljava/lang/Object;	Object
    //   103	3	5	exception	Exception
    //   133	23	6	localObject1	Object
    //   123	3	7	e	SQLException
    //   148	3	7	e	SQLException
    //   171	3	7	e	SQLException
    // Exception table:
    //   from	to	target	type
    //   12	96	99	finally
    //   99	102	99	finally
    //   5	103	103	java/lang/Exception
    //   114	120	123	java/sql/SQLException
    //   5	110	133	finally
    //   139	145	148	java/sql/SQLException
    //   162	168	171	java/sql/SQLException
  }
  
  public boolean loadClientInformation(UUID uuid)
  {
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;
    
    try
    {
      synchronized (_connectionLock)
      {
        if (this._connection.isClosed())
        {
          this._connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
        }
        
        preparedStatement = this._connection.prepareStatement(RETRIEVE_FILTER_VALUE);
        preparedStatement.setString(1, uuid.toString());
        
        resultSet = preparedStatement.executeQuery();
        
        if (!resultSet.next())
        {
          preparedStatement.close();
          preparedStatement = this._connection.prepareStatement(INSERT_ACCOUNT);
          preparedStatement.setString(1, uuid.toString());
          
          preparedStatement.execute();
          



































          return true;
        }
        if (resultSet.next())
        {
          return resultSet.getBoolean(1);
        }
      }
      






      if (preparedStatement == null) {
        break label353;
      }
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
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
    try
    {
      preparedStatement.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    
    label353:
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
    

    return true;
  }
}
