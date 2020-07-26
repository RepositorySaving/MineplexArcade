package nautilus.game.arcade.game.games.dragonriders;

import java.util.HashMap;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.explosion.Explosion;
import nautilus.game.arcade.ArcadeManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DragonData
{
  ArcadeManager Manager;
  public EnderDragon Dragon;
  public Player Rider;
  public Entity TargetEntity = null;
  
  public Location Location = null;
  
  public float Pitch = 0.0F;
  public Vector Velocity = new Vector(0, 0, 0);
  
  public DragonData(ArcadeManager manager, Player rider)
  {
    this.Manager = manager;
    
    this.Rider = rider;
    
    this.Velocity = rider.getLocation().getDirection().setY(0).normalize();
    this.Pitch = UtilAlg.GetPitch(rider.getLocation().getDirection());
    
    this.Location = rider.getLocation();
    

    manager.GetGame().CreatureAllowOverride = true;
    this.Dragon = ((EnderDragon)rider.getWorld().spawn(rider.getLocation(), EnderDragon.class));
    UtilEnt.Vegetate(this.Dragon);
    manager.GetGame().CreatureAllowOverride = false;
    
    rider.getWorld().playSound(rider.getLocation(), Sound.ENDERDRAGON_GROWL, 20.0F, 1.0F);
    
    this.Dragon.setPassenger(this.Rider);
  }
  
  public void Move()
  {
    ((CraftEnderDragon)this.Dragon).getHandle().setTargetBlock(GetTarget().getBlockX(), GetTarget().getBlockY(), GetTarget().getBlockZ());
    
    this.Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(this.Dragon.getLocation(), 10.0D).keySet(), this.Dragon.getLocation(), false);
  }
  
  public Location GetTarget()
  {
    return this.Rider.getLocation().add(this.Rider.getLocation().getDirection().multiply(40));
  }
}
