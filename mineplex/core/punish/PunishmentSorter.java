package mineplex.core.punish;

import java.util.Comparator;
import java.util.Map.Entry;

public class PunishmentSorter implements Comparator<Map.Entry<Category, Punishment>>
{
  public int compare(Map.Entry<Category, Punishment> a, Map.Entry<Category, Punishment> b)
  {
    if (((Punishment)a.getValue()).GetTime() > ((Punishment)b.getValue()).GetTime()) {
      return -1;
    }
    if (((Punishment)a.getValue()).GetTime() == ((Punishment)b.getValue()).GetTime()) {
      return 0;
    }
    return 1;
  }
}
