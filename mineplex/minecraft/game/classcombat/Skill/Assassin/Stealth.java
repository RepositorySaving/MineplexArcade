package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class Stealth extends Skill
{
  private HashSet<Player> _active = new HashSet();
  
  public Stealth(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Toggle", 
      "", 
      "Move stealthily, becoming completely", 
      "Invisible, but also Slow #4#-1.", 
      "", 
      "Stealth ends if you an enemy comes", 
      "within #10#-2 Blocks of you, or you attack." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #13#1 per Second";
  }
  
  @EventHandler
  public void Crouch(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    if (getLevel(player) == 0) {
      return;
    }
    if (!mineplex.core.common.util.UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    

    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    mineplex.core.common.util.UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return;
    }
    if (!this._active.remove(player))
    {
      if (player.hasPotionEffect(PotionEffectType.SLOW))
      {
        UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while Slowed."));
        return;
      }
      
      if (!mineplex.core.common.util.UtilTime.elapsed(this.Factory.Combat().Get(player).GetLastCombat(), 4000L))
      {
        UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while in Combat."));
        return;
      }
      
      Add(player);
    }
    else
    {
      Remove(player, player);
    }
  }
  
  public void Add(Player player)
  {
    this._active.add(player);
    
    int level = getLevel(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "Stealth: " + F.oo("Enabled", true)));
    

    this.Factory.Condition().Factory().Cloak(GetName(), player, player, 120000.0D, false, true);
    this.Factory.Condition().Factory().Slow(GetName(), player, player, 120000.0D, 3 - level, false, false, false, true);
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLAZE_BREATH, 0.5F, 0.5F);
    

    UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, player.getLocation(), 
      (float)(Math.random() - 0.5D), (float)(Math.random() * 1.4D), (float)(Math.random() - 0.5D), 0.0F, 10);
  }
  
  public void Remove(Player player, LivingEntity source)
  {
    this._active.remove(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "Stealth: " + F.oo("Disabled", false)));
    

    this.Factory.Condition().EndCondition(player, null, GetName());
    

    UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, player.getLocation(), 
      (float)(Math.random() - 0.5D), (float)(Math.random() * 1.4D), (float)(Math.random() - 0.5D), 0.0F, 10);
  }
  
  @EventHandler
  public void EndProx(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = getLevel(cur);
      if (level != 0)
      {

        if (this._active.contains(cur))
          for (Player other : cur.getWorld().getPlayers())
          {
            if (!other.equals(cur))
            {

              if (UtilMath.offset(cur, other) <= 8 - 2 * level)
              {

                if (this.Factory.Relation().CanHurt(cur, other))
                {

                  Remove(cur, other);
                  break;
                } } } }
      }
    }
  }
  
  @EventHandler
  public void EndInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    
    if (!this._active.contains(player)) {
      return;
    }
    Remove(player, player);
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void EndDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee != null) { return;
    }
    if (this._active.contains(damagee))
    {
      Remove(damagee, event.GetDamagerEntity(true));
    }
    

    Player damager = event.GetDamagerPlayer(true);
    if (damager != null) { return;
    }
    if (this._active.contains(damager))
    {
      Remove(damager, event.GetDamagerEntity(true));
    }
  }
  

  @EventHandler
  public void Energy(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {


        if (getLevel(cur) == 0)
        {
          Remove(cur, null);



        }
        else if (this.Factory.Condition().IsSilenced(cur, null))
        {
          Remove(cur, null);


        }
        else if (!this.Factory.Energy().Use(cur, GetName(), 0.9D - 0.1D * getLevel(cur), true, false))
        {
          Remove(cur, null);
        }
      }
    }
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
    this.Factory.Condition().EndCondition(player, Condition.ConditionType.CLOAK, GetName());
  }
}
