package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActiveCharge;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class FleshHook extends SkillActiveCharge implements IThrown
{
  public FleshHook(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray, 0.01F, 0.005F);
    
    SetDesc(
      new String[] {
      "Hold Block to charge Flesh Hook.", 
      "Release Block to release it.", 
      "", 
      GetChargeString(), 
      "", 
      "If Flesh Hook hits a player, it", 
      "deals up to #2#2 damage, and rips them", 
      "towards you with #1.2#0.2 velocity." });
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
    this._charge.put(player, Float.valueOf(0.0F));
  }
  
  @EventHandler
  public void ChargeRelease(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {

      if (this._charge.containsKey(cur))
      {


        int level = getLevel(cur);
        if (level == 0) { return;
        }
        
        if (cur.isBlocking())
        {
          Charge(cur);


        }
        else if (this._charge.containsKey(cur))
        {
          float charge = ((Float)this._charge.remove(cur)).floatValue();
          

          Item item = cur.getWorld().dropItem(cur.getEyeLocation().add(cur.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(131));
          UtilAction.velocity(item, cur.getLocation().getDirection(), 
            1.0F + charge, false, 0.0D, 0.2D, 20.0D, false);
          
          this.Factory.Projectile().AddThrow(item, cur, this, -1L, true, true, true, 
            Sound.FIRE_IGNITE, 1.4F, 0.8F, UtilParticle.ParticleType.CRIT, UpdateType.TICK, 1.5D);
          

          UtilPlayer.message(cur, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
          

          item.getWorld().playSound(item.getLocation(), Sound.IRONGOLEM_THROW, 2.0F, 0.8F);
        }
      }
    }
  }
  


  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    double velocity = data.GetThrown().getVelocity().length();
    data.GetThrown().remove();
    
    if (!(data.GetThrower() instanceof Player)) {
      return;
    }
    Player player = (Player)data.GetThrower();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    if (target == null) {
      return;
    }
    
    UtilAction.velocity(target, 
      UtilAlg.getTrajectory(target.getLocation(), player.getLocation()), 
      1.2D + 0.2D * level, false, 0.0D, 0.8D, 1.5D, true);
    

    this.Factory.Condition().Factory().Falling(GetName(), target, player, 10.0D, false, true);
    

    this.Factory.Damage().NewDamageEvent(target, player, null, 
      EntityDamageEvent.DamageCause.CUSTOM, velocity * (1 + 1 * level), false, true, false, 
      player.getName(), GetName());
    


    if (target != null) {
      UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(player, GetName(), IPvpClass.ClassType.Brute, target));
    }
    
    UtilPlayer.message(target, F.main(GetClassType().name(), F.name(player.getName()) + " pulled you with " + F.skill(GetName(level)) + "."));
  }
  


  public void Idle(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
  


  public void Expire(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
}
