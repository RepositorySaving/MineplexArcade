package mineplex.core.punish;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import mineplex.core.common.util.NautHashMap;


public class PunishTrackUtil
{
  public static long GetPunishTime(PunishClient client, Category category, int severity)
  {
    int severityLimitOne = -1;
    int severityLimitTwo = -1;
    double algMod = 2.0D;
    
    switch (category)
    {
    case Advertisement: 
      severityLimitOne = 24;
      severityLimitOne = 72;
      algMod = -2.0D;
      break;
    case ChatOffense: 
      severityLimitOne = 48;
      severityLimitOne = 168;
      algMod = -1.0D;
      break;
    case Exploiting: 
      severityLimitOne = 48;
      severityLimitOne = 168;
      algMod = 0.0D;
      break;
    case Hacking: 
      severityLimitOne = -1;
      severityLimitOne = -1;
      algMod = 1.0D;
      break;
    }
    
    

    List<Map.Entry<Category, Punishment>> punishments = new ArrayList();
    
    if (client.GetPunishments().containsKey(category))
    {
      for (Punishment punishment : (List)client.GetPunishments().get(category))
      {
        punishments.add(new AbstractMap.SimpleEntry(category, punishment));
      }
    }
    
    Collections.sort(punishments, new PunishmentSorter());
    
    long timeOfLastInfraction = Math.min(punishments.size() > 0 ? (System.currentTimeMillis() - ((Punishment)((Map.Entry)punishments.get(0)).getValue()).GetTime()) / 86400000L : 180L, 180L);
    
    long punishTime = (Math.pow(2.0D, algMod + (severity - 1) * 2) * 24.0D) + (180L - timeOfLastInfraction) / 3L;
    
    return severity < 3 ? Math.min(punishTime, severityLimitTwo == -1 ? punishTime : severity == 1 ? severityLimitOne : severityLimitOne == -1 ? punishTime : severityLimitTwo) : punishTime;
  }
}
