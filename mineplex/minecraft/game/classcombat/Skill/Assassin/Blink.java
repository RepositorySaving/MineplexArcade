package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.teleport.Teleport;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Blink extends SkillActive
{
  private HashMap<Player, Location> _loc = new HashMap();
  private HashMap<Player, Long> _blinkTime = new HashMap();
  










  public Blink(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Instantly teleport forwards #9#3 Blocks.", 
      "Cannot be used while Slowed.", 
      "", 
      "Using again within 5 seconds De-Blinks,", 
      "returning you to your original location.", 
      "Can be used while Slowed." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if (player.hasPotionEffect(PotionEffectType.SLOW))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while Slowed."));
      return false;
    }
    
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    

    if ((this._loc.containsKey(player)) && (this._blinkTime.containsKey(player)) && 
      (!UtilTime.elapsed(((Long)this._blinkTime.get(player)).longValue(), 5000L)))
    {
      Deblink(player, level);
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    Block lastSmoke = player.getLocation().getBlock();
    
    double maxRange = 9 + level * 3;
    double curRange = 0.0D;
    while (curRange <= maxRange)
    {
      Location newTarget = player.getLocation().add(new Vector(0.0D, 0.2D, 0.0D)).add(player.getLocation().getDirection().multiply(curRange));
      
      if ((!UtilBlock.airFoliage(newTarget.getBlock())) || (!UtilBlock.airFoliage(newTarget.getBlock().getRelative(BlockFace.UP)))) {
        break;
      }
      
      for (Player cur : player.getWorld().getPlayers())
      {
        if (!cur.equals(player))
        {

          if (UtilMath.offset(newTarget, cur.getLocation()) <= 1.0D)
          {


            Location target = cur.getLocation().add(player.getLocation().subtract(cur.getLocation()).toVector().normalize());
            player.teleport(mineplex.core.common.util.UtilWorld.locMerge(player.getLocation(), target));
            

            UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName()) + "."));
            

            player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
            return;
          }
        }
      }
      curRange += 0.2D;
      

      UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, newTarget.clone().add(0.0D, 0.5D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      
      lastSmoke = newTarget.getBlock();
    }
    

    curRange -= 0.4D;
    if (curRange < 0.0D) {
      curRange = 0.0D;
    }
    
    Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(curRange).add(new Vector(0.0D, 0.4D, 0.0D)));
    this._loc.put(player, player.getLocation());
    

    if (curRange > 0.0D)
    {
      player.leaveVehicle();
      player.teleport(loc);
    }
    
    player.setFallDistance(0.0F);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
    

    this._blinkTime.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  public void Deblink(Player player, int level)
  {
    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill("De-Blink") + "."));
    

    Block lastSmoke = player.getLocation().getBlock();
    
    double curRange = 0.0D;
    
    Location target = (Location)this._loc.remove(player);
    
    boolean done = false;
    while (!done)
    {
      Vector vec = UtilAlg.getTrajectory(player.getLocation(), 
        new Location(player.getWorld(), target.getX(), target.getY(), target.getZ()));
      
      Location newTarget = player.getLocation().add(vec.multiply(curRange));
      

      curRange += 0.2D;
      

      UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, newTarget.clone().add(0.0D, 0.5D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      
      lastSmoke = newTarget.getBlock();
      
      if (UtilMath.offset(newTarget, target) < 0.4D) {
        done = true;
      }
      if (curRange > 24.0D) {
        done = true;
      }
      if (curRange > 24.0D) {
        done = true;
      }
    }
    this.Factory.Teleport().TP(player, target);
    
    player.setFallDistance(0.0F);
    

    player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
  }
  

  public void Reset(Player player)
  {
    this._loc.remove(player);
    this._blinkTime.remove(player);
  }
}
