package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Sharpshooter
  extends Skill
{
  private WeakHashMap<Player, Integer> _hitCount = new WeakHashMap();
  private HashMap<Entity, Player> _arrows = new HashMap();
  
  public Sharpshooter(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Consecutive arrow hits deal an", 
      "additional #1#0.5 damage.", 
      "", 
      "Stacks up to #1#1 times", 
      "", 
      "Missing an arrow resets the bonus." });
  }
  

  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    int level = getLevel((Player)event.getEntity());
    if (level == 0) { return;
    }
    
    this._arrows.put(event.getProjectile(), (Player)event.getEntity());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
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
    if (!this._arrows.containsKey(projectile)) {
      return;
    }
    Player player = (Player)this._arrows.remove(projectile);
    int level = getLevel(player);
    
    if (this._hitCount.containsKey(player))
    {

      event.AddMod(player.getName(), GetName(), ((Integer)this._hitCount.get(player)).intValue() * (1.0D + 0.5D * level), true);
      
      int limit = Math.min(1 + level, ((Integer)this._hitCount.get(player)).intValue() + 1);
      
      this._hitCount.put(player, Integer.valueOf(limit));
      

      UtilPlayer.message(projectile.getShooter(), F.main(GetClassType().name(), GetName() + ": " + 
        F.elem(new StringBuilder().append(this._hitCount.get(player)).append(" Consecutive Hits").toString()) + C.cGray + " (" + F.skill(new StringBuilder("+").append(limit * 2).append("Damage").toString()) + C.cGray + ")"));
    }
    else
    {
      this._hitCount.put(player, Integer.valueOf(1));
    }
    
    projectile.remove();
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Entity> remove = new HashSet();
    
    for (Entity cur : this._arrows.keySet()) {
      if ((cur.isDead()) || (!cur.isValid()) || (cur.isOnGround()))
        remove.add(cur);
    }
    for (Entity cur : remove)
    {
      Player player = (Player)this._arrows.remove(cur);
      
      if ((player != null) && 
        (this._hitCount.remove(player) != null)) {
        UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.elem("0 Consecutive Hits")));
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._hitCount.remove(player);
  }
}
