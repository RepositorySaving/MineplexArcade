package mineplex.minecraft.game.core.combat;

import java.util.HashMap;
import java.util.LinkedList;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;



public class CombatComponent
{
  private boolean _player = false;
  
  private LinkedList<CombatDamage> _damage;
  
  protected String EntityName;
  protected long LastDamage = 0L;
  
  public CombatComponent(String name, LivingEntity ent)
  {
    this.EntityName = name;
    
    if (ent != null)
    {
      if ((ent instanceof Player))
      {
        this._player = true;
      }
    }
  }
  
  public void AddDamage(String source, double dmg)
  {
    if (source == null) {
      source = "-";
    }
    GetDamage().addFirst(new CombatDamage(source, dmg));
    this.LastDamage = System.currentTimeMillis();
  }
  
  public String GetName()
  {
    if (this.EntityName.equals("Null")) {
      return "World";
    }
    return this.EntityName;
  }
  
  public LinkedList<CombatDamage> GetDamage()
  {
    if (this._damage == null) {
      this._damage = new LinkedList();
    }
    return this._damage;
  }
  
  public String GetReason()
  {
    if (this._damage.isEmpty()) {
      return null;
    }
    return ((CombatDamage)this._damage.get(0)).GetName();
  }
  
  public long GetLastDamage()
  {
    return this.LastDamage;
  }
  
  public int GetTotalDamage()
  {
    int total = 0;
    for (CombatDamage cur : GetDamage())
      total = (int)(total + cur.GetDamage());
    return total;
  }
  
  public String GetBestWeapon()
  {
    HashMap<String, Integer> cumulative = new HashMap();
    String weapon = null;
    int best = 0;
    for (CombatDamage cur : this._damage)
    {
      int dmg = 0;
      if (cumulative.containsKey(cur.GetName())) {
        dmg = ((Integer)cumulative.get(cur.GetName())).intValue();
      }
      cumulative.put(cur.GetName(), Integer.valueOf(dmg));
      
      if (dmg >= best) {
        weapon = cur.GetName();
      }
    }
    return weapon;
  }
  

  public String Display(long _deathTime)
  {
    String time = "";
    if (_deathTime == 0L) {
      time = 
      
        UtilTime.convertString(System.currentTimeMillis() - this.LastDamage, 1, UtilTime.TimeUnit.FIT) + " Ago";
    } else {
      time = 
        UtilTime.convertString(_deathTime - this.LastDamage, 1, UtilTime.TimeUnit.FIT) + " Prior";
    }
    return 
    
      F.name(this.EntityName) + " [" + F.elem(new StringBuilder(String.valueOf(GetTotalDamage())).append("dmg").toString()) + "] [" + F.elem(GetBestWeapon()) + "]  [" + F.time(time) + "]";
  }
  

  public String Display(long _deathTime, CombatDamage damage)
  {
    String time = "";
    if (_deathTime == 0L) {
      time = 
      
        UtilTime.convertString(System.currentTimeMillis() - damage.GetTime(), 1, UtilTime.TimeUnit.FIT) + " Ago";
    } else {
      time = 
        UtilTime.convertString(_deathTime - damage.GetTime(), 1, UtilTime.TimeUnit.FIT) + " Prior";
    }
    
    return 
    
      F.name(this.EntityName) + " [" + F.elem(new StringBuilder(String.valueOf(damage.GetDamage())).append(" dmg").toString()) + "] [" + F.elem(damage.GetName()) + "]  [" + F.time(time) + "]";
  }
  
  public boolean IsPlayer()
  {
    return this._player;
  }
  
  public String GetLastDamageSource()
  {
    return ((CombatDamage)this._damage.getFirst()).GetName();
  }
}
