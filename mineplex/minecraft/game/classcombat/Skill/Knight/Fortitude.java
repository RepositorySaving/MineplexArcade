package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class Fortitude
  extends Skill
{
  private WeakHashMap<Player, Double> _preHealth = new WeakHashMap();
  private WeakHashMap<Player, Integer> _health = new WeakHashMap();
  private WeakHashMap<Player, Long> _last = new WeakHashMap();
  
  public Fortitude(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "After taking damage, you regenerate", 
      "up to #0#1 of the health you lost.", 
      "", 
      "You restore health at a rate of", 
      "1 health per #4.5#-0.5 seconds.", 
      "", 
      "This does not stack, and is reset if", 
      "you are hit again." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void RegisterPre(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (getLevel(damagee) <= 0) {
      return;
    }
    this._preHealth.put(damagee, Double.valueOf(damagee.getHealth()));
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void RegisterLast(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    if (!this._preHealth.containsKey(damagee)) {
      return;
    }
    double diff = ((Double)this._preHealth.remove(damagee)).doubleValue() - damagee.getHealth();
    
    this._health.put(damagee, Integer.valueOf(Math.min(level, (int)(diff + 0.5D))));
    this._last.put(damagee, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._health.keySet())
    {
      int level = getLevel(cur);
      if (level != 0)
      {
        if (UtilTime.elapsed(((Long)this._last.get(cur)).longValue(), 4500 - 500 * level))
        {
          this._health.put(cur, Integer.valueOf(((Integer)this._health.get(cur)).intValue() - 1));
          this._last.put(cur, Long.valueOf(System.currentTimeMillis()));
          
          if (((Integer)this._health.get(cur)).intValue() <= 0) {
            remove.add(cur);
          }
          
          UtilPlayer.health(cur, 1.0D);
          

          UtilParticle.PlayParticle(UtilParticle.ParticleType.HEART, cur.getEyeLocation(), 0.0F, 0.2F, 0.0F, 0.0F, 1);
        }
      }
    }
    for (Player cur : remove)
    {
      this._health.remove(cur);
      this._last.remove(cur);
    }
  }
  

  public void Reset(Player player)
  {
    this._preHealth.remove(player);
    this._health.remove(player);
    this._last.remove(player);
  }
}
