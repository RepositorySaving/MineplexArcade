package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillChargeSword;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class WolfsPounce
  extends SkillChargeSword
{
  private NautHashMap<Player, Long> _live = new NautHashMap();
  



  public WolfsPounce(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel)
  {
    super(skills, name, classType, skillType, cost, maxLevel, 0.01F, 0.005F, false, false);
    
    SetDesc(
      new String[] {
      "Hold Block to charge pounce.", 
      "Release Block to pounce.", 
      "", 
      GetChargeString(), 
      "Taking damage cancels charge.", 
      "", 
      "Colliding with another player", 
      "mid-air deals #1#1 damage and", 
      "Slow 2 for #2.5#0.5 seconds." });
  }
  




  public void DoSkillCustom(Player player, float charge)
  {
    UtilAction.velocity(player, 0.4D + 1.2D * charge, 0.2D, 0.6D + 0.6D * charge, true);
    this._live.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(getLevel(player))) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 1.0F, 0.5F + 1.5F * charge);
  }
  
  @EventHandler
  public void CheckCollide(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    for (Player player : GetUsers())
    {
      if (UtilEnt.isGrounded(player))
      {

        if (this._live.containsKey(player))
        {

          if (UtilTime.elapsed(((Long)this._live.get(player)).longValue(), 2000L))
          {

            this._live.remove(player); }
        }
      }
    }
    for (Player player : GetUsers())
    {
      if (this._live.containsKey(player))
      {

        for (Player other : player.getWorld().getPlayers())
        {
          if (other.getGameMode() == GameMode.SURVIVAL)
          {

            if (!other.equals(player))
            {

              if (this.Factory.Relation().CanHurt(player, other))
              {

                if (UtilMath.offset(player, other) <= 2.0D)
                {


                  HandleCollide(player, other);
                  this._live.remove(player);
                  return;
                } } } } }
      }
    }
  }
  
  public void HandleCollide(Player damager, LivingEntity damagee) {
    int level = getLevel(damager);
    int damage = 1 + level;
    

    this.Factory.Damage().NewDamageEvent(damagee, damager, null, 
      EntityDamageEvent.DamageCause.CUSTOM, damage, false, true, false, 
      damager.getName(), GetName());
    

    this.Factory.Condition().Factory().Slow(GetName(), damagee, damagee, 2.5D + 0.5D * level, 1, false, true, true, true);
    

    UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    

    damager.getWorld().playSound(damager.getLocation(), Sound.WOLF_BARK, 0.5F, 0.5F);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void DamageCancelCharge(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (this._charge.remove(damagee) == null) {
      return;
    }
    
    UtilPlayer.message(damagee, F.main(GetClassType().name(), F.skill(GetName()) + " was interrupted."));
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.WOLF_WHINE, 0.6F, 1.2F);
  }
  


  public void Reset(Player player)
  {
    this._charge.remove(player);
    this._live.remove(player);
  }
}
