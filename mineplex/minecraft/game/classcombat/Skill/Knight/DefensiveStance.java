package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class DefensiveStance extends SkillActive
{
  private HashMap<Player, Long> _useTime = new HashMap();
  










  public DefensiveStance(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "While Blocking, you are immune to all", 
      "damage from attacks infront of you." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    return true;
  }
  

  public void Skill(Player player, int level)
  {
    this._useTime.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void AntiTurtle(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) && (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE)) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this._useTime.containsKey(damager)) {
      return;
    }
    if (UtilTime.elapsed(((Long)this._useTime.get(damager)).longValue(), 400L)) {
      return;
    }
    event.SetCancelled(GetName() + " Attack");
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damagee(CustomDamageEvent event)
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
    if (!damagee.isBlocking()) {
      return;
    }
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    org.bukkit.entity.LivingEntity damager = event.GetDamagerEntity(true);
    if (damager == null) { return;
    }
    Vector look = damagee.getLocation().getDirection();
    look.setY(0);
    look.normalize();
    
    Vector from = UtilAlg.getTrajectory(damagee, damager);
    from.normalize();
    
    if (damagee.getLocation().getDirection().subtract(from).length() > 1.4D)
    {

      return;
    }
    

    event.SetCancelled(GetName() + " Defense");
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 2.0F);
  }
  

  public void Reset(Player player)
  {
    this._useTime.remove(player);
  }
}
