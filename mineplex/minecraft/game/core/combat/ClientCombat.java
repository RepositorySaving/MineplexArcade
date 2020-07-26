package mineplex.minecraft.game.core.combat;

import java.util.LinkedList;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilTime;
import org.bukkit.entity.LivingEntity;



public class ClientCombat
{
  private LinkedList<CombatLog> _kills = new LinkedList();
  private LinkedList<CombatLog> _assists = new LinkedList();
  private LinkedList<CombatLog> _deaths = new LinkedList();
  
  private WeakHashMap<LivingEntity, Long> _lastHurt = new WeakHashMap();
  private WeakHashMap<LivingEntity, Long> _lastHurtBy = new WeakHashMap();
  private long _lastHurtByWorld = 0L;
  
  public LinkedList<CombatLog> GetKills()
  {
    return this._kills;
  }
  
  public LinkedList<CombatLog> GetAssists()
  {
    return this._assists;
  }
  
  public LinkedList<CombatLog> GetDeaths()
  {
    return this._deaths;
  }
  
  public boolean CanBeHurtBy(LivingEntity damager)
  {
    if (damager == null)
    {
      if (UtilTime.elapsed(this._lastHurtByWorld, 250L))
      {
        this._lastHurtByWorld = System.currentTimeMillis();
        return true;
      }
      

      return false;
    }
    

    if (!this._lastHurtBy.containsKey(damager))
    {
      this._lastHurtBy.put(damager, Long.valueOf(System.currentTimeMillis()));
      return true;
    }
    
    if (System.currentTimeMillis() - ((Long)this._lastHurtBy.get(damager)).longValue() > 400L)
    {
      this._lastHurtBy.put(damager, Long.valueOf(System.currentTimeMillis()));
      return true;
    }
    
    return false;
  }
  
  public boolean CanHurt(LivingEntity damagee)
  {
    if (damagee == null) {
      return true;
    }
    if (!this._lastHurt.containsKey(damagee))
    {
      this._lastHurt.put(damagee, Long.valueOf(System.currentTimeMillis()));
      return true;
    }
    
    if (System.currentTimeMillis() - ((Long)this._lastHurt.get(damagee)).longValue() > 400L)
    {
      this._lastHurt.put(damagee, Long.valueOf(System.currentTimeMillis()));
      return true;
    }
    
    return false;
  }
}
