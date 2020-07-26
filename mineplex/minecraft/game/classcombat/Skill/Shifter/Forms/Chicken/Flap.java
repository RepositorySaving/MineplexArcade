package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.energy.Energy;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class Flap extends SkillActive
{
  private HashMap<Player, Vector> _active = new HashMap();
  private HashMap<Player, Long> _damaged = new HashMap();
  
  private long _damageDisableTime = 6000L;
  
  private double _flap = 0.5D;
  private double _min = 0.3D;
  private double _max = 0.7D;
  
  private FlapGrab _grab = null;
  
  private int _tick = 0;
  










  public Flap(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    this._grab = new FlapGrab(this);
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    

    if (this._damaged.containsKey(player))
    {
      long damageTime = ((Long)this._damaged.get(player)).longValue();
      
      if (!UtilTime.elapsed(damageTime, this._damageDisableTime))
      {
        UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " for " + 
          F.time(UtilTime.MakeStr(this._damageDisableTime - (System.currentTimeMillis() - damageTime))) + "."));
        
        return false;
      }
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    Vector vel = player.getLocation().getDirection();
    vel.multiply(this._flap + this._flap / 10.0D * level);
    vel.add(new Vector(0.0D, 0.1D, 0.0D));
    

    if (player.getVehicle() != null) {
      vel.multiply(0.5D + level * 0.05D);
    }
    this._active.put(player, vel);
    

    player.setVelocity(vel);
    

    player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.0F);
  }
  






  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent paramCustomDamageEvent)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method containsKey(Player) is undefined for the type Set<Player>\n");
  }
  





  @EventHandler
  public void Glide(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    this._tick = ((this._tick + 1) % 12);
    
    for (Player cur : GetUsers())
    {
      if (!this._active.containsKey(cur))
      {
        GetGrab().Release(cur);


      }
      else if (!cur.isBlocking())
      {
        this._active.remove(cur);

      }
      else
      {
        int level = getLevel(cur);
        if (level == 0)
        {
          this._active.remove(cur);

        }
        else
        {
          if (cur.getVehicle() != null)
          {

            if (!this.Factory.Energy().Use(cur, "Glide", 1.2D - 0.02D * level, true, true))
            {
              this._active.remove(cur);
              continue;

            }
            

          }
          else if (!this.Factory.Energy().Use(cur, "Glide", 0.6D - 0.02D * level, true, true))
          {
            this._active.remove(cur);
            continue;
          }
          


          Entity target = cur;
          if (cur.getVehicle() != null) {
            target = cur.getVehicle();
          }
          
          Vector vel = (Vector)this._active.get(cur);
          
          if ((mineplex.core.common.util.UtilEnt.isGrounded(target)) && (vel.getY() < 0.0D))
          {
            this._active.remove(cur);

          }
          else
          {
            double speed = vel.length();
            Vector turn = cur.getLocation().getDirection();
            turn.subtract(UtilAlg.Normalize(UtilAlg.Clone(vel)));
            turn.multiply(0.1D);
            vel.add(turn);
            UtilAlg.Normalize(vel).multiply(speed);
            

            vel.setX(vel.getX() * (1.0D - vel.getY() / 6.0D));
            vel.setZ(vel.getZ() * (1.0D - vel.getY() / 6.0D));
            if (vel.getY() > 0.0D) { vel.setY(vel.getY() * (1.0D - vel.getY() / 6.0D));
            }
            
            vel.multiply(0.998D);
            
            double minSpeed = this._min + this._min / 10.0D * level;
            double maxSpeed = this._max + this._max / 10.0D * level;
            

            if (vel.length() < minSpeed) {
              vel.normalize().multiply(minSpeed);
            }
            
            if (vel.length() > maxSpeed) {
              vel.normalize().multiply(maxSpeed);
            }
            
            target.setVelocity(vel);
            

            target.setFallDistance(0.0F);
            

            if (cur.equals(target))
            {
              for (Player other : UtilPlayer.getNearby(cur.getLocation(), 2.0D))
              {
                if (!other.equals(cur))
                {

                  if (this.Factory.Relation().CanHurt(cur, other))
                  {

                    this._grab.Grab(cur, other);
                    
                    if (vel.getY() < 0.1D)
                      vel.setY(0.1D);
                  }
                }
              }
            }
            if (this._tick == 0)
              cur.getWorld().playSound(cur.getLocation(), Sound.BAT_TAKEOFF, 0.4F, 1.0F);
          }
        }
      } }
  }
  
  @EventHandler
  public void DamageRelease(CustomDamageEvent event) { GetGrab().DamageRelease(event); }
  

  public FlapGrab GetGrab()
  {
    return this._grab;
  }
  

  public void Reset(Player player)
  {
    this._grab.Reset(player);
    
    this._active.remove(player);
    this._damaged.remove(player);
  }
}
