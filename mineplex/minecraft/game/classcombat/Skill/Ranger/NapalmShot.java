package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class NapalmShot extends SkillActive
{
  private HashSet<Entity> _arrows = new HashSet();
  private HashSet<Player> _napalm = new HashSet();
  










  public NapalmShot(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Prepare a napalm shot;", 
      "Your next arrow will explode on", 
      "impact, releasing #8#8 flames." });
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
    this._napalm.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 2.5F, 2.0F);
  }
  
  @EventHandler
  public void BowShoot(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof org.bukkit.entity.Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._napalm.remove(player)) {
      return;
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You fired " + F.skill(GetName(getLevel(player))) + "."));
    
    this._arrows.add(event.getProjectile());
    event.getProjectile().setFireTicks(120);
  }
  
  @EventHandler
  public void ProjectileHit(ProjectileHitEvent event)
  {
    Projectile proj = event.getEntity();
    
    if (!this._arrows.remove(proj)) {
      return;
    }
    if (proj.getShooter() == null) {
      return;
    }
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }
    Player damager = (Player)proj.getShooter();
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    proj.getWorld().playSound(proj.getLocation(), Sound.EXPLODE, 0.4F, 2.0F);
    
    for (int i = 0; i < 8 + 8 * level; i++)
    {
      Item fire = proj.getWorld().dropItemNaturally(proj.getLocation(), ItemStackFactory.Instance.CreateStack(Material.FIRE, 1));
      this.Factory.Fire().Add(fire, damager, 16.0D, 0.25D, 2.0D, 0, GetName());
      fire.setVelocity(fire.getVelocity().multiply(1.0D + 0.15D * level));
    }
    

    proj.remove();
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._arrows)
    {
      UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.LAVA, ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    }
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
      
      if ((arrow.isDead()) || (!arrow.isValid()) || (arrow.isOnGround())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._napalm.remove(player);
  }
}
