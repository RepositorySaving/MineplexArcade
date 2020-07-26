package mineplex.core.stats;

import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import mineplex.core.MiniPlugin;
import mineplex.core.account.event.RetrieveClientInformationEvent;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.stats.column.Column;
import mineplex.core.stats.column.ColumnInt;
import mineplex.core.stats.column.ColumnVarChar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsManager extends MiniPlugin
{
  private static Object _statSync = new Object();
  
  private NautHashMap<String, NautHashMap<String, Row>> _statUploadQueue = new NautHashMap();
  
  private Runnable _saveRunnable;
  private NautHashMap<String, Table> _tables = new NautHashMap();
  
  private NautHashMap<String, NautHashMap<String, Row>> _playerStatList = new NautHashMap();
  
  public StatsManager(JavaPlugin plugin)
  {
    super("StatsManager", plugin);
    
    if (this._saveRunnable == null)
    {
      this._saveRunnable = new Runnable()
      {
        public void run()
        {
          StatsManager.this.saveStats();
        }
        
      };
      plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this._saveRunnable, 200L, 200L);
    }
  }
  
  public StatsManager addTable(String tableName, String... columns)
  {
    if (!this._tables.containsKey(tableName))
    {
      ColumnVarChar playerColumn = new ColumnVarChar("playerName", 16);
      List<Column<?>> columnList = new ArrayList();
      columnList.add(playerColumn);
      
      for (String column : columns)
      {
        columnList.add(new ColumnInt(column));
      }
      
      Table statTable = new Table(tableName, columnList, columnList, playerColumn);
      statTable.initialize();
      

      this._tables.put(tableName, statTable);
    }
    
    if (!this._playerStatList.containsKey(tableName))
    {
      this._playerStatList.put(tableName, new NautHashMap());
    }
    
    return this;
  }
  
  public void addStat(Player player, String table, String statName, int value)
  {
    addStat(player.getName(), table, statName, value);
  }
  
  public void addStat(String playerName, String table, String statName, int value)
  {
    if (!this._playerStatList.containsKey(table))
    {
      System.out.println("Error adding stats for " + playerName + " on table " + table + " (" + statName + ", " + value + ") : TABLE DOES NOT EXIST!");
      return;
    }
    
    if (!((NautHashMap)this._playerStatList.get(table)).containsKey(playerName))
    {

      Row row = ((Table)this._tables.get(table)).createRow();
      
      ((ColumnVarChar)row.Columns.get("playerName")).Value = playerName;
      
      ((NautHashMap)this._playerStatList.get(table)).put(playerName, row);
    }
    
    ((Row)((NautHashMap)this._playerStatList.get(table)).get(playerName)).Columns.put(statName, new ColumnInt(statName, value));
    
    synchronized (_statSync)
    {
      if (!this._statUploadQueue.containsKey(table))
      {
        this._statUploadQueue.put(table, new NautHashMap());
      }
      
      ((NautHashMap)this._statUploadQueue.get(table)).put(playerName, (Row)((NautHashMap)this._playerStatList.get(table)).get(playerName));
    }
  }
  
  @EventHandler
  public void clearPlayerStatsOnLeave(PlayerQuitEvent event)
  {
    for (String table : this._playerStatList.keySet()) {
      ((NautHashMap)this._playerStatList.get(table)).remove(event.getPlayer().getName());
    }
  }
  
  @EventHandler
  public void loadPlayerStats(RetrieveClientInformationEvent event) {
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;
    
    for (Map.Entry<String, Table> tableEntry : this._tables.entrySet())
    {
      Table table = (Table)tableEntry.getValue();
      String tableName = (String)tableEntry.getKey();
      
      try
      {
        List<Column<?>> columnList = new ArrayList();
        columnList.add(new ColumnVarChar("playerName", 16, event.getPlayerName()));
        List<Row> rows = table.retrieve(columnList);
        
        Row row = table.createRow();
        ((ColumnVarChar)row.Columns.get("playerName")).Value = event.getPlayerName();
        
        if (rows.size() > 0)
        {
          for (Column<?> column : ((Row)rows.get(0)).Columns.values())
          {
            row.Columns.put(column.Name, column);
          }
        }
        
        ((NautHashMap)this._playerStatList.get(tableName)).put(event.getPlayerName(), row);
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
    }
  }
  
  protected void saveStats()
  {
    PreparedStatement preparedStatement = null;
    
    try
    {
      NautHashMap<String, NautHashMap<String, Row>> uploadQueue = new NautHashMap();
      
      synchronized (_statSync) {
        Iterator localIterator2;
        for (Iterator localIterator1 = this._statUploadQueue.keySet().iterator(); localIterator1.hasNext(); 
            


            localIterator2.hasNext())
        {
          key = (String)localIterator1.next();
          
          uploadQueue.put(key, new NautHashMap());
          
          localIterator2 = ((NautHashMap)this._statUploadQueue.get(key)).keySet().iterator(); continue;String stat = (String)localIterator2.next();
          
          ((NautHashMap)uploadQueue.get(key)).put(stat, (Row)((NautHashMap)this._statUploadQueue.get(key)).get(stat));
        }
        

        this._statUploadQueue.clear();
      }
      Object iterator;
      for (String key = uploadQueue.keySet().iterator(); key.hasNext(); 
          
          ((Iterator)iterator).hasNext())
      {
        String tableName = (String)key.next();
        
        iterator = ((NautHashMap)uploadQueue.get(tableName)).entrySet().iterator(); continue;
        
        Map.Entry<String, Row> entry = (Map.Entry)((Iterator)iterator).next();
        
        if (!((Table)this._tables.get(tableName)).update(new ArrayList(((Row)entry.getValue()).Columns.values()), new ColumnVarChar("playerName", 16, (String)entry.getKey()))) {
          ((Table)this._tables.get(tableName)).insert(new ArrayList(((Row)entry.getValue()).Columns.values()));
        }
        ((Iterator)iterator).remove();
      }
      
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
  
  public int getStat(Player player, String table, String stat)
  {
    return getStat(player.getName(), table, stat);
  }
  
  public int getStat(String player, String table, String stat)
  {
    if (!this._playerStatList.containsKey(table))
    {
      System.out.println("STATS ERROR: Table doesn't exist (" + table + ")");
      return 0;
    }
    
    if (!((NautHashMap)this._playerStatList.get(table)).containsKey(player))
    {
      System.out.println("STATS ERROR: Player doesn't exist (" + player + ")");
      return 0;
    }
    
    return ((Integer)((ColumnInt)((Row)((NautHashMap)this._playerStatList.get(table)).get(player)).Columns.get(stat)).Value).intValue();
  }
}
