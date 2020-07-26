package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class Evade extends SkillActive
{
  private HashSet<Player> _active = new HashSet();
  










  public Evade(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Block attacks to evade them and", 
      "teleport behind the attacker.", 
      "", 
      "Crouch and Evade to teleport backwards." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #30#-2 and #24#-4 per Second";
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
    this._active.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared to " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Energy(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);


        }
        else if (!this.Factory.Energy().Use(cur, GetName(), 1.2D - getLevel(cur) * 0.2D, true, true))
        {
          this._active.remove(cur);
        }
      }
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.NORMAL)
  public void Damage(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    
    if (!damagee.isBlocking()) {
      return;
    }
    
    if (!this._active.contains(damagee)) {
      return;
    }
    
    LivingEntity damager = event.GetDamagerEntity(false);
    if (damager == null) { return;
    }
    
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    if (!Recharge.Instance.use(damagee, GetName(), 500L, false, false)) {
      return;
    }
    
    event.SetCancelled(GetName());
    
    this._active.remove(damagee);
    

    for (int i = 0; i < 3; i++) {
      damagee.getWorld().playEffect(damagee.getLocation(), org.bukkit.Effect.SMOKE, 5);
    }
    
    Location target = null;
    if (damagee.isSneaking()) target = FindLocationBack(damager, damagee); else
      target = FindLocationBehind(damager, damagee);
    if (target == null) {
      return;
    }
    
    UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, damagee.getLocation(), 
      (float)(Math.random() - 0.5D), (float)(Math.random() * 1.4D), (float)(Math.random() - 0.5D), 0.0F, 10);
    

    this.Factory.Teleport().TP(damagee, target);
    

    if (damagee.isSneaking()) {
      this.Factory.Condition().Factory().Cloak(GetName(), damagee, damagee, 0.1D, false, false);
    }
    
    this.Factory.Condition().Factory().Invulnerable(GetName(), damagee, damagee, 0.5D, false, false);
    

    UtilPlayer.message(damagee, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damager, F.main(GetClassType().name(), F.name(damagee.getName()) + " used " + F.skill(GetName(level)) + "."));
  }
  
  private Location FindLocationBehind(LivingEntity damager, Player damagee)
  {
    double curMult = 0.0D;
    double maxMult = 1.5D;
    
    double rate = 0.1D;
    
    Location lastValid = damager.getLocation();
    
    while (curMult <= maxMult)
    {
      Vector vec = UtilAlg.getTrajectory(damager, damagee).multiply(curMult);
      Location loc = damager.getLocation().subtract(vec);
      
      if ((!UtilBlock.airFoliage(loc.getBlock())) || (!UtilBlock.airFoliage(loc.getBlock().getRelative(BlockFace.UP)))) {
        return lastValid;
      }
      lastValid = loc;
      
      curMult += rate;
    }
    
    return lastValid;
  }
  
  private Location FindLocationBack(LivingEntity damager, Player damagee)
  {
    double curMult = 0.0D;
    double maxMult = 3.0D;
    
    double rate = 0.1D;
    
    Location lastValid = damagee.getLocation();
    
    while (curMult <= maxMult)
    {
      Vector vec = UtilAlg.getTrajectory(damager, damagee).multiply(curMult);
      Location loc = damagee.getLocation().add(vec);
      
      if ((!UtilBlock.airFoliage(loc.getBlock())) || (!UtilBlock.airFoliage(loc.getBlock().getRelative(BlockFace.UP)))) {
        return lastValid;
      }
      lastValid = loc;
      
      curMult += rate;
    }
    
    return lastValid;
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
