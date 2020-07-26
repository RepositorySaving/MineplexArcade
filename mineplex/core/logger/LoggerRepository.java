package mineplex.core.logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoggerRepository
{
  private static Object _connectionLock = new Object();
  
  private String _connectionString;
  private String _userName = "root";
  private String _password = "tAbechAk3wR7tuTh";
  
  private static String CREATE_LOG_TABLE = "CREATE TABLE IF NOT EXISTS errorLog (id INT NOT NULL AUTO_INCREMENT, server VARCHAR(256), message VARCHAR(256), date LONG, PRIMARY KEY (id));";
  private static String INSERT_LOG = "INSERT INTO errorLog (server, message, date) VALUES (?, ?, now());";
  
  private Connection _connection = null;
  private String _serverName;
  
  public LoggerRepository(String connectionUrl, String serverName)
  {
    this._connectionString = connectionUrl;
    this._serverName = serverName;
    
    initialize();
  }
  
  public void initialize()
  {
    PreparedStatement preparedStatement = null;
    
    try
    {
      this._connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      

      preparedStatement = this._connection.prepareStatement(CREATE_LOG_TABLE);
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
  public void saveLog(String... message)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: getstatic 22	mineplex/core/logger/LoggerRepository:_connectionLock	Ljava/lang/Object;
    //   5: dup
    //   6: astore_3
    //   7: monitorenter
    //   8: aload_0
    //   9: getfield 43	mineplex/core/logger/LoggerRepository:_connection	Ljava/sql/Connection;
    //   12: invokeinterface 96 1 0
    //   17: ifeq +22 -> 39
    //   20: aload_0
    //   21: aload_0
    //   22: getfield 45	mineplex/core/logger/LoggerRepository:_connectionString	Ljava/lang/String;
    //   25: aload_0
    //   26: getfield 37	mineplex/core/logger/LoggerRepository:_userName	Ljava/lang/String;
    //   29: aload_0
    //   30: getfield 41	mineplex/core/logger/LoggerRepository:_password	Ljava/lang/String;
    //   33: invokestatic 56	java/sql/DriverManager:getConnection	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;
    //   36: putfield 43	mineplex/core/logger/LoggerRepository:_connection	Ljava/sql/Connection;
    //   39: aload_0
    //   40: getfield 43	mineplex/core/logger/LoggerRepository:_connection	Ljava/sql/Connection;
    //   43: getstatic 30	mineplex/core/logger/LoggerRepository:INSERT_LOG	Ljava/lang/String;
    //   46: invokeinterface 62 2 0
    //   51: astore_2
    //   52: aload_1
    //   53: dup
    //   54: astore 7
    //   56: arraylength
    //   57: istore 6
    //   59: iconst_0
    //   60: istore 5
    //   62: goto +54 -> 116
    //   65: aload 7
    //   67: iload 5
    //   69: aaload
    //   70: astore 4
    //   72: aload_2
    //   73: iconst_1
    //   74: aload_0
    //   75: getfield 47	mineplex/core/logger/LoggerRepository:_serverName	Ljava/lang/String;
    //   78: invokeinterface 99 3 0
    //   83: aload_2
    //   84: iconst_2
    //   85: aload 4
    //   87: iconst_0
    //   88: sipush 257
    //   91: aload 4
    //   93: invokevirtual 103	java/lang/String:length	()I
    //   96: invokestatic 109	java/lang/Math:min	(II)I
    //   99: invokevirtual 115	java/lang/String:substring	(II)Ljava/lang/String;
    //   102: invokeinterface 99 3 0
    //   107: aload_2
    //   108: invokeinterface 119 1 0
    //   113: iinc 5 1
    //   116: iload 5
    //   118: iload 6
    //   120: if_icmplt -55 -> 65
    //   123: aload_2
    //   124: invokeinterface 122 1 0
    //   129: pop
    //   130: aload_3
    //   131: monitorexit
    //   132: goto +59 -> 191
    //   135: aload_3
    //   136: monitorexit
    //   137: athrow
    //   138: astore_3
    //   139: aload_3
    //   140: invokevirtual 74	java/lang/Exception:printStackTrace	()V
    //   143: aload_2
    //   144: ifnull +67 -> 211
    //   147: aload_2
    //   148: invokeinterface 79 1 0
    //   153: goto +58 -> 211
    //   156: astore 9
    //   158: aload 9
    //   160: invokevirtual 82	java/sql/SQLException:printStackTrace	()V
    //   163: goto +48 -> 211
    //   166: astore 8
    //   168: aload_2
    //   169: ifnull +19 -> 188
    //   172: aload_2
    //   173: invokeinterface 79 1 0
    //   178: goto +10 -> 188
    //   181: astore 9
    //   183: aload 9
    //   185: invokevirtual 82	java/sql/SQLException:printStackTrace	()V
    //   188: aload 8
    //   190: athrow
    //   191: aload_2
    //   192: ifnull +19 -> 211
    //   195: aload_2
    //   196: invokeinterface 79 1 0
    //   201: goto +10 -> 211
    //   204: astore 9
    //   206: aload 9
    //   208: invokevirtual 82	java/sql/SQLException:printStackTrace	()V
    //   211: return
    // Line number table:
    //   Java source line #64	-> byte code offset #0
    //   Java source line #68	-> byte code offset #2
    //   Java source line #70	-> byte code offset #8
    //   Java source line #72	-> byte code offset #20
    //   Java source line #75	-> byte code offset #39
    //   Java source line #77	-> byte code offset #52
    //   Java source line #79	-> byte code offset #72
    //   Java source line #80	-> byte code offset #83
    //   Java source line #81	-> byte code offset #107
    //   Java source line #77	-> byte code offset #113
    //   Java source line #84	-> byte code offset #123
    //   Java source line #68	-> byte code offset #130
    //   Java source line #87	-> byte code offset #138
    //   Java source line #89	-> byte code offset #139
    //   Java source line #93	-> byte code offset #143
    //   Java source line #97	-> byte code offset #147
    //   Java source line #98	-> byte code offset #153
    //   Java source line #99	-> byte code offset #156
    //   Java source line #101	-> byte code offset #158
    //   Java source line #92	-> byte code offset #166
    //   Java source line #93	-> byte code offset #168
    //   Java source line #97	-> byte code offset #172
    //   Java source line #98	-> byte code offset #178
    //   Java source line #99	-> byte code offset #181
    //   Java source line #101	-> byte code offset #183
    //   Java source line #104	-> byte code offset #188
    //   Java source line #93	-> byte code offset #191
    //   Java source line #97	-> byte code offset #195
    //   Java source line #98	-> byte code offset #201
    //   Java source line #99	-> byte code offset #204
    //   Java source line #101	-> byte code offset #206
    //   Java source line #105	-> byte code offset #211
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	212	0	this	LoggerRepository
    //   0	212	1	message	String[]
    //   1	195	2	preparedStatement	PreparedStatement
    //   6	130	3	Ljava/lang/Object;	Object
    //   138	2	3	exception	Exception
    //   70	22	4	msg	String
    //   60	61	5	i	int
    //   57	64	6	j	int
    //   54	12	7	arrayOfString	String[]
    //   166	23	8	localObject1	Object
    //   156	3	9	e	SQLException
    //   181	3	9	e	SQLException
    //   204	3	9	e	SQLException
    // Exception table:
    //   from	to	target	type
    //   8	132	135	finally
    //   135	137	135	finally
    //   2	138	138	java/lang/Exception
    //   147	153	156	java/sql/SQLException
    //   2	143	166	finally
    //   172	178	181	java/sql/SQLException
    //   195	201	204	java/sql/SQLException
  }
}
