package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class RopedArrow extends SkillActive
{
  private HashSet<Entity> _arrows = new HashSet();
  private HashSet<Player> _roped = new HashSet();
  










  public RopedArrow(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Prepare a roped arrow;", 
      "Your next arrow will pull you", 
      "pull you in after it hits." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._roped.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 2.5F, 2.0F);
  }
  
  @EventHandler
  public void handleShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._roped.remove(player)) {
      return;
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You fired " + F.skill(GetName(getLevel(player))) + "."));
    
    this._arrows.add(event.getProjectile());
    
    UtilEnt.Leash(player, event.getProjectile(), false, false);
  }
  
  @EventHandler
  public void ArrowHit(ProjectileHitEvent event)
  {
    if (!this._arrows.remove(event.getEntity())) {
      return;
    }
    Projectile proj = event.getEntity();
    
    if (proj.getShooter() == null) {
      return;
    }
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }
    
    int level = getLevel((Player)proj.getShooter());
    if (level == 0) { return;
    }
    Vector vec = UtilAlg.getTrajectory(proj.getShooter(), proj);
    double mult = proj.getVelocity().length() / 3.0D;
    

    UtilAction.velocity(proj.getShooter(), vec, 
      0.4D + mult, false, 0.0D, 0.3D * mult, 1.2D * mult, true);
    

    proj.getWorld().playSound(proj.getLocation(), Sound.BLAZE_BREATH, 2.5F, 2.0F);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Entity> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Entity arrow = (Entity)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._roped.remove(player);
  }
}
