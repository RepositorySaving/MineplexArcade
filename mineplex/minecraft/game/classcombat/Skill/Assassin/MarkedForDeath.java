package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;

public class MarkedForDeath extends SkillActive
{
  private HashSet<Entity> _arrows = new HashSet();
  private HashSet<Player> _active = new HashSet();
  private HashMap<LivingEntity, Long> _markedTime = new HashMap();
  private HashMap<LivingEntity, Double> _markedDamage = new HashMap();
  










  public MarkedForDeath(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Your next arrow will mark players,", 
      "making them take #2.5#1.5 more damage", 
      "from the next melee attack.", 
      "", 
      "Lasts for #3#1 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._active.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 2.5F, 2.0F);
  }
  
  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._active.remove(player)) {
      return;
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(getLevel(player))) + "."));
    
    this._arrows.add(event.getProjectile());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageMark(CustomDamageEvent event)
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
    if (!this._arrows.contains(projectile)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    damagee.getWorld().playSound(damagee.getLocation(), Sound.BLAZE_BREATH, 2.5F, 2.0F);
    

    UtilPlayer.message(event.GetDamageePlayer(), F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(mineplex.core.common.util.UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
    

    this._markedTime.put(damagee, Long.valueOf(System.currentTimeMillis() + (3000 + 1000 * level)));
    this._markedDamage.put(damagee, Double.valueOf(2.5D + 1.5D * level));
    

    projectile.remove();
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageAmplify(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    if ((!this._markedTime.containsKey(damagee)) || (!this._markedDamage.containsKey(damagee))) {
      return;
    }
    long time = ((Long)this._markedTime.remove(damagee)).longValue();
    double damage = ((Double)this._markedDamage.remove(damagee)).doubleValue();
    
    if (System.currentTimeMillis() > time) {
      return;
    }
    event.AddMod(GetName(), GetName(), damage, true);
  }
  
  @EventHandler
  public void Particle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._arrows)
    {
      mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.MOB_SPELL, ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
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
    this._active.remove(player);
    this._markedTime.remove(player);
    this._markedDamage.remove(player);
  }
}
