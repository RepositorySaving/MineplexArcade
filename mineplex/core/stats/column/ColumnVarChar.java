package mineplex.core.stats.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnVarChar extends Column<String>
{
  public int Length = 25;
  
  public ColumnVarChar(String name, int length)
  {
    this(name, length, "");
  }
  
  public ColumnVarChar(String name, int length, String value)
  {
    super(name);
    
    this.Length = length;
    this.Value = value;
  }
  
  public String getCreateString()
  {
    return this.Name + " VARCHAR(" + this.Length + ")";
  }
  
  public String getValue(ResultSet resultSet)
    throws SQLException
  {
    return resultSet.getString(this.Name);
  }
  

  public ColumnVarChar clone()
  {
    return new ColumnVarChar(this.Name, this.Length, (String)this.Value);
  }
}
