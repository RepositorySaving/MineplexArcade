package mineplex.minecraft.game.classcombat.Skill.Brute;

import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;

public class CripplingBlow extends Skill
{
  public CripplingBlow(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your powerflow axe blows give", 
      "targets Slow #0#1 for #0.5#0.5 seconds,", 
      "as well as no knockback." });
  }
  

  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    org.bukkit.entity.LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
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
    
    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, 0.5D + 0.5D * level, level, false, true, false, true);
    

    event.AddMod(damager.getName(), GetName(), 0.0D, true);
    event.SetKnockback(false);
    

    UtilServer.getServer().getPluginManager().callEvent(new mineplex.minecraft.game.classcombat.Skill.event.SkillEvent(damager, GetName(), IPvpClass.ClassType.Brute, damagee));
  }
  
  public void Reset(Player player) {}
}
