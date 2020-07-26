package nautilus.game.arcade.game.games.gravity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerPrepareTeleportEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.gravity.objects.GravityBomb;
import nautilus.game.arcade.game.games.gravity.objects.GravityDebris;
import nautilus.game.arcade.game.games.gravity.objects.GravityHook;
import nautilus.game.arcade.game.games.gravity.objects.GravityPlayer;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftFallingSand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Gravity extends SoloGame
{
  private ArrayList<GravityObject> _objects = new ArrayList();
  

  private HashMap<Player, GravityHook> _hooks = new HashMap();
  
  private HashMap<Arrow, Vector> _arrows = new HashMap();
  
  private ArrayList<Location> _powerups = new ArrayList();
  private Location _powerup = null;
  private long _lastPowerup = 0L;
  













  public Gravity(ArcadeManager manager)
  {
    super(manager, GameType.Gravity, new nautilus.game.arcade.kit.Kit[] {new nautilus.game.arcade.game.games.gravity.kits.KitJetpack(manager) }, new String[] {C.cGreen + "Push Drop" + C.cGray + " to boost off blocks", C.cGreen + "Left-Click" + C.cGray + " to use " + F.skill("Sonic Blast"), C.cGreen + "Right-Click" + C.cGray + " to use " + F.skill("Jetpack"), "Food is Oxygen. Restore it at Emerald Blocks.", "Last player alive wins!" });
    

    this._help = 
      new String[] {
      "Push DROP to launch yourself off platforms.", 
      "You automatically grab onto nearby platforms.", 
      "Hold Block to use your Jetpack", 
      "Your Experience Bar is your Jetpack Fuel", 
      "Restore Jetpack Fuel by collecting Powerups", 
      "Powerups are flashing green fireworks", 
      "Your Hunger is your Oxygen Level", 
      "Restore Oxygen at the Emerald Blocks" };
    

    this.DamagePvP = false;
    this.HungerSet = 10;
    
    this.WorldTimeSet = 18000;
    
    this.CompassEnabled = true;
  }
  

  public void ParseData()
  {
    this._powerups = this.WorldData.GetDataLocs("LIME");
  }
  
  @EventHandler
  public void CreatePlayerObjects(PlayerPrepareTeleportEvent event)
  {
    Player player = event.GetPlayer();
    
    GravityPlayer obj = new GravityPlayer(this, player, 60.0D, null);
    this._objects.add(obj);
    
    player.setExp(0.9999F);
  }
  
  @EventHandler
  public void AnnounceBoost(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    Announce(C.Bold + C.cPurple + "Press " + C.Bold + C.cWhite + "DROP WEAPON" + C.Bold + C.cPurple + " to boost yourself off platforms!");
  }
  
  @EventHandler
  public void ClearObjects(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.End) {
      return;
    }
    for (GravityObject obj : this._objects) {
      obj.Clean();
    }
    this._objects.clear();
  }
  
  @EventHandler
  public void KickOff(PlayerDropItemEvent event)
  {
    for (GravityObject object : this._objects) {
      if ((object instanceof GravityPlayer))
        ((GravityPlayer)object).KickOff(event.getPlayer());
    }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void Jetpack(UpdateEvent event)
  {
    if (!InProgress()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (GravityObject object : this._objects) {
      if ((object instanceof GravityPlayer))
        ((GravityPlayer)object).Jetpack();
    }
  }
  
  @EventHandler
  public void Shoot(PlayerInteractEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("IRON_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!Recharge.Instance.use(player, "Sonic Blast", 1200L, true, false)) {
      return;
    }
    
    Vector vel = player.getLocation().getDirection().multiply(1.2D);
    
    Arrow arrow = player.getWorld().spawnArrow(
      player.getEyeLocation().add(player.getLocation().getDirection().multiply(2.5D)).subtract(new Vector(0.0D, 0.8D, 0.0D)), 
      player.getLocation().getDirection(), (float)vel.length(), 0.0F);
    arrow.setShooter(player);
    
    UtilEnt.ghost(arrow, true, true);
    
    this._arrows.put(arrow, vel);
    
    event.setCancelled(true);
  }
  











  @EventHandler
  public void HookFire(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.DIAMOND_SWORD) {
      return;
    }
    Player player = event.getPlayer();
    
    GravityObject old = (GravityObject)this._hooks.remove(player);
    if (old != null)
    {
      old.Clean();
      this._objects.remove(old);
      

      UtilPlayer.message(player, F.main("Skill", "You detatched from your " + F.skill("Grappling Hook") + "."));
      

      player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.75F, 2.0F);
      
      return;
    }
    
    if (!Recharge.Instance.use(player, "Hookshot", 12000L, true, false)) {
      return;
    }
    
    Vector velocity = player.getLocation().getDirection().multiply(0.4D);
    
    this.CreatureAllowOverride = true;
    Slime slime = (Slime)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)), Slime.class);
    this.CreatureAllowOverride = false;
    
    slime.setSize(1);
    UtilEnt.Vegetate(slime, true);
    UtilEnt.ghost(slime, true, false);
    
    GravityHook hook = new GravityHook(this, slime, 4.0D, velocity);
    
    UtilEnt.Leash(hook.Base, player, false, false);
    
    this._hooks.put(player, hook);
    
    this._objects.add(hook);
    

    UtilPlayer.message(player, F.main("Skill", "You launched a " + F.skill("Grappling Hook") + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.75F, 1.5F);
  }
  
  @EventHandler
  public void HookUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> hookIterator = this._hooks.keySet().iterator();
    
    while (hookIterator.hasNext())
    {
      Player player = (Player)hookIterator.next();
      GravityObject obj = (GravityObject)this._hooks.get(player);
      
      if ((!player.isValid()) || (!obj.Ent.isValid()))
      {
        obj.Clean();
        hookIterator.remove();

      }
      else
      {
        if ((obj.Ent instanceof FallingBlock)) {
          ((CraftFallingSand)obj.Ent).getHandle().b = 1;
        }
        HashMap<Block, Double> blocks = UtilBlock.getInRadius(obj.Ent.getLocation(), 1.2D);
        
        double bestDist = 0.0D;
        Block bestBlock = null;
        
        for (Block block : blocks.keySet())
        {
          if (!UtilBlock.airFoliage(block))
          {

            double dist = ((Double)blocks.get(block)).doubleValue();
            
            if ((bestBlock == null) || (dist > bestDist))
            {
              bestBlock = block;
              bestDist = dist;
            }
          }
        }
        if (bestBlock == null)
        {

          if (UtilMath.offset(player, obj.Ent) > 16.0D)
          {
            obj.Clean();
            hookIterator.remove();
            

            UtilPlayer.message(player, F.main("Skill", "Your " + F.skill("Grappling Hook") + " missed."));
          }
          

        }
        else
        {

          if ((player.isBlocking()) && (player.getItemInHand().getType() == Material.DIAMOND_SWORD))
          {
            for (GravityObject object : this._objects) {
              if ((object instanceof GravityPlayer))
              {
                GravityPlayer pObj = (GravityPlayer)object;
                
                if (pObj.Ent.equals(player))
                {
                  pObj.AddVelocity(UtilAlg.getTrajectory(player, obj.Ent).multiply(0.03D), 0.6D);
                  

                  player.getWorld().playSound(player.getLocation(), Sound.NOTE_STICKS, 0.3F, 2.0F);
                }
              }
            }
          } else if (UtilMath.offset(player, obj.Ent) > 16.0D)
          {
            double power = (UtilMath.offset(player, obj.Ent) - 16.0D) / 200.0D;
            
            for (GravityObject object : this._objects) {
              if ((object instanceof GravityPlayer))
              {
                GravityPlayer pObj = (GravityPlayer)object;
                
                if (pObj.Ent.equals(player))
                {
                  pObj.AddVelocity(UtilAlg.getTrajectory(player, obj.Ent).multiply(power), 0.6D);
                  

                  player.getWorld().playSound(player.getLocation(), Sound.NOTE_STICKS, 0.3F, 0.6F - (float)power);
                }
              }
            }
          }
          this._objects.remove(obj);
          obj.Base.setVelocity(new Vector(0, 0, 0));
        }
      }
    }
  }
  
  @EventHandler
  public void ObjectUpdate(UpdateEvent event) { if (event.getType() != UpdateType.TICK) {
      return;
    }
    if ((!InProgress()) && (GetState() != Game.GameState.End)) {
      return;
    }
    
    Iterator<GravityObject> objectIterator = this._objects.iterator();
    
    while (objectIterator.hasNext())
    {
      GravityObject obj = (GravityObject)objectIterator.next();
      
      obj.Update();
      
      if (!obj.Update())
      {
        obj.Clean();
        objectIterator.remove();
      }
    }
    

    for (GravityObject object : this._objects) {
      if ((object instanceof GravityPlayer))
        ((GravityPlayer)object).AutoGrab();
    }
    Iterator localIterator2;
    for (??? = this._objects.iterator(); ???.hasNext(); 
        localIterator2.hasNext())
    {
      GravityObject a = (GravityObject)???.next();
      localIterator2 = this._objects.iterator(); continue;GravityObject b = (GravityObject)localIterator2.next();
      a.Collide(b);
    }
    
    HashSet<GravityDebris> newDebris = new HashSet();
    objectIterator = this._objects.iterator();
    while (objectIterator.hasNext())
    {
      GravityObject obj = (GravityObject)objectIterator.next();
      
      if ((obj instanceof GravityBomb))
      {

        HashSet<GravityDebris> debris = ((GravityBomb)obj).BombDetonate();
        
        if ((debris != null) && (!debris.isEmpty()))
        {
          newDebris.addAll(debris);
          objectIterator.remove();
          obj.CustomCollide(null);
          obj.Clean();
        }
      } }
    this._objects.addAll(newDebris);
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if ((event.GetCause() != EntityDamageEvent.DamageCause.CUSTOM) && (event.GetCause() != EntityDamageEvent.DamageCause.VOID)) {
      event.SetCancelled("No Damage");
    }
  }
  
  @EventHandler
  public void FallingBlockLand(EntityChangeBlockEvent event) {
    if ((event.getEntity() instanceof FallingBlock))
    {
      event.setCancelled(true);
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void OxygenSuffocate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    for (GravityObject object : this._objects) {
      if ((object instanceof GravityPlayer))
        ((GravityPlayer)object).Oxygen();
    }
  }
  
  public ArrayList<GravityObject> GetObjects() {
    return this._objects;
  }
  
  @EventHandler
  public void BowShoot(EntityShootBowEvent event)
  {
    Player shooter = (Player)event.getEntity();
    
    Vector vel = event.getProjectile().getVelocity();
    vel.multiply(0.6D);
    
    Arrow arrow = shooter.getWorld().spawnArrow(
      shooter.getEyeLocation().add(shooter.getLocation().getDirection().multiply(1.5D)).subtract(new Vector(0.0D, 0.8D, 0.0D)), 
      shooter.getLocation().getDirection(), (float)vel.length(), 0.0F);
    arrow.setShooter(shooter);
    
    UtilEnt.ghost(arrow, true, true);
    
    this._arrows.put(arrow, vel);
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void BowUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Arrow> arrowIterator = this._arrows.keySet().iterator();
    
    while (arrowIterator.hasNext())
    {
      Arrow arrow = (Arrow)arrowIterator.next();
      
      for (GravityObject obj : this._objects)
      {
        if (UtilMath.offset(obj.Base.getLocation().add(0.0D, 0.5D, 0.0D), arrow.getLocation()) <= obj.Size)
        {

          if ((!(obj instanceof GravityPlayer)) || 
          
            (!obj.Ent.equals(arrow.getShooter())))
          {


            BowExplode(arrow);
            break;
          } }
      }
      if ((!arrow.isValid()) || (arrow.getTicksLived() > 200))
      {
        arrow.remove();
        arrowIterator.remove();
      }
      else
      {
        arrow.setVelocity((Vector)this._arrows.get(arrow));
        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
        UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
        arrow.getWorld().playSound(arrow.getLocation(), Sound.FIZZ, 0.3F, 0.5F);
      }
    }
  }
  
  @EventHandler
  public void BowHit(ProjectileHitEvent event)
  {
    BowExplode(event.getEntity());
  }
  

  public void BowExplode(Projectile proj)
  {
    for (GravityObject obj : this._objects)
    {
      if (UtilMath.offset(proj, obj.Base) <= 3.0D)
      {

        obj.AddVelocity(UtilAlg.getTrajectory(proj, obj.Base).multiply(0.4D), 10.0D);
        


        if ((obj.Ent instanceof Player))
        {

          this.Manager.GetDamage().NewDamageEvent((Player)obj.Ent, proj.getShooter(), null, 
            EntityDamageEvent.DamageCause.CUSTOM, 1.0D, false, true, true, 
            UtilEnt.getName(proj.getShooter()), "Sonic Blast");
        }
        

        obj.GrabDelay = System.currentTimeMillis();
        obj.SetMovingBat(true);
      }
    }
    
    for (Block block : UtilBlock.getInRadius(proj.getLocation().add(0.0D, 0.5D, 0.0D), 3.0D).keySet())
    {
      if (!UtilBlock.airFoliage(block))
      {

        if ((block.getType() != Material.EMERALD_BLOCK) && (block.getType() != Material.GOLD_BLOCK))
        {


          Vector velocity = UtilAlg.getTrajectory(proj.getLocation(), block.getLocation().add(0.5D, 0.5D, 0.5D));
          velocity.add(proj.getVelocity().normalize());
          velocity.add(new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D).multiply(0.5D));
          velocity.multiply(0.3D);
          

          Material type = block.getType();
          byte data = block.getData();
          block.setType(Material.AIR);
          

          FallingBlock projectile = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5D, 0.6D, 0.5D), type, data);
          GravityDebris newDebris = new GravityDebris(this, projectile, 12.0D, velocity);
          

          this._objects.add(newDebris);
        }
      }
    }
    UtilParticle.PlayParticle(UtilParticle.ParticleType.HUGE_EXPLOSION, proj.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    proj.getWorld().playSound(proj.getLocation(), Sound.EXPLODE, 0.6F, 1.5F);
    

    proj.remove();
  }
  
  @EventHandler
  public void PowerupUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!mineplex.core.common.util.UtilTime.elapsed(this._lastPowerup, 15000L)) {
      return;
    }
    if (this._powerup == null) {
      this._powerup = ((Location)UtilAlg.Random(this._powerups));
    }
    else
    {
      FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.GREEN).with(org.bukkit.FireworkEffect.Type.BALL).trail(false).build();
      mineplex.core.common.util.UtilFirework.playFirework(this._powerup, effect);
    }
  }
  
  @EventHandler
  public void PowerupCollect(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._powerup == null) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      if (UtilMath.offset(player.getLocation(), this._powerup) < 3.0D)
      {
        UtilPlayer.message(player, F.main("Game", "You collected " + F.skill("Jetpack Fuel") + "!"));
        player.setExp(Math.min(0.9999F, player.getExp() + 0.25F));
        
        player.getWorld().playSound(player.getLocation(), Sound.DRINK, 1.0F, 0.5F);
        
        this._powerup = null;
        this._lastPowerup = System.currentTimeMillis();
        
        break;
      }
    }
  }
}
