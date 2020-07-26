package nautilus.game.arcade.game.games.gravity.objects;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle.ParticleType;
import nautilus.game.arcade.game.games.gravity.Gravity;
import nautilus.game.arcade.game.games.gravity.GravityObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

public class GravityBomb extends GravityObject
{
  public Player Owner;
  private long _blockHitDelay;
  
  public GravityBomb(Gravity host, Entity ent, double mass, Vector vel, Player owner)
  {
    super(host, ent, mass, 1.5D, vel);
    
    this.Owner = owner;
    
    this._blockHitDelay = System.currentTimeMillis();
  }
  

  public void PlayCollideSound(double power)
  {
    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
  }
  
  public boolean CollideCheck(GravityObject other)
  {
    if (equals(other)) {
      return false;
    }
    if (System.currentTimeMillis() < this.CollideDelay) {
      return false;
    }
    if (System.currentTimeMillis() < other.CollideDelay) {
      return false;
    }
    if ((this.Vel.length() == 0.0D) && (other.Vel.length() == 0.0D)) {
      return false;
    }
    double size = this.Size;
    if (other.Size > size) {
      size = other.Size;
    }
    if (UtilMath.offset(this.Base, other.Base) > size) {
      return false;
    }
    return true;
  }
  
  public HashSet<GravityDebris> BombDetonate()
  {
    boolean collided = false;
    

    for (GravityObject obj : this.Host.GetObjects())
    {
      if (CollideCheck(obj))
      {

        collided = true;
      }
    }
    
    if ((!collided) && (mineplex.core.common.util.UtilTime.elapsed(this._blockHitDelay, 100L))) {
      for (Block block : UtilBlock.getInRadius(this.Base.getLocation().add(0.0D, 0.5D, 0.0D), 2.0D).keySet())
      {
        if (!UtilBlock.airFoliage(block))
        {


          if ((block.getLocation().getX() + 0.5D >= this.Base.getLocation().getX()) || 
            (this.Vel.getX() <= 0.0D))
          {

            if ((block.getLocation().getX() + 0.5D <= this.Base.getLocation().getX()) || 
              (this.Vel.getX() >= 0.0D))
            {


              if ((block.getLocation().getY() + 0.5D >= this.Base.getLocation().getY()) || 
                (this.Vel.getY() <= 0.0D))
              {

                if ((block.getLocation().getY() + 0.5D <= this.Base.getLocation().getY()) || 
                  (this.Vel.getY() >= 0.0D))
                {


                  if ((block.getLocation().getZ() + 0.5D >= this.Base.getLocation().getZ()) || 
                    (this.Vel.getZ() <= 0.0D))
                  {

                    if ((block.getLocation().getZ() + 0.5D <= this.Base.getLocation().getZ()) || 
                      (this.Vel.getZ() >= 0.0D))
                    {

                      collided = true;
                      break;
                    } } } } } } } }
    }
    if (!collided) {
      return null;
    }
    
    for (GravityObject obj : this.Host.GetObjects())
    {
      if (UtilMath.offset(this.Base, obj.Base) <= 3.0D)
      {

        if (!equals(obj))
        {

          if (System.currentTimeMillis() >= obj.CollideDelay)
          {

            if ((this.Vel.length() != 0.0D) || (obj.Vel.length() != 0.0D))
            {

              obj.AddVelocity(UtilAlg.getTrajectory(this.Base, obj.Base).multiply(0.4D), 10.0D); } }
        }
      }
    }
    HashSet<GravityDebris> debris = new HashSet();
    
    for (Block block : UtilBlock.getInRadius(this.Base.getLocation().add(0.0D, 0.5D, 0.0D), 3.0D).keySet())
    {
      if (!UtilBlock.airFoliage(block))
      {

        if (block.getType() != Material.EMERALD_BLOCK)
        {


          Vector velocity = UtilAlg.getTrajectory(this.Ent.getLocation(), block.getLocation().add(0.5D, 0.5D, 0.5D));
          velocity.add(this.Vel.clone().normalize());
          velocity.add(new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D).multiply(0.5D));
          velocity.multiply(0.3D);
          

          Material type = block.getType();
          byte data = block.getData();
          block.setType(Material.AIR);
          

          FallingBlock projectile = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5D, 0.6D, 0.5D), type, data);
          GravityDebris newDebris = new GravityDebris(this.Host, projectile, 12.0D, velocity);
          

          debris.add(newDebris);
        } }
    }
    return debris;
  }
  


  public void CustomCollide(GravityObject other)
  {
    mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.HUGE_EXPLOSION, this.Ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.EXPLODE, 0.3F, 1.0F);
    this.Ent.remove();
  }
  

  public boolean CanCollide(GravityObject other)
  {
    return false;
  }
}
