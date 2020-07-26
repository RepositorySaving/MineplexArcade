package nautilus.game.arcade.game.games.christmas.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import nautilus.game.arcade.game.games.christmas.content.BossFloor;
import nautilus.game.arcade.game.games.christmas.content.BossMobs;
import nautilus.game.arcade.game.games.christmas.content.BossSnowmanPattern;
import nautilus.game.arcade.game.games.christmas.content.PumpkinKing;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Part5
  extends Part
{
  private ArrayList<Location> _spawn;
  private ArrayList<Location> _floor;
  private ArrayList<Location> _playerSpawns;
  private ArrayList<Location> _hurt;
  private ArrayList<Location> _mobs;
  private ArrayList<Location> _snowmenA;
  private ArrayList<Location> _snowmenB;
  private ArrayList<Location> _glass;
  private PumpkinKing _boss;
  private BossSnowmanPattern _bossSnowmen;
  private BossFloor _bossFloor;
  private BossMobs _bossMob;
  private long _bossDamageDelay = 0L;
  
  private int _state = 0;
  private long _stateTime = 0L;
  private int _stateHealthMax = 6;
  private int _stateHealth = 6;
  
  private boolean _a = false;
  private boolean _b = false;
  private boolean _c = false;
  private boolean _d = false;
  private boolean _e = false;
  private boolean _f = false;
  private boolean _g = false;
  private boolean _h = false;
  private boolean _i = false;
  
  private long _dialogueDelay = 0L;
  private long _delayTime = 4000L;
  



  public Part5(Christmas host, Location sleigh, Location[] presents, ArrayList<Location> snowmenA, ArrayList<Location> snowmenB, ArrayList<Location> mobs, ArrayList<Location> floor, ArrayList<Location> playerSpawns, ArrayList<Location> hurt, ArrayList<Location> spawn, ArrayList<Location> glass)
  {
    super(host, sleigh, presents);
    
    this._floor = floor;
    this._playerSpawns = playerSpawns;
    this._spawn = spawn;
    this._mobs = mobs;
    this._snowmenA = snowmenA;
    this._snowmenB = snowmenB;
    this._glass = glass;
    this._hurt = hurt;
    
    for (Location loc : this._glass) {
      loc.getBlock().setType(Material.GLASS);
    }
    for (Location loc : this._spawn) {
      loc.getBlock().setType(Material.AIR);
    }
    for (Location loc : this._playerSpawns) {
      loc.getBlock().setType(Material.AIR);
    }
    for (Location loc : hurt) {
      loc.getBlock().setType(Material.AIR);
    }
    for (Location loc : this._mobs) {
      loc.getBlock().setType(Material.AIR);
    }
    for (Location loc : this._snowmenA) {
      loc.getBlock().setType(Material.AIR);
    }
    for (Location loc : this._snowmenB) {
      loc.getBlock().setType(Material.AIR);
    }
  }
  
  public void Activate()
  {
    this._bossSnowmen = new BossSnowmanPattern(this, this._snowmenA, this._snowmenB, GetSleighWaypoint());
    this._bossFloor = new BossFloor(this, this._floor);
    this._bossMob = new BossMobs(this, this._mobs);
  }
  

  public boolean CanFinish()
  {
    return (this._boss != null) && (this._boss.IsDead());
  }
  
  public int GetState()
  {
    return this._state;
  }
  
  public long GetStateTime()
  {
    return this._stateTime;
  }
  
  @EventHandler
  public void UpdateIntro(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!this._a)
    {
      if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 1.0D) {
        return;
      }
      this._a = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.SantaSay("WHAT IS THIS?! Who's castle is this?!");
    }
    else if ((this._a) && (!this._b) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._b = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.BossSay("Unknown Voice", "I will destroy Christmas, Santa Claus!");
    }
    else if ((this._b) && (!this._c) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._c = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.BossSay("Unknown Voice", "Not even your friends can save you now!");
    }
    else if ((this._c) && (!this._d) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._d = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.SantaSay("WHO IS THAT?! Reveal yourself!");
    }
    else if ((this._d) && (!this._e) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._e = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.BossSay("Unknown Voice", "It is me... THE PUMPKIN KING!");
      

      this._boss = new PumpkinKing(this, (Location)this._spawn.get(0), this._floor);
    }
    else if ((this._e) && (!this._f) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._f = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.BossSay("Pumpkin King", "Revenge shall be mine! You will all die!");
    }
    else if ((this._f) && (!this._g) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._g = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.SantaSay("My friends beat you before, and they'll do it again!");
    }
    else if ((this._g) && (!this._h) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._h = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.SantaSay("Prepare yourselves for battle!");
      

      for (int i = 0; i < this.Host.GetPlayers(true).size(); i++)
      {
        Player player = (Player)this.Host.GetPlayers(true).get(i);
        
        player.leaveVehicle();
        
        player.teleport((Location)this._playerSpawns.get(i % this._playerSpawns.size()));
        player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 1.0F);
      }
    }
    else if ((this._h) && (!this._i) && (UtilTime.elapsed(this._dialogueDelay, this._delayTime)))
    {
      this._i = true;
      this._dialogueDelay = System.currentTimeMillis();
      this.Host.BossSay("Pumpkin King", "Prepare to die, you pathetic humans!");
    }
  }
  
  @EventHandler
  public void ElementUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (!this._i) {
      return;
    }
    if (this._bossSnowmen != null) {
      this._bossSnowmen.Update();
    }
    if (this._bossFloor != null) {
      this._bossFloor.Update();
    }
    if (this._bossMob != null) {
      this._bossMob.Update();
    }
  }
  
  public void NextState() {
    this._state += 1;
    this._stateTime = System.currentTimeMillis();
    
    this._stateHealth = this._stateHealthMax;
    
    this._bossSnowmen.SetActive(false, 0);
    this._bossFloor.SetActive(false, 0);
    this._bossMob.SetActive(false, 0);
    
    if (this._state > 7)
    {
      this._boss.Die();
      
      for (Location loc : this._glass)
      {
        loc.getWorld().playEffect(loc, Effect.STEP_SOUND, 20);
        loc.getBlock().setType(Material.AIR);
      }
    }
  }
  

  @EventHandler
  public void HurtPlayer(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = this.Host.GetPlayers(true).iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Player player = (Player)localIterator1.next();
      
      localIterator2 = this._hurt.iterator(); continue;Location loc = (Location)localIterator2.next();
      
      if (UtilMath.offset(player.getLocation(), loc) < 1.5D)
      {
        player.damage(2.0D);
        UtilAction.velocity(player, UtilAlg.getTrajectory2d(player.getLocation(), (Location)this._spawn.get(0)), 1.0D, true, 0.6D, 0.0D, 1.0D, true);
      }
    }
  }
  

  @EventHandler
  public void StateUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if ((this._boss == null) || (this._boss.IsDead())) {
      return;
    }
    if (!this._i) {
      return;
    }
    
    SetObjectiveText("Defeat the Pumpkin King!", 
      (8 * this._stateHealthMax - this._state * this._stateHealthMax - (this._stateHealthMax - this._stateHealth)) / (8.0D * this._stateHealthMax));
    
    if (this._bossFloor.ShouldBossMove())
    {
      this._boss.MoveUpdate();
      this._boss.TNTUpdate();
    }
    else
    {
      this._boss.StayIdle();
    }
    

    if (this._state != 0)
    {



      if (this._state == 1)
      {
        this._bossFloor.SetActive(true, 0);

      }
      else if (this._state == 2)
      {
        this._bossMob.SetActive(true, 0);

      }
      else if (this._state == 3)
      {
        this._bossSnowmen.SetActive(true, 0);

      }
      else if (this._state == 4)
      {
        this._bossFloor.SetActive(true, 1);

      }
      else if (this._state == 5)
      {
        this._bossMob.SetActive(true, 1);

      }
      else if (this._state == 6)
      {
        this._bossSnowmen.SetActive(true, 1);

      }
      else if (this._state == 7)
      {
        this._bossSnowmen.SetActive(true, 1);
        this._bossMob.SetActive(true, 1);
        this._bossFloor.SetActive(true, 1);
      }
    }
  }
  
  @EventHandler
  public void Skip(PlayerCommandPreprocessEvent event) {
    if ((event.getMessage().equals("/boss")) && 
      (event.getPlayer().getName().equals("Chiss")))
    {
      event.setCancelled(true);
      
      NextState();
    }
  }
  











  @EventHandler(priority=EventPriority.LOWEST)
  public void TNTExplosion(EntityExplodeEvent event)
  {
    event.blockList().clear();
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (this._boss == null) {
      return;
    }
    if (!this._boss.GetEntity().equals(event.GetDamageeEntity())) {
      return;
    }
    event.SetCancelled("Boss Damage");
    

    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      UtilAction.velocity(damager, UtilAlg.getTrajectory(event.GetDamageeEntity(), damager), 1.0D, true, 0.6D, 0.0D, 1.0D, true);
    }
    if (!this.Host.IsAlive(damager)) {
      return;
    }
    if (!UtilTime.elapsed(this._bossDamageDelay, 400L)) {
      return;
    }
    this._bossDamageDelay = System.currentTimeMillis();
    
    event.GetDamageeEntity().playEffect(EntityEffect.HURT);
    
    this._stateHealth -= 1;
    

    this._boss.GetEntity().getWorld().playSound(this._boss.GetEntity().getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 2.0F);
    

    if (this._stateHealth <= 0)
    {
      NextState();
      
      this._boss.GetEntity().getWorld().playSound(this._boss.GetEntity().getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 0.5F);
    }
  }
  
  public PumpkinKing GetBoss()
  {
    return this._boss;
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DamageCancel(CustomDamageEvent event)
  {
    if (!(event.GetDamageeEntity() instanceof Snowman)) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.LAVA) {
      return;
    }
    event.SetCancelled("Snowman Damage");
  }
}
