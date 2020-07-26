package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.IRelation;
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






public class MagneticRepel
  extends SkillActive
{
  public MagneticRepel(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
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
    for (Entity other : player.getWorld().getEntities())
    {
      if ((other instanceof LivingEntity))
      {

        if (!player.equals(other))
        {

          double offset = UtilMath.offset(player, other);
          double maxOffset = 6 + level * 2;
          
          if (offset <= maxOffset)
          {

            if ((!(other instanceof Player)) || 
            
              (this.Factory.Relation().CanHurt(player, (Player)other)))
            {


              double power = 0.5D + 0.5D * ((maxOffset - offset) / maxOffset);
              
              Vector vel = UtilAlg.getTrajectory(player, other);
              vel.setY(Math.min(0.3D, vel.getY()));
              vel.normalize();
              
              UtilAction.velocity(other, vel, 
                power * (2.0D + level * 0.5D), false, 0.0D, 0.8D, 0.8D, true);
            } }
        } }
    }
    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    for (int i = 0; i < 3; i++) {
      player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 2.0F, 0.6F);
    }
    player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 2.0F, 2.0F);
  }
  
  public void Reset(Player player) {}
}
