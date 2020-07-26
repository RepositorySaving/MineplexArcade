package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Disengage extends SkillActive
{
  private HashMap<Player, Long> _prepare = new HashMap();
  










  public Disengage(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Push Block, then block an attack", 
      "within 1 second to disengage.", 
      "", 
      "If successful, you leap backwards", 
      "and your attacker receives Slow 4", 
      "for #2.5#0.5 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._prepare.put(player, Long.valueOf(System.currentTimeMillis() + 1000L));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared to " + F.skill(GetName()) + "."));
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.LOW)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    
    if (!damagee.isBlocking()) {
      return;
    }
    if (!this._prepare.containsKey(damagee)) {
      return;
    }
    
    LivingEntity damager = event.GetDamagerEntity(false);
    if (damager == null) { return;
    }
    
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    
    event.SetCancelled(GetName());
    

    this._prepare.remove(damagee);
    

    if (UtilEnt.isGrounded(damagee)) {
      UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 3.0D, true, 0.0D, 0.8D, 1.0D, true);
    } else {
      UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 1.5D, true, 0.0D, 0.8D, 1.0D, true);
    }
    
    this.Factory.Condition().Factory().Slow(GetName(), damager, damagee, 2.5D + 0.5D * level, 3, false, true, true, true);
    

    damagee.getWorld().playSound(damager.getLocation(), org.bukkit.Sound.ZOMBIE_METAL, 0.5F, 1.6F);
    UtilParticle.PlayParticle(UtilParticle.ParticleType.ANGRY_VILLAGER, damager.getEyeLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    

    UtilPlayer.message(damagee, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(event.GetDamageePlayer(), F.main(GetClassType().name(), F.name(damagee.getName()) + " used " + F.skill(GetName(level)) + "."));
  }
  

  @EventHandler
  public void Expire(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._prepare.containsKey(cur))
      {

        if (System.currentTimeMillis() > ((Long)this._prepare.get(cur)).longValue())
        {

          this._prepare.remove(cur);
          

          UtilPlayer.message(cur, F.main(GetClassType().name(), "You failed to " + F.skill(GetName()) + "."));
        }
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._prepare.remove(player);
  }
}
