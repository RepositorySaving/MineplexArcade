package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ComboAttack extends Skill
{
  private WeakHashMap<Player, Float> _repeat = new WeakHashMap();
  private WeakHashMap<Player, Long> _last = new WeakHashMap();
  
  public ComboAttack(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Each time you attack, your damage", 
      "increases by 1.", 
      "Maximum of #0#1 bonus damage.", 
      "", 
      "Not attacking for 2 seconds clears", 
      "your bonus damage." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    if (!this._repeat.containsKey(damager)) {
      this._repeat.put(damager, Float.valueOf(0.5F));
    }
    
    event.AddMod(damager.getName(), GetName(), ((Float)this._repeat.get(damager)).floatValue(), true);
    

    this._repeat.put(damager, Float.valueOf(Math.min(level, ((Float)this._repeat.get(damager)).floatValue() + 1.0F)));
    this._last.put(damager, Long.valueOf(System.currentTimeMillis()));
    

    damager.getWorld().playSound(damager.getLocation(), Sound.NOTE_STICKS, 1.0F, 0.7F + 0.3F * ((Float)this._repeat.get(damager)).floatValue());
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._repeat.keySet()) {
      if (UtilTime.elapsed(((Long)this._last.get(cur)).longValue(), 2000L))
        remove.add(cur);
    }
    for (Player cur : remove)
    {
      this._repeat.remove(cur);
      this._last.remove(cur);
    }
  }
  

  public void Reset(Player player)
  {
    this._repeat.remove(player);
    this._last.remove(player);
  }
}
