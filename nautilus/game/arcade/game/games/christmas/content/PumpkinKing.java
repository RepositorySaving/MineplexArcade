package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.parts.Part5;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class PumpkinKing
{
  private Part5 Host;
  private Skeleton _ent;
  private Location _target;
  private ArrayList<Location> _grid;
  private ArrayList<TNTPrimed> _tnt = new ArrayList();
  
  private long _lastTNT = 0L;
  
  public PumpkinKing(Part5 host, Location loc, ArrayList<Location> grid)
  {
    this.Host = host;
    
    this._grid = grid;
    
    this.Host.Host.CreatureAllowOverride = true;
    this._ent = ((Skeleton)loc.getWorld().spawn(loc, Skeleton.class));
    this.Host.Host.CreatureAllowOverride = false;
    UtilEnt.Vegetate(this._ent);
    UtilEnt.ghost(this._ent, true, false);
    
    this._ent.setSkeletonType(Skeleton.SkeletonType.WITHER);
    this._ent.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
    this._ent.getEquipment().setItemInHand(new ItemStack(Material.TNT));
    
    this._ent.setCustomName("The Pumpkin King");
    this._ent.setCustomNameVisible(true);
    
    this._ent.getWorld().strikeLightningEffect(this._ent.getLocation());
  }
  
  public boolean IsDead()
  {
    return (this._ent != null) && (!this._ent.isValid());
  }
  
  public void SetTarget(Location loc)
  {
    this._target = loc;
  }
  
  public Location GetTarget()
  {
    return this._target;
  }
  
  public Entity GetEntity()
  {
    return this._ent;
  }
  
  public void MoveUpdate()
  {
    if (IsDead()) {
      return;
    }
    if ((this._target == null) || (UtilMath.offset(this._ent.getLocation(), this._target) < 1.0D)) {
      SetTarget(((Location)UtilAlg.Random(this._grid)).clone().add(0.0D, 0.5D, 0.0D));
    }
    else {
      UtilEnt.CreatureMoveFast(this._ent, this._target, (float)(1.2D + 0.06D * this.Host.GetState()));
    }
  }
  
  public void TNTUpdate() {
    if (IsDead()) {
      return;
    }
    if (!UtilTime.elapsed(this.Host.GetStateTime(), 4000L)) {
      return;
    }
    if (!UtilTime.elapsed(this._lastTNT, 6000 - 200 * this.Host.GetState())) {
      return;
    }
    this._lastTNT = System.currentTimeMillis();
    
    Player player = (Player)UtilAlg.Random(this.Host.Host.GetPlayers(true));
    
    TNTPrimed tnt = (TNTPrimed)this._ent.getWorld().spawn(this._ent.getEyeLocation(), TNTPrimed.class);
    
    UtilAction.velocity(tnt, UtilAlg.getTrajectory(tnt, player), 1.0D, false, 0.0D, 0.2D, 10.0D, false);
    
    double mult = 0.5D + 0.6D * (UtilMath.offset(tnt, player) / 24.0D);
    

    tnt.setVelocity(player.getLocation().toVector().subtract(tnt.getLocation().toVector()).normalize().add(new Vector(0.0D, 0.4D, 0.0D)).multiply(mult));
    
    this._tnt.add(tnt);
  }
  
  public void StayIdle()
  {
    if (IsDead()) {
      return;
    }
    UtilEnt.CreatureMoveFast(this._ent, this._ent.getLocation().getBlock().getLocation().add(0.5D, 0.0D, 0.5D), 0.6F);
  }
  
  public void Die()
  {
    UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, this._ent.getLocation(), 0.0F, 1.0F, 0.0F, 0.0F, 1);
    UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, this._ent.getLocation(), 0.25F, 1.0F, 0.25F, 0.0F, 50);
    
    this._ent.getWorld().playSound(this._ent.getLocation(), Sound.ENDERDRAGON_DEATH, 4.0F, 0.5F);
    
    this.Host.Host.BossSay("Pumpkin King", "NOOOOOOOOOOOOOO!!!!!!");
    
    this._ent.remove();
  }
}
