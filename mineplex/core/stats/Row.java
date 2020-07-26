package mineplex.core.stats;

import mineplex.core.common.util.NautHashMap;
import mineplex.core.stats.column.Column;

public class Row
{
  public NautHashMap<String, Column<?>> Columns = new NautHashMap();
}
