package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;
import java.util.WeakHashMap;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Vengeance
  extends Skill
{
  private WeakHashMap<Player, HashMap<String, Integer>> _vengeance = new WeakHashMap();
  
  public Vengeance(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "When you attack someone, your damage", 
      "is increased by #0#0.5 for each time the", 
      "enemy hurt you since you last hit them,", 
      "up to a maximum of #0#1 bonus damage." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void RegisterDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) && (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE)) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this._vengeance.containsKey(damagee)) {
      this._vengeance.put(damagee, new HashMap());
    }
    
    if (!((HashMap)this._vengeance.get(damagee)).containsKey(damager.getName()))
    {

      if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
        ((HashMap)this._vengeance.get(damagee)).put(damager.getName(), Integer.valueOf(0));
      } else {
        ((HashMap)this._vengeance.get(damagee)).put(damager.getName(), Integer.valueOf(1));
      }
    }
    else {
      ((HashMap)this._vengeance.get(damagee)).put(damager.getName(), Integer.valueOf(((Integer)((HashMap)this._vengeance.get(damagee)).get(damager.getName())).intValue() + 1));
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void IncreaseDamage(CustomDamageEvent event)
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
    if (!this._vengeance.containsKey(damager)) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!((HashMap)this._vengeance.get(damager)).containsKey(damagee.getName())) {
      return;
    }
    if (((Integer)((HashMap)this._vengeance.get(damager)).get(damagee.getName())).intValue() == 0) {
      return;
    }
    int hits = ((Integer)((HashMap)this._vengeance.get(damager)).remove(damagee.getName())).intValue();
    
    double damage = hits * (0.5D * level);
    
    damage = Math.min(damage, level * 1);
    

    event.AddMod(damager.getName(), GetName(), damage, true);
  }
  

  public void Reset(Player player)
  {
    this._vengeance.remove(player);
  }
}
