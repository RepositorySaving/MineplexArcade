package mineplex.core.punish;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.NautHashMap;

public class PunishClient
{
  private NautHashMap<Category, List<Punishment>> _punishments;
  
  public PunishClient()
  {
    this._punishments = new NautHashMap();
  }
  
  public void AddPunishment(Category category, Punishment punishment)
  {
    if (!this._punishments.containsKey(category)) {
      this._punishments.put(category, new java.util.ArrayList());
    }
    ((List)this._punishments.get(category)).add(punishment);
  }
  
  public boolean IsBanned() {
    Iterator localIterator2;
    for (Iterator localIterator1 = this._punishments.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      List<Punishment> punishments = (List)localIterator1.next();
      
      localIterator2 = punishments.iterator(); continue;Punishment punishment = (Punishment)localIterator2.next();
      
      if (punishment.IsBanned())
      {
        return true;
      }
    }
    

    return false;
  }
  
  public boolean IsMuted() {
    Iterator localIterator2;
    for (Iterator localIterator1 = this._punishments.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      List<Punishment> punishments = (List)localIterator1.next();
      
      localIterator2 = punishments.iterator(); continue;Punishment punishment = (Punishment)localIterator2.next();
      
      if (punishment.IsMuted())
      {
        return true;
      }
    }
    

    return false;
  }
  
  public Punishment GetPunishment(PunishmentSentence sentence) {
    Iterator localIterator2;
    for (Iterator localIterator1 = this._punishments.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      List<Punishment> punishments = (List)localIterator1.next();
      
      localIterator2 = punishments.iterator(); continue;Punishment punishment = (Punishment)localIterator2.next();
      
      if ((sentence == PunishmentSentence.Ban) && (punishment.IsBanned()))
      {
        return punishment;
      }
      if ((sentence == PunishmentSentence.Mute) && (punishment.IsMuted()))
      {
        return punishment;
      }
    }
    

    return null;
  }
  
  public NautHashMap<Category, List<Punishment>> GetPunishments()
  {
    return this._punishments;
  }
}
