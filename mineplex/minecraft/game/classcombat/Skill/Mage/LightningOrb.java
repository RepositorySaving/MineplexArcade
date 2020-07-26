package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
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
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;





public class LightningOrb
  extends SkillActive
  implements IThrown
{
  public LightningOrb(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Launch a lightning orb. Upon a direct", 
      "hit with player, or #5#-0.4 seconds, it will", 
      "strike all enemies within #3#0.5 Blocks ", 
      "with lightning, giving them Slow 3", 
      "for #2#1 seconds." });
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
    Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(57));
    item.setVelocity(player.getLocation().getDirection());
    this.Factory.Projectile().AddThrow(item, player, this, System.currentTimeMillis() + 5000L - 400 * level, true, false, false, 
      Sound.FIZZ, 0.6F, 1.6F, UtilParticle.ParticleType.FIREWORKS_SPARK, UpdateType.TICK, 1.5D);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    item.getWorld().playSound(item.getLocation(), Sound.SILVERFISH_HIT, 2.0F, 1.0F);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Strike(target, data);
  }
  

  public void Idle(ProjectileUser data)
  {
    Strike(null, data);
  }
  

  public void Expire(ProjectileUser data)
  {
    Strike(null, data);
  }
  

  public void Strike(LivingEntity target, ProjectileUser data)
  {
    data.GetThrown().remove();
    

    if (!(data.GetThrower() instanceof Player)) {
      return;
    }
    Player player = (Player)data.GetThrower();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    HashMap<Player, Double> hit = UtilPlayer.getInRadius(data.GetThrown().getLocation(), 3.0D + 0.5D * level);
    

    for (Player cur : hit.keySet())
    {
      this.Factory.Condition().Factory().Lightning(GetName(), cur, player, 0, 0.5D, false, true);
    }
    

    for (Player cur : hit.keySet())
    {
      if (!cur.equals(player))
      {


        UtilPlayer.message(cur, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
        

        cur.getWorld().strikeLightning(cur.getLocation());
      }
    }
    
    for (Player cur : hit.keySet())
    {
      if (!cur.equals(player))
      {

        this.Factory.Condition().Factory().Slow(GetName(), cur, player, 2 + 1 * level, 2, false, true, true, true);
      }
    }
  }
  
  @EventHandler
  public void CancelFire(BlockIgniteEvent event) {
    if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
      event.setCancelled(true);
    }
  }
  
  public void Reset(Player player) {}
}
