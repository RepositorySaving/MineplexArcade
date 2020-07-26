package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class PerkThrower extends Perk implements IThrown
{
  public PerkThrower(ArcadeManager manager)
  {
    super("Thrower", new String[] {C.cGray + "You can pick up team mates!", C.cYellow + "Right-Click" + C.cGray + " with Sword to " + C.cGreen + "Throw Sheep" });
  }
  

  @EventHandler
  public void Throw(PlayerInteractEvent event)
  {
    if (!mineplex.core.common.util.UtilEvent.isAction(event, UtilEvent.ActionType.R)) {
      return;
    }
    Player thrower = event.getPlayer();
    
    if (!UtilGear.isMat(thrower.getItemInHand(), Material.IRON_SWORD)) {
      return;
    }
    if (thrower.getPassenger() == null) {
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
      
      this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          fThrower.setPassenger(fThroweeStack);
        }
      }, 2L);
    }
    

    UtilAction.velocity(throwee, thrower.getLocation().getDirection(), 1.4D, false, 0.0D, 0.3D, 0.8D, true);
    this.Manager.GetProjectile().AddThrow(throwee, thrower, this, -1L, true, false, true, false, 2.0D);
    

    thrower.getWorld().playSound(thrower.getLocation(), Sound.SHEEP_IDLE, 2.0F, 3.0F);
    

    Recharge.Instance.useForce(thrower, "Sheep Stack", 500L);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    if ((target instanceof Player))
    {
      if (!this.Manager.GetGame().IsAlive((Player)target))
      {
        return;
      }
    }
    

    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.CUSTOM, 6.0D, false, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
    

    Vector dir = UtilAlg.getTrajectory(data.GetThrown(), target);
    if (dir.getY() < 0.0D) dir.setY(0);
    UtilAction.velocity(target, dir, 1.2D, false, 0.0D, 0.4D, 1.0D, true);
    
    dir = UtilAlg.getTrajectory(target, data.GetThrown());
    if (dir.getY() < 0.0D) dir.setY(0);
    UtilAction.velocity(data.GetThrown(), dir, 1.2D, false, 0.0D, 0.4D, 1.0D, true);
    

    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.SHEEP_IDLE, 3.0F, 5.0F);
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
