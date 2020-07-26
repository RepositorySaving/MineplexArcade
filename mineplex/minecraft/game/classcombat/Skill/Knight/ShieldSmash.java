package mineplex.minecraft.game.classcombat.Skill.Knight;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;





public class ShieldSmash
  extends SkillActive
{
  public ShieldSmash(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Smash your shield into an enemy,", 
      "dealing #1.6#0.2 knockback." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    if (!Recharge.Instance.use(player, GetName() + " Cooldown", 250L, false, false)) {
      return false;
    }
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    Location loc = player.getLocation();
    loc.add(player.getLocation().getDirection().setY(0).normalize().multiply(1.5D));
    loc.add(0.0D, 0.8D, 0.0D);
    

    UtilParticle.PlayParticle(UtilParticle.ParticleType.CLOUD, loc, 0.0F, 0.0F, 0.0F, 0.05F, 6);
    UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, loc, 0.0F, 0.0F, 0.0F, 0.0F, 1);
    
    boolean hit = false;
    
    for (Entity other : player.getWorld().getEntities())
    {
      if ((other instanceof LivingEntity))
      {

        LivingEntity cur = (LivingEntity)other;
        
        if (!cur.equals(player))
        {

          if (UtilMath.offset(loc, cur.getLocation()) <= 2.5D)
          {

            hit = true;
            
            Vector dir = player.getLocation().getDirection();
            if (dir.getY() < 0.0D) {
              dir.setY(0);
            }
            
            UtilAction.velocity(cur, dir, 1.6D + 0.2D * level, false, 0.0D, 0.3D, 0.8D + 0.05D * level, true);
            

            this.Factory.Condition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
            

            UtilPlayer.message(cur, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
          } }
      } }
    if (hit)
    {

      UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName(level)) + "."));
      

      player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 0.9F);

    }
    else
    {
      UtilPlayer.message(player, F.main("Skill", "You missed " + F.skill(GetName(level)) + "."));
    }
  }
  
  public void Reset(Player player) {}
}
