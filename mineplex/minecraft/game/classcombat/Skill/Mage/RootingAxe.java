package mineplex.minecraft.game.classcombat.Skill.Mage;

import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilGear;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class RootingAxe extends Skill
{
  public RootingAxe(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your axe rips players downward into", 
      "the earth, disrupting their movement." });
  }
  


  public String GetRechargeString()
  {
    return "Recharge: #7#-1.5 seconds";
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Root(CustomDamageEvent event)
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
    if (!UtilGear.isAxe(damager.getItemInHand())) {
      return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    event.SetKnockback(false);
    

    if ((Recharge.Instance.use(damager, GetName(), 7000 - level * 1500, false, false)) && 
      (mineplex.core.common.util.UtilEnt.isGrounded(damagee)))
    {

      if (UtilBlock.solid(damagee.getLocation().getBlock())) {
        return;
      }
      
      if (UtilBlock.airFoliage(damagee.getLocation().getBlock().getRelative(0, -2, 0))) {
        return;
      }
      
      this.Factory.Teleport().TP(damagee, damagee.getLocation().add(0.0D, -0.9D, 0.0D));
      

      damagee.getWorld().playEffect(damagee.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, damagee.getLocation().getBlock().getTypeId());
    }
    

    UtilAction.velocity(damagee, new org.bukkit.util.Vector(0, -1, 0), 0.5D, false, 0.0D, 0.0D, 10.0D, false);
  }
  
  public void Reset(Player player) {}
}
