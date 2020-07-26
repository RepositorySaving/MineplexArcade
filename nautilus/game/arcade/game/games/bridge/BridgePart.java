package nautilus.game.arcade.game.games.bridge;

import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class BridgePart
{
  public FallingBlock Entity;
  public Location Target;
  public Location Initial;
  public boolean Velocity;
  
  public BridgePart(FallingBlock entity, Location target, boolean velocity)
  {
    this.Entity = entity;
    this.Target = target;
    this.Initial = entity.getLocation();
    this.Velocity = velocity;
  }
  
  public boolean Update()
  {
    if (!this.Entity.isValid())
    {
      MapUtil.QuickChangeBlockAt(this.Target, this.Entity.getBlockId(), this.Entity.getBlockData());
      return true;
    }
    

    if ((UtilMath.offset(this.Entity.getLocation(), this.Target) < 1.0D) || (this.Entity.getTicksLived() > 600) || (this.Entity.getLocation().getY() < this.Target.getY()))
    {
      MapUtil.QuickChangeBlockAt(this.Target, this.Entity.getBlockId(), this.Entity.getBlockData());
      
      this.Entity.remove();
      
      this.Target.getBlock().getWorld().playEffect(this.Target, Effect.STEP_SOUND, this.Target.getBlock().getTypeId());
      
      return true;
    }
    
    if (!this.Velocity) {
      return false;
    }
    
    if (UtilMath.offset2d(this.Entity.getLocation(), this.Target) < 0.1D)
    {
      Location loc = this.Entity.getLocation();
      loc.setX(this.Target.getX());
      loc.setZ(this.Target.getZ());
    }
    


    Vector dir = UtilAlg.getTrajectory(this.Entity.getLocation(), this.Target);
    dir.add(new Vector(0.0D, 0.6D, 0.0D));
    dir.normalize();
    dir.multiply(0.8D);
    
    if (UtilMath.offset(this.Entity.getLocation(), this.Initial) < UtilMath.offset(this.Entity.getLocation(), this.Target)) {
      dir.add(new Vector(0.0D, 0.6D, 0.0D));
    }
    this.Entity.setVelocity(dir);
    
    return false;
  }
  
  public boolean ItemSpawn(Item item)
  {
    if (UtilMath.offset(this.Entity, item) < 1.0D)
    {
      return true;
    }
    
    return false;
  }
}
