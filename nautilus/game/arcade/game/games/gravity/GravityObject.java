package nautilus.game.arcade.game.games.gravity;

import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.disguise.disguises.DisguiseBat;
import nautilus.game.arcade.game.games.gravity.objects.GravityBomb;
import nautilus.game.arcade.game.games.gravity.objects.GravityHook;
import nautilus.game.arcade.game.games.gravity.objects.GravityPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

public abstract class GravityObject
{
  public Gravity Host;
  public Entity Ent;
  public double Mass;
  public double Size;
  public Vector Vel;
  public Zombie Base;
  public DisguiseBat Bat;
  public long GrabDelay = 0L;
  
  public long CollideDelay;
  
  public GravityObject(Gravity host, Entity ent, double mass, double size, Vector vel)
  {
    this.Host = host;
    
    this.Ent = ent;
    this.Mass = mass;
    this.Size = size;
    
    this.CollideDelay = (System.currentTimeMillis() + 100L);
    
    if (vel != null) {
      this.Vel = vel;
    } else {
      this.Vel = new Vector(0, 0, 0);
    }
    this.Host.CreatureAllowOverride = true;
    this.Base = ((Zombie)ent.getWorld().spawn(ent.getLocation().subtract(0.0D, 0.0D, 0.0D), Zombie.class));
    this.Host.CreatureAllowOverride = false;
    
    this.Base.setMaxHealth(60.0D);
    this.Base.setHealth(60.0D);
    
    this.Bat = new DisguiseBat(this.Base);
    this.Bat.setSitting(true);
    

    UtilEnt.Vegetate(this.Base, true);
    UtilEnt.ghost(this.Base, true, true);
  }
  
  public boolean IsPlayer()
  {
    return this.Ent instanceof Player;
  }
  
  public boolean Update()
  {
    if (!this.Ent.isValid()) {
      return false;
    }
    if (!this.Base.isValid()) {
      return false;
    }
    if ((IsPlayer()) && 
      (!this.Host.IsAlive((Player)this.Ent))) {
      return false;
    }
    if (this.Ent.getVehicle() == null) {
      this.Base.setPassenger(this.Ent);
    }
    this.Base.setVelocity(this.Vel);
    

    if (this.Vel.length() > 0.0D)
    {
      if ((this instanceof GravityPlayer))
      {
        UtilParticle.PlayParticle(UtilParticle.ParticleType.FIREWORKS_SPARK, this.Ent.getLocation().subtract(0.0D, 0.5D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      }
      else if ((this instanceof GravityBomb))
      {
        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, this.Ent.getLocation().add(0.0D, -0.1D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      }
      else if ((this instanceof GravityHook))
      {
        UtilParticle.PlayParticle(UtilParticle.ParticleType.SNOW_SHOVEL, this.Ent.getLocation().add(0.0D, 0.1D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      }
    }
    
    return true;
  }
  
  public void Collide(GravityObject other)
  {
    if (equals(other)) {
      return;
    }
    if (System.currentTimeMillis() < this.CollideDelay) {
      return;
    }
    if (System.currentTimeMillis() < other.CollideDelay) {
      return;
    }
    if ((this.Vel.length() == 0.0D) && (other.Vel.length() == 0.0D)) {
      return;
    }
    if ((!CanCollide(other)) || (!other.CanCollide(this))) {
      return;
    }
    double size = this.Size;
    if (other.Size > size) {
      size = other.Size;
    }
    if (UtilMath.offset(this.Base, other.Base) > size) {
      return;
    }
    Vector v1 = this.Vel;
    Vector v2 = other.Vel;
    

    double totalMass = this.Mass + other.Mass;
    
    this.Vel = v1.clone().multiply((this.Mass - other.Mass) / totalMass).add(v2.clone().multiply(2.0D * other.Mass / totalMass));
    
    other.Vel = v1.clone().multiply(2.0D * this.Mass / totalMass).subtract(v2.clone().multiply((this.Mass - other.Mass) / totalMass));
    

    double power = v1.clone().multiply(this.Mass).subtract(v2.clone().multiply(other.Mass)).length();
    

    PlayCollideSound(power);
    other.PlayCollideSound(power);
    

    this.CollideDelay = (System.currentTimeMillis() + 1000L);
    other.CollideDelay = (System.currentTimeMillis() + 1000L);
    
    this.GrabDelay = System.currentTimeMillis();
    other.GrabDelay = System.currentTimeMillis();
    

    CustomCollide(other);
    other.CustomCollide(this);
    

    SetMovingBat(true);
    other.SetMovingBat(true);
  }
  
  public boolean CanCollide(GravityObject other)
  {
    return true;
  }
  


  public void CustomCollide(GravityObject other) {}
  

  public void PlayCollideSound(double power)
  {
    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.ZOMBIE_WOOD, 1.0F, 1.0F);
  }
  
  public void AddVelocity(Vector vel)
  {
    AddVelocity(vel, 50.0D);
  }
  
  public void AddVelocity(Vector vel, double limit)
  {
    double preLength = this.Vel.length();
    
    this.Vel.add(vel);
    

    if ((this.Vel.length() > limit) && (this.Vel.length() > preLength))
    {
      this.Vel.normalize().multiply(preLength);
    }
    

    if (this.Vel.length() > 3.0D)
    {
      this.Vel.normalize().multiply(3);
    }
    
    SetMovingBat(true);
  }
  
  public void Clean()
  {
    this.Ent.leaveVehicle();
    
    if (!(this.Ent instanceof Player)) {
      this.Ent.remove();
    }
    this.Base.remove();
  }
  
  public void SetMovingBat(boolean moving)
  {
    this.Bat.setSitting(!moving);
  }
}
