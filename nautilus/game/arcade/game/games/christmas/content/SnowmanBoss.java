package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import net.minecraft.server.v1_7_R3.ControllerMove;
import net.minecraft.server.v1_7_R3.EntityCreature;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class SnowmanBoss
{
  private Christmas Host;
  private Location _spawn;
  private ArrayList<SnowmanMinion> _minions;
  private Creature _heart;
  
  public SnowmanBoss(Christmas host, Location spawn)
  {
    this.Host = host;
    this._spawn = spawn;
    
    this._minions = new ArrayList();
    

    this.Host.CreatureAllowOverride = true;
    
    for (int i = 0; i < 14; i++) {
      this._minions.add(new SnowmanMinion((Snowman)this._spawn.getWorld().spawn(this._spawn, Snowman.class)));
    }
    this._heart = ((Creature)this._spawn.getWorld().spawn(this._spawn, IronGolem.class));
    this._heart.setMaxHealth(250.0D);
    this._heart.setHealth(250.0D);
    UtilEnt.Vegetate(this._heart);
    
    this.Host.CreatureAllowOverride = false;
    

    Entity base = null;
    for (SnowmanMinion ent : this._minions)
    {
      if (base != null) {
        base.setPassenger(ent.Ent);
      }
      base = ent.Ent;
    }
    
    base.setPassenger(this._heart);
  }
  
  public void UpdateSnowball()
  {
    if (this.Host.GetPlayers(true).isEmpty()) {
      return;
    }
    Entity ent = this._heart;
    
    while (ent.getVehicle() != null)
    {
      ent = ent.getVehicle();
      
      if (Math.random() <= 0.005D * this.Host.GetPlayers(true).size())
      {

        Player target = (Player)UtilAlg.Random(this.Host.GetPlayers(true));
        
        Vector dir = UtilAlg.getTrajectory(ent, target);
        dir.multiply(2);
        
        Snowball ball = (Snowball)ent.getWorld().spawn(ent.getLocation().add(0.0D, 1.0D, 0.0D).add(dir), Snowball.class);
        
        ball.setShooter((LivingEntity)ent);
        ball.setVelocity(dir.add(new Vector(0.0D, Math.min(0.6D, UtilMath.offset2d(target, ent) / 150.0D), 0.0D)));
      }
    }
  }
  
  public void UpdateMove() {
    if (this._heart != null)
    {

      double speed = 20.0D;
      double oX = Math.sin(this._heart.getTicksLived() / speed) * 8.0D;
      double oY = 0.0D;
      double oZ = Math.cos(this._heart.getTicksLived() / speed) * 8.0D;
      Location loc = this._spawn.clone().add(oX, oY, oZ);
      
      Entity bottomEnt = this._heart;
      while (bottomEnt.getVehicle() != null) {
        bottomEnt = bottomEnt.getVehicle();
      }
      float rate = 2.0F;
      if (this._heart.getVehicle() == null) {
        rate = 1.0F;
      }
      UtilEnt.CreatureMoveFast(bottomEnt, loc, rate);
    }
    
    for (SnowmanMinion minion : this._minions)
    {
      if (minion.Ent.getVehicle() == null)
      {

        if (minion.Ent.getPassenger() == null)
        {


          if (minion.CanStack())
          {
            EntityCreature ec = ((CraftCreature)minion.Ent).getHandle();
            ec.getControllerMove().a(this._heart.getLocation().getX(), minion.Ent.getLocation().getY(), this._heart.getLocation().getZ(), 2.0D);

          }
          else
          {
            if (((minion.Target == null) || (!minion.Target.isValid()) || (!this.Host.IsAlive(minion.Target))) && (!this.Host.GetPlayers(true).isEmpty())) {
              minion.Target = ((Player)UtilAlg.Random(this.Host.GetPlayers(true)));
            }
            
            UtilEnt.CreatureMoveFast(minion.Ent, minion.Target.getLocation(), 1.6F);
            

            if (UtilMath.offset(minion.Ent, minion.Target) < 1.5D)
            {
              if (!Recharge.Instance.usable(minion.Target, "Snowman Hit")) {
                return;
              }
              UtilAction.velocity(minion.Target, UtilAlg.getTrajectory(minion.Ent, minion.Target), 0.8D, false, 0.0D, 0.3D, 1.2D, true);
              Recharge.Instance.useForce(minion.Target, "Snowman Hit", 1000L);
              

              this.Host.Manager.GetDamage().NewDamageEvent(minion.Target, minion.Ent, null, 
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 6.0D, false, false, false, 
                null, null);
            }
          }
        }
      }
    }
    if ((this._heart != null) && (!this._heart.isValid()))
    {
      for (SnowmanMinion minion : this._minions)
      {
        minion.Ent.getWorld().playEffect(minion.Ent.getLocation(), Effect.STEP_SOUND, 80);
        minion.Ent.remove();
      }
      
      this._minions.clear();
    }
  }
  
  public void UpdateCombine()
  {
    Entity bottomEnt = this._heart;
    while (bottomEnt.getVehicle() != null) {
      bottomEnt = bottomEnt.getVehicle();
    }
    for (SnowmanMinion minion : this._minions)
    {
      if (!bottomEnt.equals(minion.Ent))
      {

        if (minion.Ent.getVehicle() == null)
        {

          if (minion.CanStack())
          {

            if (UtilMath.offset(minion.Ent, bottomEnt) < 2.0D)
            {
              minion.Ent.setPassenger(bottomEnt);
              return;
            }
          }
        }
      }
    }
  }
  
























































  public void Damage(CustomDamageEvent event)
  {
    if (this._heart == null) {
      return;
    }
    if (event.GetDamageeEntity().equals(this._heart))
    {
      event.SetKnockback(false);
    }
    
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    if (this._heart.getVehicle() == null) {
      return;
    }
    
    if (event.GetDamageeEntity().equals(this._heart))
    {
      event.SetCancelled("Ranged Damage");
      
      this.Host.SantaSay("Good! Now kill it with your swords!");
      
      Entity cur = this._heart;
      
      while (cur.getVehicle() != null)
      {
        Entity past = cur;
        
        cur = cur.getVehicle();
        cur.eject();
        
        past.setVelocity(new Vector((Math.random() - 0.5D) * 2.0D, Math.random() * 1.0D, (Math.random() - 0.5D) * 2.0D));
      }
      
      for (SnowmanMinion minion : this._minions) {
        minion.StackDelay = System.currentTimeMillis();
      }
      return;
    }
  }
  
  public boolean IsDead()
  {
    return !this._heart.isValid();
  }
  
  public double GetHealth()
  {
    return this._heart.getHealth() / this._heart.getMaxHealth();
  }
}
