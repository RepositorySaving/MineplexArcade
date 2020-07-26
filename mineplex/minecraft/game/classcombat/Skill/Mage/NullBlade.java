package mineplex.minecraft.game.classcombat.Skill.Mage;

import mineplex.core.common.util.UtilGear;
import mineplex.core.energy.Energy;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NullBlade extends Skill
{
  public NullBlade(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your attacks suck the life from", 
      "opponents, restoring #2#2 energy." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Drain(CustomDamageEvent event)
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
    if (!UtilGear.isSword(damager.getItemInHand())) {
      return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    
    this.Factory.Energy().ModifyEnergy(damager, 2 + 2 * level);
    

    event.AddMod(damager.getName(), GetName(), 0.0D, true);
    

    damager.getWorld().playSound(damager.getLocation(), org.bukkit.Sound.BLAZE_BREATH, 0.6F, 0.6F);
  }
  
  public void Reset(Player player) {}
}
