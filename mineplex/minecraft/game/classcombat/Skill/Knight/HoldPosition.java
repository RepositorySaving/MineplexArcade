package mineplex.minecraft.game.classcombat.Skill.Knight;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;







public class HoldPosition
  extends SkillActive
{
  public HoldPosition(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Hold your position, gaining", 
      "Protection 4, Slow 3 and no", 
      "knockback for #2.5#1.5 seconds." });
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
    double duration = 3 + 1 * level;
    

    this.Factory.Condition().Factory().Slow(GetName(), player, player, duration, 2, false, true, false, true);
    this.Factory.Condition().Factory().Protection(GetName(), player, player, duration, 3, false, true, true);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_SCREAM, 1.5F, 0.0F);
    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 49);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
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
    Condition data = this.Factory.Condition().GetActiveCondition(damagee, Condition.ConditionType.DAMAGE_RESISTANCE);
    if (data == null) { return;
    }
    if (!data.GetReason().equals(GetName())) {
      return;
    }
    
    event.AddMod(damagee.getName(), GetName(), 0.0D, false);
    event.SetKnockback(false);
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : GetUsers())
    {
      Condition data = this.Factory.Condition().GetActiveCondition(player, Condition.ConditionType.DAMAGE_RESISTANCE);
      if (data != null)
      {
        if (data.GetReason().equals(GetName()))
        {

          UtilParticle.PlayParticle(UtilParticle.ParticleType.MOB_SPELL, player.getLocation(), 
            (float)(Math.random() - 0.5D), 0.2F + (float)(Math.random() * 1.0D), (float)(Math.random() - 0.5D), 0.0F, 3 + getLevel(player));
        }
      }
    }
  }
  
  public void Reset(Player player) {}
}
