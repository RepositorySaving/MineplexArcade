package mineplex.core.energy;

import java.util.HashMap;
import java.util.Iterator;

public class ClientEnergy
{
  public double Energy;
  public long LastEnergy;
  public HashMap<String, Integer> MaxEnergyMods = new HashMap();
  public HashMap<String, Integer> SwingEnergyMods = new HashMap();
  
  public int EnergyBonus()
  {
    int bonus = 0;
    
    for (Iterator localIterator = this.MaxEnergyMods.values().iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
      bonus += i;
    }
    return bonus;
  }
  
  public int SwingEnergy()
  {
    int mod = 0;
    
    for (Iterator localIterator = this.SwingEnergyMods.values().iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
      mod += i;
    }
    return Math.max(0, 4 + mod);
  }
}
