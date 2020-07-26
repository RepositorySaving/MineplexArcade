package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PinDown extends SkillActive
{
  private HashSet<Projectile> _arrows = new HashSet();
  










  public PinDown(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Instantly fire an arrow, giving", 
      "target Slow 3 for #3#1 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    

    if (!UtilInv.contains(player, Material.ARROW, (byte)0, 1))
    {
      UtilPlayer.message(player, F.main("Skill", "You need " + F.item("1 Arrow") + " to use " + F.skill(GetName()) + "."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    UtilInv.remove(player, Material.ARROW, (byte)0, 1);
    

    Projectile proj = player.launchProjectile(Arrow.class);
    this._arrows.add(proj);
    

    proj.setVelocity(player.getLocation().getDirection().multiply(1.6D));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 0);
    player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 0);
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Hit(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile projectile = event.GetProjectile();
    if (projectile == null) { return;
    }
    
    if (!this._arrows.contains(projectile)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    double dur = 3 + level;
    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, dur, 3, false, true, true, true);
    

    event.AddMod(damager.getName(), GetName(), -2.0D, true);
    event.SetKnockback(false);
    

    for (int i = 0; i < 3; i++) {
      damagee.playEffect(org.bukkit.EntityEffect.HURT);
    }
    
    this._arrows.remove(projectile);
    projectile.remove();
    

    UtilPlayer.message(event.GetDamageePlayer(), F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(mineplex.core.common.util.UtilEnt.getName(event.GetDamageeEntity())) + " with " + F.skill(GetName(level)) + "."));
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SEC)
      return;
    HashSet<Projectile> remove = new HashSet();
    
    for (Projectile cur : this._arrows) {
      if ((cur.isDead()) || (!cur.isValid()))
        remove.add(cur);
    }
    for (Projectile cur : remove) {
      this._arrows.remove(cur);
    }
  }
  
  public void Reset(Player player) {}
}
