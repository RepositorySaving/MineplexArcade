package nautilus.game.arcade.game.games.halloween.creatures;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import net.minecraft.server.v1_7_R3.EntityArrow;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PumpkinKing extends CreatureBase<Skeleton>
{
  private int _state = 0;
  private long _stateTime = System.currentTimeMillis();
  
  private ArrayList<Skeleton> _minions = new ArrayList();
  private int _minionsMax = 12;
  private boolean _minionSpawn = true;
  private HashMap<org.bukkit.entity.Entity, Player> _minionTargets = new HashMap();
  private HashMap<Skeleton, Long> _minionAttack = new HashMap();
  
  private ArrayList<Slime> _shields = new ArrayList();
  private int _shieldsMax = 6;
  private long _shieldSpawn = 0L;
  
  private Location _kingLocation;
  private Player _kingTarget = null;
  
  private HashSet<Arrow> _arrows = new HashSet();
  
  public PumpkinKing(Game game, Location loc)
  {
    super(game, null, Skeleton.class, loc);
    
    this._kingLocation = loc;
  }
  

  public void SpawnCustom(Skeleton ent)
  {
    ent.setSkeletonType(org.bukkit.entity.Skeleton.SkeletonType.WITHER);
    ent.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
    
    ent.setMaxHealth(400.0D);
    ent.setHealth(ent.getMaxHealth());
    
    ent.getWorld().strikeLightningEffect(ent.getLocation());
    ent.getWorld().strikeLightningEffect(ent.getLocation());
    ent.getWorld().strikeLightningEffect(ent.getLocation());
  }
  


  public void Damage(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity().equals(GetEntity()))
    {
      if (event.GetProjectile() != null) {
        event.GetProjectile().remove();
      }
      if (this._shields.size() > 0)
      {
        event.SetCancelled("Shielded");
        UtilPlayer.message(event.GetDamagerPlayer(true), F.main("Boss", "You must destroy " + F.elem("Flame Shields") + " first!"));
      }
      else if (this._minions.size() > 0)
      {
        event.SetCancelled("Shielded");
        UtilPlayer.message(event.GetDamagerPlayer(true), F.main("Boss", "You must destroy " + F.elem("Pumpkin Minions") + " first!"));
      }
      
      if (event.GetDamagerPlayer(true) == null) {
        event.SetCancelled("Non-Player");
      }
      event.SetKnockback(false);

    }
    else if (this._minions.contains(event.GetDamageeEntity()))
    {
      if (event.GetProjectile() != null) {
        event.GetProjectile().remove();
      }
      if (this._shields.size() > 0)
      {
        event.SetCancelled("Shielded");
        UtilPlayer.message(event.GetDamagerPlayer(true), F.main("Boss", "You must destroy " + F.elem("Flame Shields") + " first!"));
      }
      else if (event.GetProjectile() != null)
      {
        event.SetCancelled("Projectile");
        UtilPlayer.message(event.GetDamagerPlayer(true), F.main("Boss", "Projectiles cannot harm " + F.elem("Pumpkin Minions") + "!"));
      }
      
    }
    else if (this._shields.contains(event.GetDamageeEntity()))
    {
      event.SetCancelled("Shield Break");
      
      if ((event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) && (event.GetCause() != EntityDamageEvent.DamageCause.LIGHTNING)) {
        return;
      }
      event.GetProjectile().remove();
      
      if (event.GetDamagerPlayer(true) == null) {
        return;
      }
      
      this.Host.Manager.GetBlood().Effects(event.GetDamageeEntity().getLocation(), 10, 0.2D, null, 0.0F, 0.0F, Material.FIRE, (byte)0, 10, false);
      event.GetDamageeEntity().getWorld().playEffect(event.GetDamageeEntity().getLocation(), Effect.STEP_SOUND, 51);
      

      this._shields.remove(event.GetDamageeEntity());
      event.GetDamageeEntity().remove();
      

      KingUpdateHealth();
      

      this._shieldSpawn = System.currentTimeMillis();
    }
  }
  


  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.FASTER) {
      StateUpdate();
    }
    if (event.getType() == UpdateType.FAST) {
      KingDestroyBlocks();
    }
    if (event.getType() == UpdateType.TICK) {
      KingUpdateHealth();
    }
    
    if (event.getType() == UpdateType.TICK) {
      MinionOrbit();
    }
    if (event.getType() == UpdateType.FASTER) {
      MinionAttack();
    }
    if (event.getType() == UpdateType.TICK) {
      MinionAttackDamage();
    }
    if (event.getType() == UpdateType.FASTEST) {
      MinionArrow();
    }
    if (event.getType() == UpdateType.FAST) {
      MinionSpawn();
    }
    
    if (event.getType() == UpdateType.FAST) {
      KingControl();
    }
    if (event.getType() == UpdateType.SEC) {
      KingLeap();
    }
    if (event.getType() == UpdateType.SEC) {
      KingBomb();
    }
    if (event.getType() == UpdateType.SLOW) {
      KingTarget();
    }
    if (event.getType() == UpdateType.TICK) {
      KingTrail();
    }
    
    if (event.getType() == UpdateType.TICK) {
      ShieldOrbit(false);
    }
    if (event.getType() == UpdateType.FAST) {
      ShieldSpawn();
    }
  }
  

  private void KingTrail()
  {
    if (GetState() >= 4)
    {

      UtilParticle.PlayParticle(UtilParticle.ParticleType.FIREWORKS_SPARK, ((Skeleton)GetEntity()).getLocation().add(0.0D, 1.5D, 0.0D), 0.2F, 0.4F, 0.2F, 0.0F, 1);
    }
  }
  
  private void KingTarget()
  {
    if (Math.random() > 0.25D) {
      this._kingTarget = GetRandomPlayer();
    }
  }
  
  private void KingControl() {
    if (GetState() >= 4)
    {
      if (this._kingTarget == null) {
        this._kingTarget = GetRandomPlayer();
      }
      ((Skeleton)GetEntity()).setTarget(this._kingTarget);
      
      Location loc = this._kingTarget.getLocation();
      if (UtilMath.offset(loc, ((Skeleton)GetEntity()).getLocation()) > 16.0D) {
        loc = ((Skeleton)GetEntity()).getLocation().add(UtilAlg.getTrajectory(((Skeleton)GetEntity()).getLocation(), loc).multiply(16));
      }
      
      EntityCreature ec = ((CraftCreature)GetEntity()).getHandle();
      Navigation nav = ec.getNavigation();
      nav.a(loc.getX(), loc.getY(), loc.getZ(), 1.0D);
    }
    else
    {
      ((Skeleton)GetEntity()).teleport(this._kingLocation);
    }
  }
  
  private void KingLeap()
  {
    if (GetState() < 4) {
      return;
    }
    if (this._kingTarget == null) {
      return;
    }
    if (Math.random() > 0.4D) {
      return;
    }
    UtilAction.velocity(GetEntity(), UtilAlg.getTrajectory2d(GetEntity(), this._kingTarget), 1.2D, false, 0.0D, 0.4D, 10.0D, true);
  }
  
  private void KingBomb()
  {
    if (GetState() < 4) {
      return;
    }
    if (this._kingTarget == null) {
      return;
    }
    if (Math.random() > 0.4D) {
      return;
    }
    TNTPrimed tnt = (TNTPrimed)((Skeleton)GetEntity()).getWorld().spawn(((Skeleton)GetEntity()).getEyeLocation().add(((Skeleton)GetEntity()).getLocation().getDirection()), TNTPrimed.class);
    
    Player target = GetRandomPlayer();
    
    UtilAction.velocity(tnt, UtilAlg.getTrajectory(tnt, target), 1.2D, false, 0.0D, 0.4D, 10.0D, false);
  }
  
  private void KingUpdateHealth()
  {
    for (Player player : )
    {
      if (this._shields.size() > 0)
      {
        double percent = this._shields.size() / this._shieldsMax;
        UtilDisplay.displayTextBar(this.Host.Manager.GetPlugin(), player, percent, C.cGold + C.Bold + "The Pumpkin King" + C.cWhite + C.Bold + " - " + C.cYellow + C.Bold + "Flame Shield");


      }
      else if (this._minions.size() > 0)
      {
        double percent = this._minions.size() / this._minionsMax;
        UtilDisplay.displayTextBar(this.Host.Manager.GetPlugin(), player, percent, C.cGold + C.Bold + "The Pumpkin King" + C.cWhite + C.Bold + " - " + C.cYellow + C.Bold + "Pumpkin Soldiers");
      }
      else
      {
        double percent = ((Skeleton)GetEntity()).getHealth() / ((Skeleton)GetEntity()).getMaxHealth();
        UtilDisplay.displayTextBar(this.Host.Manager.GetPlugin(), player, percent, C.cGold + C.Bold + "The Pumpkin King" + C.cWhite + C.Bold + " - " + C.cYellow + C.Bold + "Final Stage");
      }
    }
  }
  

  private void KingDestroyBlocks()
  {
    this.Host.Manager.GetExplosion().BlockExplosion(mineplex.core.common.util.UtilBlock.getInRadius(((Skeleton)GetEntity()).getLocation(), 7.0D).keySet(), ((Skeleton)GetEntity()).getLocation(), true);
  }
  
  @org.bukkit.event.EventHandler
  public void MinionSpawn()
  {
    Iterator<Skeleton> shieldIterator = this._minions.iterator();
    while (shieldIterator.hasNext())
    {
      Skeleton skel = (Skeleton)shieldIterator.next();
      
      if (!skel.isValid()) {
        shieldIterator.remove();
      }
    }
    if (!this._minionSpawn) {
      return;
    }
    for (int i = 0; i < this._minionsMax; i++)
    {
      this.Host.CreatureAllowOverride = true;
      Skeleton skel = (Skeleton)((Skeleton)GetEntity()).getWorld().spawn(((Skeleton)GetEntity()).getLocation(), Skeleton.class);
      this.Host.CreatureAllowOverride = false;
      
      this.Host.Manager.GetCondition().Factory().Invisible("Cloak", skel, skel, 999999.0D, 0, false, false, false);
      
      skel.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
      skel.getEquipment().setItemInHand(new ItemStack(Material.BOW));
      
      skel.setMaxHealth(50.0D);
      skel.setHealth(skel.getMaxHealth());
      
      this._minions.add(skel);
      
      UtilEnt.Vegetate(skel);
    }
    
    this._minionSpawn = false;
  }
  
  public void MinionOrbit()
  {
    if ((GetState() != 0) && (GetState() != 1) && (GetState() != 2)) {
      return;
    }
    for (int i = 0; i < this._minions.size(); i++)
    {
      Skeleton minion = (Skeleton)this._minions.get(i);
      
      UtilParticle.PlayParticle(UtilParticle.ParticleType.WITCH_MAGIC, minion.getEyeLocation(), 0.1F, 0.1F, 0.1F, 0.0F, 1);
      
      minion.setTarget(null);
      
      double lead = i * (6.283185307179586D / this._minions.size());
      
      double sizeMod = 2 + this._minions.size() / 12;
      

      double speed = 20.0D;
      double oX = Math.sin(((Skeleton)GetEntity()).getTicksLived() / speed + lead) * 2.0D * sizeMod;
      double oY = 1.0D;
      double oZ = Math.cos(((Skeleton)GetEntity()).getTicksLived() / speed + lead) * 2.0D * sizeMod;
      Location loc = ((Skeleton)GetEntity()).getLocation().add(oX, oY, oZ);
      
      if (UtilMath.offset(loc, minion.getLocation()) > 16.0D)
      {
        this.Host.Manager.GetBlood().Effects(minion.getEyeLocation(), 10, 0.2D, Sound.SKELETON_HURT, 1.0F, 1.0F, Material.BONE, (byte)0, 20, false);
        minion.teleport(loc);
      }
      else
      {
        loc.setYaw(UtilAlg.GetYaw(UtilAlg.getTrajectory(GetEntity(), minion)));
        

        EntityCreature ec = ((CraftCreature)minion).getHandle();
        ec.getControllerMove().a(loc.getX(), loc.getY(), loc.getZ(), 1.4D);
      }
    }
  }
  
  public void MinionAttack() {
    if (GetState() != 3) {
      return;
    }
    if (this._minions.isEmpty()) {
      return;
    }
    Skeleton minion = (Skeleton)this._minions.remove(0);
    
    LivingEntity target = (LivingEntity)this._minionTargets.get(minion);
    if (target == null) {
      return;
    }
    minion.setTarget(target);
    
    Location loc = target.getLocation().add(UtilAlg.getTrajectory(target, minion).multiply(1));
    if (UtilMath.offset(loc, minion.getLocation()) > 12.0D) {
      loc = minion.getLocation().add(UtilAlg.getTrajectory(minion.getLocation(), loc).multiply(12));
    }
    
    EntityCreature ec = ((CraftCreature)minion).getHandle();
    Navigation nav = ec.getNavigation();
    nav.a(loc.getX(), loc.getY(), loc.getZ(), 1.0D);
    
    this._minions.add(minion);
  }
  
  private void MinionAttackDamage()
  {
    if (GetState() != 3) {
      return;
    }
    for (int i = 0; i < this._minions.size(); i++)
    {
      final Skeleton minion = (Skeleton)this._minions.get(i);
      
      UtilParticle.PlayParticle(UtilParticle.ParticleType.WITCH_MAGIC, minion.getEyeLocation(), 0.1F, 0.1F, 0.1F, 0.0F, 1);
      
      LivingEntity target = (LivingEntity)this._minionTargets.get(minion);
      if (target != null)
      {

        if (UtilMath.offset(minion, target) <= 2.0D)
        {

          if ((!this._minionAttack.containsKey(minion)) || (UtilTime.elapsed(((Long)this._minionAttack.get(minion)).longValue(), 500L)))
          {


            this.Host.Manager.GetDamage().NewDamageEvent(target, minion, null, 
              EntityDamageEvent.DamageCause.ENTITY_ATTACK, 3.0D, true, true, false, 
              UtilEnt.getName(minion), GetName());
            

            minion.getWorld().playSound(minion.getLocation(), Sound.ENDERMAN_SCREAM, 2.0F, 2.0F);
            minion.getWorld().playSound(minion.getLocation(), Sound.ENDERMAN_SCREAM, 2.0F, 2.0F);
            

            minion.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            

            UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Host.Manager.GetPlugin(), new Runnable()
            {
              public void run()
              {
                minion.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
              }
            }, 4L);
            
            this._minionAttack.put(minion, Long.valueOf(System.currentTimeMillis()));
          } }
      }
    }
  }
  
  public void MinionArrow() {
    Iterator<Arrow> arrowIterator = this._arrows.iterator();
    while (arrowIterator.hasNext())
    {
      Arrow arrow = (Arrow)arrowIterator.next();
      
      if ((arrow.getLocation().getY() > 30.0D) && (arrow.getVelocity().getY() > 0.0D))
      {
        Player target = (Player)this.Host.GetPlayers(true).get(UtilMath.r(this.Host.GetPlayers(true).size()));
        arrow.teleport(target.getLocation().add(Math.random() * 8.0D - 4.0D, Math.random() * 10.0D + 30.0D, Math.random() * 8.0D - 4.0D));
        arrow.setVelocity(arrow.getVelocity().setY(-0.1D));


      }
      else if ((arrow.getTicksLived() > 200) || (arrow.isOnGround()))
      {
        if ((arrow.isValid()) && (Math.random() > 0.5D))
        {
          try
          {
            EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();
            
            Field fieldX = EntityArrow.class.getDeclaredField("d");
            Field fieldY = EntityArrow.class.getDeclaredField("e");
            Field fieldZ = EntityArrow.class.getDeclaredField("f");
            
            fieldX.setAccessible(true);
            fieldY.setAccessible(true);
            fieldZ.setAccessible(true);
            
            int x = fieldX.getInt(entityArrow);
            int y = fieldY.getInt(entityArrow);
            int z = fieldZ.getInt(entityArrow);
            
            Block block = arrow.getWorld().getBlockAt(x, y, z);
            
            if (block.getY() > ((Skeleton)GetEntity()).getLocation().getY()) {
              block.breakNaturally();
            }
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
        
        arrow.remove();
        arrowIterator.remove();
      }
    }
    

    if (GetState() == 1)
    {
      for (int i = 0; i < this._minions.size(); i++)
      {
        Skeleton minion = (Skeleton)this._minions.get(i);
        
        if (minion.isValid())
        {

          Vector traj = UtilAlg.getTrajectory2d(GetEntity(), minion);
          traj.add(new Vector(0.0D, Math.random() * 0.25D, 0.0D));
          
          Arrow arrow = ((Skeleton)GetEntity()).getWorld().spawnArrow(minion.getEyeLocation().add(traj), traj, 2.0F, 16.0F);
          arrow.setShooter(minion);
          
          this._arrows.add(arrow);
        }
        
      }
      
    } else if (GetState() == 2)
    {
      for (int i = 0; i < this._minions.size(); i++)
      {
        Skeleton minion = (Skeleton)this._minions.get(i);
        
        if (minion.isValid())
        {

          Vector traj = new Vector(0, 1, 0);
          
          Arrow arrow = ((Skeleton)GetEntity()).getWorld().spawnArrow(minion.getEyeLocation().add(traj), traj, 2.0F, 16.0F);
          arrow.setShooter(minion);
          
          this._arrows.add(arrow);
        }
      }
    }
  }
  
  @org.bukkit.event.EventHandler
  public void ShieldSpawn() {
    if ((GetState() == 3) || (GetState() == 4)) {
      return;
    }
    Iterator<Slime> shieldIterator = this._shields.iterator();
    while (shieldIterator.hasNext())
    {
      Slime slime = (Slime)shieldIterator.next();
      
      if (!slime.isValid()) {
        shieldIterator.remove();
      }
    }
    if (!UtilTime.elapsed(this._shieldSpawn, 10000L)) {
      return;
    }
    if (this._shields.size() >= this._shieldsMax) {
      return;
    }
    
    this._shieldSpawn = System.currentTimeMillis();
    
    int toSpawn = 1;
    if (this._shields.size() == 0)
    {
      toSpawn = this._shieldsMax;
      

      ((Skeleton)GetEntity()).getWorld().playSound(((Skeleton)GetEntity()).getLocation(), Sound.WITHER_HURT, 10.0F, 1.5F);
      
      if (((Skeleton)GetEntity()).getTicksLived() > 100) {
        this.Host.Announce(C.cAqua + C.Bold + "Flame Shield has regenerated!");
      }
    }
    else
    {
      ((Skeleton)GetEntity()).getWorld().playSound(((Skeleton)GetEntity()).getLocation(), Sound.WITHER_HURT, 1.0F, 2.0F);
    }
    
    for (int i = 0; i < toSpawn; i++)
    {

      this.Host.CreatureAllowOverride = true;
      MagmaCube ent = (MagmaCube)((Skeleton)GetEntity()).getWorld().spawn(((Skeleton)GetEntity()).getLocation().add(0.0D, 6.0D, 0.0D), MagmaCube.class);
      ent.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
      this._shields.add(ent);
      UtilEnt.Vegetate(ent);
      ent.setSize(1);
      this.Host.CreatureAllowOverride = false;
      

      ShieldOrbit(false);
    }
  }
  
  public void ShieldOrbit(boolean teleport)
  {
    for (int i = 0; i < this._shields.size(); i++)
    {
      Slime shield = (Slime)this._shields.get(i);
      
      UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, shield.getLocation(), 0.1F, 0.1F, 0.1F, 0.0F, 1);
      
      double lead = i * (6.283185307179586D / this._shields.size());
      
      double sizeMod = 2.0D;
      

      double speed = 10.0D;
      double oX = -Math.sin(((Skeleton)GetEntity()).getTicksLived() / speed + lead) * 2.0D * sizeMod;
      double oY = 6.0D;
      double oZ = Math.cos(((Skeleton)GetEntity()).getTicksLived() / speed + lead) * 2.0D * sizeMod;
      
      if (teleport)
      {
        shield.teleport(((Skeleton)GetEntity()).getLocation().add(oX, oY, oZ));
      }
      else
      {
        UtilAction.velocity(shield, 
          UtilAlg.getTrajectory(shield.getLocation(), ((Skeleton)GetEntity()).getLocation().add(oX, oY, oZ)), 
          0.4D, false, 0.0D, 0.1D, 1.0D, true);
      }
    }
    
    if (this._shields.size() > 0) {
      ((Skeleton)GetEntity()).getWorld().playEffect(((Skeleton)GetEntity()).getLocation().add(0.0D, 6.0D, 0.0D), Effect.ENDER_SIGNAL, 0);
    }
  }
  
  public int GetState() {
    return this._state;
  }
  
  public void SetState(int state)
  {
    this._state = state;
    this._stateTime = System.currentTimeMillis();
    
    if (state == 3)
    {

      for (int i = 0; i < this._minions.size(); i++)
      {
        Skeleton minion = (Skeleton)this._minions.get(i);
        
        minion.getEquipment().setItemInHand(null);
        

        this.Host.Manager.GetCondition().Factory().Speed("Minion Speed", minion, minion, 15.0D, 0, false, false, false);
        

        ((Skeleton)GetEntity()).getWorld().playSound(((Skeleton)GetEntity()).getLocation(), Sound.WITHER_SPAWN, 10.0F, 1.5F);
        

        this._minionTargets.put(minion, GetRandomPlayer());
      }
      

      this.Host.Announce(C.cAqua + C.Bold + "Kill the Pumpkin Minions!");
      
      MinionAttack();
    }
  }
  
  public void StateUpdate()
  {
    if (GetEntity() == null) {
      return;
    }
    if (GetState() == 0)
    {
      if (UtilTime.elapsed(this._stateTime, 10000L))
      {
        if (Math.random() > 0.5D) {
          SetState(1);
        } else {
          SetState(2);
        }
      }
    }
    else if (GetState() == 1)
    {
      if (UtilTime.elapsed(this._stateTime, 5000L))
      {
        SetState(0);
      }
      
    }
    else if (GetState() == 2)
    {
      if (UtilTime.elapsed(this._stateTime, 5000L))
      {
        SetState(0);
      }
    }
    else if (GetState() == 3)
    {
      if (UtilTime.elapsed(this._stateTime, 20000L))
      {
        SetState(0);
        

        for (int i = 0; i < this._minions.size(); i++)
        {
          Skeleton minion = (Skeleton)this._minions.get(i);
          minion.setTarget(null);
          minion.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
          minion.getEquipment().setItemInHand(new ItemStack(Material.BOW));
        }
        
        ShieldSpawn();
        
        this._minionTargets.clear();
      }
    }
    

    if ((GetState() != 3) && (UtilTime.elapsed(this._stateTime, 2000L)))
    {
      if ((this._shields.size() == 0) && (this._minions.size() > 0))
      {
        SetState(3);
      }
    }
    

    if ((GetState() != 4) && (UtilTime.elapsed(this._stateTime, 2000L)))
    {
      if ((this._shields.size() == 0) && (this._minions.size() == 0))
      {
        SetState(4);
        

        this.Host.Announce(C.cAqua + C.Bold + "Kill the Pumpkin King!!!");
        

        ((Skeleton)GetEntity()).getWorld().playSound(((Skeleton)GetEntity()).getLocation(), Sound.WITHER_SPAWN, 10.0F, 1.5F);
        

        this.Host.Manager.GetCondition().Factory().Speed("King Speed", GetEntity(), GetEntity(), 9999.0D, 1, false, false, false);
        

        ((Skeleton)GetEntity()).getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
      }
    }
  }
  
  public void Target(EntityTargetEvent event)
  {
    if (event.getEntity().equals(GetEntity()))
    {
      if ((GetState() != 4) || (this._kingTarget == null) || (!this._kingTarget.equals(event.getTarget())))
      {
        event.setCancelled(true);
      }
    }
    
    if (this._minions.contains(event.getEntity()))
    {
      if (GetState() != 3)
      {
        event.setCancelled(true);


      }
      else if (!this._minionTargets.containsKey(event.getEntity()))
      {
        event.setCancelled(true);
      }
      else
      {
        Player player = (Player)this._minionTargets.get(event.getEntity());
        
        if (!player.equals(event.getTarget())) {
          event.setCancelled(true);
        }
        if (!this.Host.IsAlive(player)) {
          this._minionTargets.put(event.getEntity(), GetRandomPlayer());
        }
      }
    }
  }
  
  public Player GetRandomPlayer()
  {
    if (this.Host.GetPlayers(true).isEmpty()) {
      return null;
    }
    return (Player)this.Host.GetPlayers(true).get(UtilMath.r(this.Host.GetPlayers(true).size()));
  }
  
  public boolean IsFinal()
  {
    return this._minions.size() == 0;
  }
}
