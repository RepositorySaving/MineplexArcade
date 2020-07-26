package mineplex.core.stats;

import java.util.Set;
import mineplex.core.common.util.NautHashMap;


public class PlayerStats
{
  private NautHashMap<String, Integer> _statHash = new NautHashMap();
  
  public void addStat(String statName, int value)
  {
    if (!this._statHash.containsKey(statName))
    {
      this._statHash.put(statName, Integer.valueOf(0));
    }
    
    this._statHash.put(statName, Integer.valueOf(((Integer)this._statHash.get(statName)).intValue() + value));
  }
  
  public int getStat(String statName)
  {
    return this._statHash.containsKey(statName) ? ((Integer)this._statHash.get(statName)).intValue() : 0;
  }
  
  public Set<String> getStatsNames()
  {
    return this._statHash.keySet();
  }
}
