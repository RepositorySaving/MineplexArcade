package mineplex.core.stats;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.stats.column.Column;


public class Table
{
  private static Connection _connection;
  private String _connectionString = "jdbc:mysql://db.mineplex.com:3306/Mineplex?autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
  private String _userName = "root";
  private String _password = "tAbechAk3wR7tuTh";
  
  private String _name;
  private List<Column<?>> _primaryKeys;
  private NautHashMap<String, Column<?>> _columns = new NautHashMap();
  private Column<?> _index;
  
  public Table(String name, List<Column<?>> primaryKeys, List<Column<?>> columns, Column<?> index)
  {
    this._name = name;
    this._primaryKeys = primaryKeys;
    
    for (Column<?> column : columns)
    {
      this._columns.put(column.Name, column);
    }
    
    this._index = index;
  }
  
  public void initialize()
  {
    if (!doesTableExist())
    {
      create();
    }
  }
  






















































  private void create()
  {
    PreparedStatement createStatement = null;
    
    try
    {
      StringBuilder columnBuilder = new StringBuilder();
      
      for (Iterator<Column<?>> columnIterator = this._columns.values().iterator(); columnIterator.hasNext();)
      {
        Column<?> column = (Column)columnIterator.next();
        
        columnBuilder.append(column.getCreateString());
        
        if (columnIterator.hasNext())
        {
          columnBuilder.append(", ");
        }
      }
      
      StringBuilder primaryKey = new StringBuilder();
      
      for (Column<?> column : this._primaryKeys)
      {
        primaryKey.append(column.Name);
        
        if (!column.equals(this._primaryKeys.get(this._primaryKeys.size() - 1)))
        {
          primaryKey.append(", ");
        }
      }
      
      if ((_connection == null) || (_connection.isClosed())) {
        _connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      createStatement = _connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this._name + "` (" + columnBuilder.toString() + ", PRIMARY KEY (" + primaryKey.toString() + "), INDEX (" + this._index.Name + "));");
      
      createStatement.execute();
    }
    catch (Exception exception)
    {
      System.out.println("Error creating table `" + this._name + "`.");
      exception.printStackTrace();
      


      if (createStatement != null)
      {
        try
        {
          createStatement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (createStatement != null)
      {
        try
        {
          createStatement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  

  private boolean doesTableExist()
  {
    PreparedStatement checkIfTableExistsStatement = null;
    
    try
    {
      if ((_connection == null) || (_connection.isClosed())) {
        _connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      checkIfTableExistsStatement = _connection.prepareStatement("SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = 'Mineplex' AND table_name LIKE '`" + this._name + "`'");
      
      if (checkIfTableExistsStatement.executeQuery().next()) {
        return true;
      }
    }
    catch (Exception exception) {
      System.out.println("Error updating table `" + this._name + "`.");
      exception.printStackTrace();
    }
    finally
    {
      if (checkIfTableExistsStatement != null)
      {
        try
        {
          checkIfTableExistsStatement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
    if (checkIfTableExistsStatement != null)
    {
      try
      {
        checkIfTableExistsStatement.close();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    

    return false;
  }
  
  public void insert(List<Column<?>> columns)
  {
    StringBuilder temporaryBuilder = new StringBuilder();
    StringBuilder questionBuilder = new StringBuilder();
    StringBuilder updateBuilder = new StringBuilder();
    
    for (Column<?> column : columns)
    {
      temporaryBuilder.append(column.Name);
      questionBuilder.append("'" + column.Value + "'");
      updateBuilder.append(column.Name + " = VALUES(" + column.Name + ")");
      
      if (!column.equals(columns.get(columns.size() - 1)))
      {
        temporaryBuilder.append(", ");
        questionBuilder.append(", ");
        updateBuilder.append(", ");
      }
    }
    
    PreparedStatement preparedStatement = null;
    
    try
    {
      if ((_connection == null) || (_connection.isClosed())) {
        _connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      preparedStatement = _connection.prepareStatement("INSERT INTO `" + this._name + "` (" + temporaryBuilder.toString() + ") VALUES (" + questionBuilder.toString() + ") ON DUPLICATE KEY UPDATE " + updateBuilder.toString() + ";", 1);
      
      preparedStatement.execute();
    }
    catch (Exception exception)
    {
      System.out.println("Error updating table `" + this._name + "`.");
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
  
  public boolean update(List<Column<?>> columns, Column<?> whereColumn)
  {
    List<Column<?>> whereColumnList = new ArrayList();
    whereColumnList.add(whereColumn);
    
    return update(columns, whereColumnList);
  }
  
  public boolean update(List<Column<?>> columns, List<Column<?>> whereColumns)
  {
    String updateStatement = buildUpdateStatement(columns, whereColumns);
    
    PreparedStatement preparedStatement = null;
    
    try
    {
      if ((_connection == null) || (_connection.isClosed())) {
        _connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      preparedStatement = _connection.prepareStatement(updateStatement);
      
      if (preparedStatement.executeUpdate() != 0) {
        return true;
      }
    }
    catch (Exception exception) {
      System.out.println("Error updating table `" + this._name + "`.");
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
    }
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
    

    return false;
  }
  
  public List<Row> retrieve(List<Column<?>> columns)
  {
    StringBuilder temporaryBuilder = new StringBuilder();
    
    for (Iterator<Column<?>> columnIterator = this._columns.values().iterator(); columnIterator.hasNext();)
    {
      Column<?> column = (Column)columnIterator.next();
      temporaryBuilder.append(column.Name);
      
      if (columnIterator.hasNext()) {
        temporaryBuilder.append(", ");
      }
    }
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<Row> rows = new ArrayList();
    
    try
    {
      if ((_connection == null) || (_connection.isClosed())) {
        _connection = DriverManager.getConnection(this._connectionString, this._userName, this._password);
      }
      preparedStatement = _connection.prepareStatement("Select " + temporaryBuilder.toString() + " FROM `" + this._name + "` " + buildWhereString(columns) + ";");
      
      resultSet = preparedStatement.executeQuery();
      
      while (resultSet.next())
      {
        Row row = new Row();
        
        for (Column<?> column : columns)
        {
          column.getValue(resultSet);
          row.Columns.put(column.Name, column);
        }
        
        rows.add(row);
      }
    }
    catch (Exception exception)
    {
      System.out.println("Error updating table `" + this._name + "`.");
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
    
    return rows;
  }
  
  private String buildUpdateStatement(List<Column<?>> columns, List<Column<?>> whereColumns)
  {
    StringBuilder setBuilder = new StringBuilder();
    
    if (columns.size() > 0) {
      setBuilder.append("SET ");
    }
    for (Column<?> column : columns)
    {
      setBuilder.append(column.Name + " = '" + column.Value + "'");
      
      if (!column.equals(columns.get(columns.size() - 1))) {
        setBuilder.append(", ");
      }
    }
    return "UPDATE `" + this._name + "` " + setBuilder.toString() + " " + buildWhereString(whereColumns) + ";";
  }
  
  private String buildWhereString(List<Column<?>> columns)
  {
    StringBuilder whereBuilder = new StringBuilder();
    
    if (columns.size() > 0)
    {
      whereBuilder.append("WHERE ");
    }
    
    for (Column<?> column : columns)
    {
      whereBuilder.append(column.Name + " = '" + column.Value + "'");
      
      if (!column.equals(columns.get(columns.size() - 1))) {
        whereBuilder.append(" AND ");
      }
    }
    return whereBuilder.toString();
  }
  
  public Column<?> getColumn(String columnName)
  {
    return (Column)this._columns.get(columnName);
  }
  
  public Row createRow()
  {
    Row row = new Row();
    
    for (Column<?> column : this._columns.values())
    {
      row.Columns.put(column.Name, column);
    }
    
    return row;
  }
}
