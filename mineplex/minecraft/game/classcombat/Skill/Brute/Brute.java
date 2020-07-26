package mineplex.minecraft.game.classcombat.Skill.Brute;

import mineplex.core.energy.event.EnergyEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Brute extends Skill
{
  public Brute(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You take 8 more damage from enemy attacks", 
      "to counter the strength of Diamond Armor.", 
      "", 
      "25% reduction in Arrow Velocity." });
  }
  

  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
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
    
    event.AddMod(damagee.getName(), GetName(), 0.0D, false);
    event.SetBrute();
  }
  
  @EventHandler
  public void BowShoot(EntityShootBowEvent event)
  {
    if (getLevel(event.getEntity()) == 0) {
      return;
    }
    event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(0.75D));
  }
  
  @EventHandler
  public void CancelEnergy(EnergyEvent event)
  {
    if (getLevel(event.GetPlayer()) > 0) {
      event.setCancelled(true);
    }
  }
  
  public void Reset(Player player) {}
}
