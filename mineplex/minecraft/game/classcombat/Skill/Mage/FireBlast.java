package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
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
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;







public class FireBlast
  extends SkillActive
{
  public FireBlast(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Launch a fireball which explodes on impact", 
      "dealing large knockback to enemies within", 
      "#6#0.5 Blocks range. Also ignites enemies", 
      "for up to #2#2 seconds." });
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
    LargeFireball ball = (LargeFireball)player.launchProjectile(LargeFireball.class);
    ball.setShooter(player);
    ball.setIsIncendiary(false);
    ball.setYield(0.0F);
    ball.setBounce(false);
    ball.teleport(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)));
    ball.setVelocity(new Vector(0, 0, 0));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0F, 0.8F);
  }
  
  @EventHandler
  public void Collide(ProjectileHitEvent event)
  {
    Projectile proj = event.getEntity();
    
    if (!(proj instanceof LargeFireball)) {
      return;
    }
    if (proj.getShooter() == null) {
      return;
    }
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }
    Player player = (Player)proj.getShooter();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    HashMap<Player, Double> hitMap = UtilPlayer.getInRadius(proj.getLocation(), 5.5D + 0.5D * level);
    for (Player cur : hitMap.keySet())
    {
      double range = ((Double)hitMap.get(cur)).doubleValue();
      

      this.Factory.Condition().Factory().Ignite(GetName(), cur, player, (2 + 1 * level) * range, false, false);
      

      this.Factory.Condition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
      

      UtilAction.velocity(cur, UtilAlg.getTrajectory(proj.getLocation().add(0.0D, -0.5D, 0.0D), cur.getEyeLocation()), 
        1.6D * range, false, 0.0D, 0.8D * range, 1.2D, true);
    }
  }
  
  public void Reset(Player player) {}
}
