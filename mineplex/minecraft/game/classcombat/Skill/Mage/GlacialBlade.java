package mineplex.minecraft.game.classcombat.Skill.Mage;

import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
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
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;




public class GlacialBlade
  extends SkillActive
  implements IThrown
{
  public GlacialBlade(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Swinging your sword releases a", 
      "shard of ice, dealing 4 damage", 
      "to anything it hits.", 
      "", 
      "Will not work if enemies are close." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    for (Player cur : UtilPlayer.getNearby(player.getLocation(), 4.0D)) {
      if (!cur.equals(player))
      {
        if (this.Factory.Relation().CanHurt(cur, player))
          return false; }
    }
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9)) {
      return false;
    }
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()).subtract(0.0D, 0.2D, 0.0D), ItemStackFactory.Instance.CreateStack(370));
    UtilAction.velocity(item, player.getLocation().getDirection(), 1.6D, false, 0.0D, 0.2D, 10.0D, false);
    this.Factory.Projectile().AddThrow(item, player, this, -1L, true, true, true, 
      null, 0.0F, 0.0F, UtilParticle.ParticleType.SNOW_SHOVEL, UpdateType.TICK, 1.5D);
    

    item.getWorld().playSound(item.getLocation(), Sound.ORB_PICKUP, 1.0F, 2.0F);
  }
  


  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);
    

    data.GetThrown().remove();
    
    if (target == null) {
      return;
    }
    
    this.Factory.Damage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.CUSTOM, 4.0D, true, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
  }
  


  public void Idle(ProjectileUser data)
  {
    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);
    

    data.GetThrown().remove();
  }
  


  public void Expire(ProjectileUser data)
  {
    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);
    

    data.GetThrown().remove();
  }
  
  public void Reset(Player player) {}
}
