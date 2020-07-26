package mineplex.core.stats.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnInt extends Column<Integer>
{
  public ColumnInt(String name)
  {
    super(name);
    this.Value = Integer.valueOf(0);
  }
  
  public ColumnInt(String name, int value)
  {
    super(name, Integer.valueOf(value));
  }
  

  public String getCreateString()
  {
    return this.Name + " INT";
  }
  
  public Integer getValue(ResultSet resultSet)
    throws SQLException
  {
    return Integer.valueOf(resultSet.getInt(this.Name));
  }
  

  public ColumnInt clone()
  {
    return new ColumnInt(this.Name, ((Integer)this.Value).intValue());
  }
}
