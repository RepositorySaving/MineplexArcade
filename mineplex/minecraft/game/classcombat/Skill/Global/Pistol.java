package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;







public class Pistol
  extends SkillActive
{
  public Pistol(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Pew Pew" });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    

    if (!UtilInv.remove(player, Material.MELON_SEEDS, (byte)0, 1))
    {
      UtilPlayer.message(player, F.main("Skill", "You need " + F.item("Pistol Ammo") + " to use " + F.skill(GetName()) + "."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    double sharpness = 0.1D;
    
    double travel = 0.0D;
    double maxTravel = 100.0D;
    
    double hitBox = 0.5D;
    

    player.getWorld().playEffect(player.getEyeLocation().add(player.getLocation().getDirection()), Effect.SMOKE, 4);
    player.getWorld().playSound(player.getEyeLocation(), Sound.EXPLODE, 0.6F, 2.0F);
    
    while (travel < maxTravel)
    {
      Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(travel));
      for (Entity ent : player.getWorld().getEntities())
      {
        if ((ent instanceof LivingEntity))
        {

          LivingEntity cur = (LivingEntity)ent;
          
          if (!cur.equals(player))
          {

            if ((cur instanceof Player))
            {
              if (UtilMath.offset(loc, ((Player)cur).getEyeLocation()) < 0.3D)
              {
                rifleHit(cur, player, true);
                player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                return;
              }
              if (UtilMath.offset2d(loc, cur.getLocation()) < hitBox)
              {
                if ((loc.getY() > cur.getLocation().getY()) && (loc.getY() < cur.getEyeLocation().getY()))
                {
                  rifleHit(cur, player, false);
                  player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                }
                
              }
            }
            else
            {
              if (UtilMath.offset(loc, cur.getEyeLocation()) < 0.3D)
              {
                rifleHit(cur, player, true);
                player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                return;
              }
              if (UtilMath.offset2d(loc, cur.getLocation()) < hitBox)
              {
                if ((loc.getY() > cur.getLocation().getY()) && (loc.getY() < cur.getLocation().getY() + 1.0D))
                {
                  rifleHit(cur, player, false);
                  player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                  return;
                }
              }
            }
            
            if (UtilMath.offset2d(loc, cur.getLocation()) < hitBox)
            {
              if ((loc.getY() > cur.getLocation().getY()) && (loc.getY() < cur.getEyeLocation().getY()))
              {
                rifleHit(cur, player, false);
                player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                return;
              } }
          }
        }
      }
      if (UtilBlock.solid(loc.getBlock()))
      {
        loc.getBlock().getWorld().playEffect(loc, Effect.STEP_SOUND, loc.getBlock().getTypeId());
        player.getWorld().playSound(player.getLocation(), Sound.BLAZE_HIT, 0.4F, 2.0F);
        return;
      }
      
      travel += sharpness;
    }
  }
  
  public void rifleHit(LivingEntity hit, Player attacker, boolean headshot) {}
  
  public void Reset(Player player) {}
}
