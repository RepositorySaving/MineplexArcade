package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

public class Stampede extends Skill
{
  private WeakHashMap<Player, Long> _sprintTime = new WeakHashMap();
  private WeakHashMap<Player, Integer> _sprintStr = new WeakHashMap();
  
  public Stampede(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You slowly build up speed as you", 
      "sprint. You gain a level of Speed", 
      "for every #5#-1 seconds, up to a max", 
      "of Speed 3.", 
      "", 
      "Attacking during stampede deals", 
      "#0#0.5 bonus damage per speed level,", 
      "and +50% knockback per speed level.", 
      "", 
      "Resets if you take damage." });
  }
  

  @EventHandler
  public void Skill(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = getLevel(cur);
      if (level != 0)
      {

        if (this._sprintTime.containsKey(cur))
        {

          if (!cur.isSprinting())
          {
            this._sprintTime.remove(cur);
            this._sprintStr.remove(cur);
            cur.removePotionEffect(PotionEffectType.SPEED);
          }
          else
          {
            long time = ((Long)this._sprintTime.get(cur)).longValue();
            int str = ((Integer)this._sprintStr.get(cur)).intValue();
            

            if (str > 0) {
              this.Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9D, str - 1, false, true, true);
            }
            
            if (UtilTime.elapsed(time, 5000 - 1000 * level))
            {

              this._sprintTime.put(cur, Long.valueOf(System.currentTimeMillis()));
              
              if (str < 3)
              {
                this._sprintStr.put(cur, Integer.valueOf(str + 1));
                

                cur.getWorld().playSound(cur.getLocation(), Sound.ZOMBIE_IDLE, 2.0F, 0.2F * str + 1.0F);
              }
              

              UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(cur, GetName(), IPvpClass.ClassType.Brute));
            }
          } } else if (cur.isSprinting())
        {

          if (!this._sprintTime.containsKey(cur))
          {
            this._sprintTime.put(cur, Long.valueOf(System.currentTimeMillis()));
            this._sprintStr.put(cur, Integer.valueOf(0));
          }
        }
      }
    }
  }
  
  @EventHandler
  public void Particle(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._sprintStr.keySet())
    {
      UtilParticle.PlayParticle(UtilParticle.ParticleType.CRIT, ent.getLocation(), 
        (float)(Math.random() - 0.5D), 0.2F + (float)Math.random(), (float)(Math.random() - 0.5D), 0.0F, ((Integer)this._sprintStr.get(ent)).intValue() * 2);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this._sprintStr.containsKey(damager)) {
      return;
    }
    if (((Integer)this._sprintStr.get(damager)).intValue() == 0) {
      return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    this._sprintTime.remove(damager);
    int str = ((Integer)this._sprintStr.remove(damager)).intValue();
    damager.removePotionEffect(PotionEffectType.SPEED);
    

    event.AddMod(damager.getName(), GetName(), str * (0.5D * level), true);
    event.AddKnockback(GetName(), 1.0D + 0.5D * str);
    

    UtilPlayer.message(damager, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    

    damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_WOOD, 1.0F, 0.4F * str);
    

    UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(damager, GetName(), IPvpClass.ClassType.Brute, damagee));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageCancel(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    Reset(damagee);
    this.Factory.Condition().EndCondition(damagee, null, GetName());
  }
  

  public void Reset(Player player)
  {
    this._sprintTime.remove(player);
    this._sprintStr.remove(player);
  }
}
