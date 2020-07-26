package mineplex.minecraft.game.classcombat.Skill.Assassin;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.movement.ClientMovement;
import mineplex.core.movement.Movement;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;





public class Leap
  extends SkillActive
{
  public Leap(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Take a great leap forwards.", 
      "", 
      "Wall Kick by using Leap with your", 
      "back against a wall. This uses no", 
      "Energy or Recharge.", 
      "", 
      "Cannot be used while Slowed." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    if (UtilTime.elapsed(((ClientMovement)this.Factory.Movement().Get(player)).LastGrounded, 8000L))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while airborne."));
    }
    
    if (player.hasPotionEffect(PotionEffectType.SLOW))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while Slowed."));
      return false;
    }
    

    if (WallJump(player, level)) {
      return false;
    }
    return true;
  }
  

  public void DoLeap(Player player, int level, boolean wallkick)
  {
    if (!wallkick) {
      UtilAction.velocity(player, 1.2D, 0.2D, 1.0D, true);
    }
    else {
      Vector vec = player.getLocation().getDirection();
      vec.setY(0);
      UtilAction.velocity(player, vec, 0.7D, false, 0.0D, 0.7D, 2.0D, true);
    }
    

    if (!wallkick) {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    } else {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill("Wall Kick") + "."));
    }
    
    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 80);
    player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 2.0F, 1.2F);
  }
  

  public void Skill(Player player, int level)
  {
    DoLeap(player, level, false);
  }
  
  public boolean WallJump(Player player, int level)
  {
    if (level == 0) { return false;
    }
    
    if (!Recharge.Instance.use(player, "Wall Kick", 500L, false, false)) {
      return false;
    }
    
    Vector vec = player.getLocation().getDirection();
    

    boolean xPos = true;
    boolean zPos = true;
    
    if (vec.getX() < 0.0D) xPos = false;
    if (vec.getZ() < 0.0D) { zPos = false;
    }
    for (int y = 0; y <= 0; y++)
    {
      for (int x = -1; x <= 1; x++)
      {
        for (int z = -1; z <= 1; z++)
        {
          if ((x != 0) || (z != 0))
          {

            if (((!xPos) || (x <= 0)) && 
              ((!zPos) || (z <= 0)) && 
              ((xPos) || (x >= 0)) && (
              (zPos) || (z >= 0)))
            {

              if (!UtilBlock.airFoliage(player.getLocation().getBlock().getRelative(x, y, z)))
              {

                Block forward = null;
                

                if (Math.abs(vec.getX()) > Math.abs(vec.getZ()))
                {
                  if (xPos) forward = player.getLocation().getBlock().getRelative(1, 0, 0); else {
                    forward = player.getLocation().getBlock().getRelative(-1, 0, 0);
                  }
                  
                }
                else if (zPos) forward = player.getLocation().getBlock().getRelative(0, 0, 1); else {
                  forward = player.getLocation().getBlock().getRelative(0, 0, -1);
                }
                
                if (UtilBlock.airFoliage(forward))
                {


                  if (Math.abs(vec.getX()) > Math.abs(vec.getZ()))
                  {
                    if (xPos) forward = player.getLocation().getBlock().getRelative(1, 1, 0); else {
                      forward = player.getLocation().getBlock().getRelative(-1, 1, 0);
                    }
                    
                  }
                  else if (zPos) forward = player.getLocation().getBlock().getRelative(0, 1, 1); else {
                    forward = player.getLocation().getBlock().getRelative(0, 1, -1);
                  }
                  
                  if (UtilBlock.airFoliage(forward))
                  {

                    DoLeap(player, level, true);
                    
                    return true;
                  }
                }
              } } } } }
    }
    return false;
  }
  
  public void Reset(Player player) {}
}
