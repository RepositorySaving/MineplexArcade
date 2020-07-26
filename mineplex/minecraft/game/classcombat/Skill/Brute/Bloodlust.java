package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class Bloodlust extends Skill
{
  private WeakHashMap<Player, Long> _time = new WeakHashMap();
  private WeakHashMap<Player, Integer> _str = new WeakHashMap();
  
  public Bloodlust(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "When an enemy dies within #8#4 blocks,", 
      "you go into a Bloodlust, receiving", 
      "Speed 1 and Strength 1 for #3#3 seconds.", 
      "", 
      "Bloodlust can stack up to 3 times,", 
      "boosting the level of Speed and Strength." });
  }
  

  @EventHandler(priority=org.bukkit.event.EventPriority.LOWEST)
  public void PlayerDeath(CombatDeathEvent event)
  {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (!Expire(cur))
      {

        if (this.Factory.Relation().CanHurt(cur, (Player)event.GetEvent().getEntity()))
        {

          if (!cur.equals(event.GetEvent().getEntity()))
          {


            int level = getLevel(cur);
            if (level != 0)
            {

              double distance = 8 + 4 * level;
              if (mineplex.core.common.util.UtilMath.offset(event.GetEvent().getEntity().getLocation(), cur.getLocation()) <= distance)
              {


                int str = 0;
                if (this._str.containsKey(cur))
                  str = ((Integer)this._str.get(cur)).intValue() + 1;
                str = Math.min(str, 3);
                this._str.put(cur, Integer.valueOf(str));
                

                double dur = 3 + 3 * level;
                this._time.put(cur, Long.valueOf(System.currentTimeMillis() + (dur * 1000.0D)));
                

                this.Factory.Condition().Factory().Speed(GetName(), cur, event.GetEvent().getEntity(), dur, str, false, true, true);
                this.Factory.Condition().Factory().Strength(GetName(), cur, event.GetEvent().getEntity(), dur, str, false, true, true);
                

                UtilPlayer.message(cur, F.main(GetClassType().name(), "You entered " + F.skill(GetName(level)) + " at " + F.elem(new StringBuilder("Level ").append(str + 1).toString()) + "."));
                

                cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.ZOMBIE_PIG_ANGRY, 2.0F, 0.6F);
              }
            }
          } } } }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) { if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers()) {
      Expire(cur);
    }
  }
  
  public boolean Expire(Player player) {
    if (!this._time.containsKey(player)) {
      return false;
    }
    if (System.currentTimeMillis() > ((Long)this._time.get(player)).longValue())
    {
      int str = ((Integer)this._str.remove(player)).intValue();
      UtilPlayer.message(player, F.main(GetClassType().name(), "Your " + F.skill(GetName(getLevel(player))) + 
        " has ended at " + F.elem(new StringBuilder("Level ").append(str + 1).toString()) + "."));
      this._time.remove(player);
      
      return true;
    }
    
    return false;
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._str.keySet())
    {
      UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.RED_DUST, ent.getLocation(), 
        (float)(Math.random() - 0.5D), 0.2F + (float)Math.random(), (float)(Math.random() - 0.5D), 0.0F, ((Integer)this._str.get(ent)).intValue() * 2);
    }
  }
  

  public void Reset(Player player)
  {
    this._time.remove(player);
    this._str.remove(player);
  }
}
