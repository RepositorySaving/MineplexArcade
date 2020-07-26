package mineplex.minecraft.game.classcombat.Skill.Knight;

import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Cleave extends Skill
{
  public Cleave(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your attacks deal #40#20 % damage to", 
      "all enemies within #1.5#0.5 Blocks", 
      "of your target enemy.", 
      "", 
      "This only works with Axes." });
  }
  

  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Skill(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    if (event.GetReason() != null) {
      return;
    }
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!UtilGear.isAxe(damager.getItemInHand())) {
      return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    event.AddMod(damager.getName(), GetName(), 0.0D, false);
    

    for (Player other : UtilPlayer.getNearby(damagee.getLocation(), 1.5D + 0.5D * level))
    {
      if ((!other.equals(damagee)) && (!other.equals(damager)) && 
        (this.Factory.Relation().CanHurt(damager, other)))
      {

        this.Factory.Damage().NewDamageEvent(other, damager, null, 
          EntityDamageEvent.DamageCause.ENTITY_ATTACK, (0.25D + level * 0.25D) * event.GetDamageInitial(), true, false, false, 
          damager.getName(), GetName());
      }
    }
  }
  
  public void Reset(Player player) {}
}
