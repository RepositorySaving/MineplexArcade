package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.ReboundData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class PerkArrowRebound extends Perk
{
  private HashMap<org.bukkit.entity.Entity, ReboundData> _arrows = new HashMap();
  
  private int _max = 0;
  private float _maxPower = 1.0F;
  



  public PerkArrowRebound(int max, float maxPower)
  {
    super("Chain Arrows", new String[] {C.cGray + "On hit, arrows bounce to nearby enemies.", C.cGray + "Arrows bounce up to " + max + " times." });
    

    this._max = max;
    this._maxPower = maxPower;
  }
  
  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    this._arrows.put(event.getProjectile(), new ReboundData(player, this._max, null));
  }
  
  @EventHandler
  public void Rebound(ProjectileHitEvent event)
  {
    ReboundData data = (ReboundData)this._arrows.remove(event.getEntity());
    if (data == null) { return;
    }
    if (data.Bounces <= 0) {
      return;
    }
    Location arrowLoc = event.getEntity().getLocation().add(event.getEntity().getVelocity());
    
    Player hit = UtilPlayer.getClosest(arrowLoc, data.Ignore);
    if (hit == null) { return;
    }
    if ((UtilMath.offset(hit.getLocation(), arrowLoc) > 1.0D) && 
      (UtilMath.offset(hit.getEyeLocation(), arrowLoc) > 1.0D)) {
      return;
    }
    data.Ignore.add(hit);
    
    Player target = UtilPlayer.getClosest(event.getEntity().getLocation().add(event.getEntity().getVelocity()), data.Ignore);
    if (target == null) { return;
    }
    Vector trajectory = UtilAlg.getTrajectory(hit, target);
    trajectory.add(new Vector(0.0D, UtilMath.offset(hit, target) / 100.0D, 0.0D));
    
    float power = (float)(0.8D + UtilMath.offset(hit, target) / 30.0D);
    if ((this._maxPower > 0.0F) && (power > this._maxPower)) {
      power = this._maxPower;
    }
    Arrow ent = hit.getWorld().spawnArrow(hit.getEyeLocation().add(UtilAlg.getTrajectory(hit, target)), trajectory, power, 0.0F);
    ent.setShooter(data.Shooter);
    
    this._arrows.put(ent, new ReboundData(data.Shooter, data.Bounces - 1, data.Ignore));
  }
}
