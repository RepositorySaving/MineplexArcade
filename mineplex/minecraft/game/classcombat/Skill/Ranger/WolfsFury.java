package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
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
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WolfsFury extends SkillActive
{
  private WeakHashMap<Player, Long> _active = new WeakHashMap();
  private HashSet<Player> _swing = new HashSet();
  private HashSet<Player> _miss = new HashSet();
  










  public WolfsFury(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Summon the power of the wolf, gaining", 
      "Strength 3 for #2#2 seconds, and giving", 
      "no knockback on your attacks.", 
      "", 
      "If you miss two consecutive attacks,", 
      "Wolfs Fury ends." });
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
    this._active.put(player, Long.valueOf(System.currentTimeMillis() + 8000L));
    

    this.Factory.Condition().Factory().Strength(GetName(), player, player, 2 + 2 * level, level - 1, false, true, true);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.WOLF_GROWL, 1.4F, 1.2F);
  }
  

  @EventHandler
  public void Expire(UpdateEvent event)
  {
    if (event.getType() == UpdateType.FAST)
    {
      HashSet<Player> expired = new HashSet();
      
      for (Player cur : this._active.keySet()) {
        if (System.currentTimeMillis() > ((Long)this._active.get(cur)).longValue())
          expired.add(cur);
      }
      for (Player cur : expired) {
        End(cur);
      }
    }
  }
  
  @EventHandler
  public void Swing(PlayerInteractEvent event) {
    if (!UtilEvent.isAction(event, UtilEvent.ActionType.L)) {
      return;
    }
    if (!this._active.containsKey(event.getPlayer())) {
      return;
    }
    if ((!UtilGear.isAxe(event.getPlayer().getItemInHand())) && (!UtilGear.isSword(event.getPlayer().getItemInHand()))) {
      return;
    }
    this._swing.add(event.getPlayer());
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Hit(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    org.bukkit.entity.LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    if (!this._swing.remove(damager)) {
      return;
    }
    
    this._miss.remove(damager);
    

    event.SetKnockback(false);
    

    damager.getWorld().playSound(damager.getLocation(), Sound.WOLF_BARK, 0.5F, 1.2F);
  }
  
  @EventHandler
  public void Miss(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> expired = new HashSet();
    
    for (Player cur : this._swing) {
      expired.add(cur);
    }
    for (Player cur : expired)
    {
      this._swing.remove(cur);
      
      if (this._miss.remove(cur))
      {
        End(cur);
      }
      else
      {
        this._miss.add(cur);
      }
    }
  }
  
  public void End(Player player)
  {
    Reset(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), F.skill(GetName()) + " has ended."));
    

    player.getWorld().playSound(player.getLocation(), Sound.WOLF_WHINE, 0.6F, 0.8F);
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this._active.keySet())
    {
      UtilParticle.PlayParticle(UtilParticle.ParticleType.RED_DUST, player.getLocation(), 
        (float)(Math.random() - 0.5D), 0.2F + (float)(Math.random() * 1.0D), (float)(Math.random() - 0.5D), 0.0F, 4);
    }
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
    this._swing.remove(player);
    this._miss.remove(player);
    this.Factory.Condition().EndCondition(player, mineplex.minecraft.game.core.condition.Condition.ConditionType.INCREASE_DAMAGE, GetName());
  }
}
