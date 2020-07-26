package nautilus.game.arcade.game.games.stacker;

import java.util.ArrayList;
import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.stacker.kits.KitDefault;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Stacker extends SoloGame implements IThrown
{
  private HashSet<Entity> _tempStackShift = new HashSet();
  












  public Stacker(ArcadeManager manager)
  {
    super(manager, GameType.Stacker, new Kit[] {new KitDefault(manager) }, new String[] {"Right-Click animals to stack them.", "Left-Click to throw an animal from your stack.", "Players lose 5 stacked animals if they get hit.", "First to stack 16 high wins!" });
  }
  

  @EventHandler
  public void SpawnFood(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    Location loc = (Location)((GameTeam)GetTeamList().get(0)).GetSpawns().get(UtilMath.r(((GameTeam)GetTeamList().get(0)).GetSpawns().size()));
    

    this.CreatureAllowOverride = true;
    Pig pig = (Pig)loc.getWorld().spawn(loc, Pig.class);
    this.CreatureAllowOverride = false;
  }
  
  @EventHandler
  public void GrabEntity(PlayerInteractEntityEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player stacker = event.getPlayer();
    
    Entity stackee = event.getRightClicked();
    if (stackee == null) {
      return;
    }
    if (!(stackee instanceof LivingEntity)) {
      return;
    }
    if ((stackee instanceof org.bukkit.entity.Horse)) {
      return;
    }
    if ((stackee instanceof Player)) {
      return;
    }
    while (stackee.getVehicle() != null) {
      stackee = stackee.getVehicle();
    }
    if (stackee.equals(stacker)) {
      return;
    }
    Entity top = stacker;
    while (top.getPassenger() != null) {
      top = top.getPassenger();
    }
    if (!Recharge.Instance.use(stacker, "Stacker", 250L, false, false)) {
      return;
    }
    top.setPassenger(stackee);
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void ThrowEntity(PlayerInteractEvent event)
  {
    if (!mineplex.core.common.util.UtilEvent.isAction(event, UtilEvent.ActionType.L)) {
      return;
    }
    Player thrower = event.getPlayer();
    
    if (thrower.getVehicle() != null) {
      return;
    }
    Entity throwee = thrower.getPassenger();
    if (throwee == null) {
      return;
    }
    thrower.eject();
    
    Entity throweeStack = throwee.getPassenger();
    if (throweeStack != null)
    {
      throwee.eject();
      throweeStack.leaveVehicle();
      
      final Entity fThrower = thrower;
      final Entity fThroweeStack = throweeStack;
      
      this._tempStackShift.add(throweeStack);
      
      this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          fThrower.setPassenger(fThroweeStack);
          Stacker.this._tempStackShift.remove(fThroweeStack);
        }
      }, 2L);
    }
    
    UtilAction.velocity(throwee, thrower.getLocation().getDirection(), 1.8D, false, 0.0D, 0.3D, 2.0D, false);
    
    this.Manager.GetProjectile().AddThrow(throwee, thrower, this, -1L, true, false, true, false, 2.4D);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    Entity hit = target;
    

    while (hit.getVehicle() != null) {
      hit = target.getVehicle();
    }
    
    if (hit.equals(data.GetThrower()))
    {
      this.Manager.GetProjectile().AddThrow(data.GetThrown(), data.GetThrower(), this, -1L, true, false, true, false, 2.4D);
      return;
    }
    

    UtilAction.velocity(hit, UtilAlg.getTrajectory2d(data.GetThrown(), target), 1.0D, true, 0.8D, 0.0D, 10.0D, true);
    

    Entity top = target;
    while (top.getPassenger() != null) {
      top = top.getPassenger();
    }
    Entity rider = target.getPassenger();
    while (rider != null)
    {
      rider.leaveVehicle();
      rider.setVelocity(new Vector(0.5D - Math.random(), Math.random() / 2.0D, 5.0D - Math.random()));
      rider = rider.getPassenger();
    }
    
    UtilPlayer.message(target, F.main("Game", F.name(UtilEnt.getName(data.GetThrower())) + " hit you with " + F.name(UtilEnt.getName(data.GetThrown()))));
    

    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), org.bukkit.Sound.HURT_FLESH, 1.0F, 1.0F);
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
