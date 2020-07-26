package mineplex.minecraft.game.core.combat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class CombatLog
{
  private LinkedList<CombatComponent> _damager = new LinkedList();
  
  private CombatComponent _player;
  private long _expireTime;
  private long _deathTime = 0L;
  
  private CombatComponent _killer;
  private int _assistants;
  private String _killedColor = ChatColor.YELLOW;
  private String _killerColor = ChatColor.YELLOW;
  
  protected CombatComponent LastDamager;
  protected long _lastDamaged;
  protected long _lastCombat;
  
  public CombatLog(Player player, long expireTime)
  {
    this._expireTime = expireTime;
    this._player = new CombatComponent(player.getName(), player);
  }
  
  public LinkedList<CombatComponent> GetAttackers()
  {
    return this._damager;
  }
  
  public CombatComponent GetPlayer()
  {
    return this._player;
  }
  


  public void Attacked(String damagerName, double damage, LivingEntity damagerEnt, String attackName)
  {
    CombatComponent comp = GetEnemy(damagerName, damagerEnt);
    
    comp.AddDamage(attackName, damage);
    

    this.LastDamager = comp;
    this._lastDamaged = System.currentTimeMillis();
    this._lastCombat = System.currentTimeMillis();
  }
  
  public CombatComponent GetEnemy(String name, LivingEntity ent)
  {
    ExpireOld();
    
    CombatComponent component = null;
    for (CombatComponent cur : this._damager)
    {
      if (cur.GetName().equals(name)) {
        component = cur;
      }
    }
    
    if (component != null)
    {
      this._damager.remove(component);
      this._damager.addFirst(component);
      return (CombatComponent)this._damager.getFirst();
    }
    
    this._damager.addFirst(new CombatComponent(name, ent));
    return (CombatComponent)this._damager.getFirst();
  }
  
  public void ExpireOld()
  {
    int expireFrom = -1;
    for (int i = 0; i < this._damager.size(); i++)
    {
      if (UtilTime.elapsed(((CombatComponent)this._damager.get(i)).GetLastDamage(), this._expireTime))
      {
        expireFrom = i;
        break;
      }
    }
    
    if (expireFrom != -1) {
      while (this._damager.size() > expireFrom)
        this._damager.remove(expireFrom);
    }
  }
  
  public LinkedList<String> Display() {
    LinkedList<String> out = new LinkedList();
    
    for (int i = 0; i < 8; i++)
    {
      if (i < this._damager.size()) {
        out.add(F.desc("#" + i, ((CombatComponent)this._damager.get(i)).Display(this._deathTime)));
      }
    }
    return out;
  }
  
  public LinkedList<String> DisplayAbsolute()
  {
    HashMap<Long, String> components = new HashMap();
    Iterator localIterator2;
    for (Iterator localIterator1 = this._damager.iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      CombatComponent cur = (CombatComponent)localIterator1.next();
      
      localIterator2 = cur.GetDamage().iterator(); continue;CombatDamage dmg = (CombatDamage)localIterator2.next();
      
      components.put(Long.valueOf(dmg.GetTime()), cur.Display(this._deathTime, dmg));
    }
    

    int id = components.size();
    Object out = new LinkedList();
    
    while (!components.isEmpty())
    {
      long bestTime = 0L;
      String bestString = null;
      
      for (Iterator localIterator3 = components.keySet().iterator(); localIterator3.hasNext();) { long time = ((Long)localIterator3.next()).longValue();
        
        if ((time > bestTime) || (bestString == null))
        {
          bestTime = time;
          bestString = (String)components.get(Long.valueOf(time));
        }
      }
      
      components.remove(Long.valueOf(bestTime));
      
      ((LinkedList)out).addFirst(F.desc("#" + id, bestString));
      id--;
    }
    
    return out;
  }
  
  public CombatComponent GetKiller()
  {
    return this._killer;
  }
  
  public void SetKiller(CombatComponent killer)
  {
    this._killer = killer;
  }
  
  public int GetAssists()
  {
    return this._assistants;
  }
  
  public void SetAssists(int assistants)
  {
    this._assistants = assistants;
  }
  
  public CombatComponent GetLastDamager()
  {
    return this.LastDamager;
  }
  
  public long GetLastDamaged()
  {
    return this._lastDamaged;
  }
  
  public long GetLastCombat()
  {
    return this._lastCombat;
  }
  
  public void SetLastCombat(long time)
  {
    this._lastCombat = time;
  }
  
  public long GetDeathTime()
  {
    return this._deathTime;
  }
  
  public void SetDeathTime(long deathTime)
  {
    this._deathTime = deathTime;
  }
  
  public String GetKilledColor()
  {
    return this._killedColor;
  }
  
  public void SetKilledColor(String color)
  {
    this._killedColor = color;
  }
  
  public String GetKillerColor()
  {
    return this._killerColor;
  }
  
  public void SetKillerColor(String color)
  {
    this._killerColor = color;
  }
}
