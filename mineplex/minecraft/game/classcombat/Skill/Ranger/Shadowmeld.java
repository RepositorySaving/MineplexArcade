package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillChargeSword;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Shadowmeld extends SkillChargeSword
{
  private HashSet<Player> _active = new HashSet();
  



  public Shadowmeld(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel)
  {
    super(skills, name, classType, skillType, cost, maxLevel, 0.01F, 0.005F, false, false);
    
    SetDesc(
      new String[] {
      "Hold Crouch to meld into the shadows.", 
      "", 
      "Charges #" + (int)(this._rateBase * 2000.0F) + "#" + (int)(this._rateBoost * 2000.0F) + " % per Second.", 
      "", 
      "Shadowmeld ends if you stop crouching,", 
      "interact or another player comes within", 
      "#12#-3 Blocks of you." });
  }
  



  public void DoSkillCustom(Player player, float charge)
  {
    this._active.add(player);
  }
  
  @EventHandler
  public void EndProximity(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        int level = getLevel(cur);
        if (level != 0)
        {

          if (!cur.isSneaking())
          {
            End(cur);

          }
          else
          {
            for (Player other : cur.getWorld().getEntitiesByClass(Player.class))
            {
              if (!other.equals(cur))
              {

                if (UtilMath.offset(cur, other) <= 16 - 3 * level)
                {

                  End(cur);
                }
              }
            }
            this.Factory.Condition().Factory().Cloak(GetName(), cur, cur, 1.9D, false, true);
          } }
      } }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamage(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    End(damagee);
  }
  
  @EventHandler
  public void EndInteract(PlayerInteractEvent event)
  {
    End(event.getPlayer());
  }
  
  @EventHandler
  public void EndBow(EntityShootBowEvent event)
  {
    if ((event.getEntity() instanceof Player)) {
      End((Player)event.getEntity());
    }
  }
  
  public void End(Player player) {
    if (this._active.remove(player)) {
      this.Factory.Condition().EndCondition(player, Condition.ConditionType.CLOAK, GetName());
    }
  }
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
    End(player);
  }
}
