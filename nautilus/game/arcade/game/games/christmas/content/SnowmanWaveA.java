package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import java.util.Iterator;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import net.minecraft.server.v1_7_R3.ControllerMove;
import net.minecraft.server.v1_7_R3.EntityCreature;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class SnowmanWaveA
{
  private Christmas Host;
  private ArrayList<Location> _spawns;
  private Location _present;
  private int xDir = 1;
  
  private long lastSpawn = 0L;
  private int lastGap = 0;
  
  private ArrayList<Snowman> _ents = new ArrayList();
  
  public SnowmanWaveA(Christmas host, ArrayList<Location> spawns, Location waypoint, Location[] presents)
  {
    this.Host = host;
    
    this._spawns = new ArrayList();
    

    while (!spawns.isEmpty())
    {
      Location bestLoc = null;
      double bestDist = 0.0D;
      
      for (Location loc : spawns)
      {
        double dist = UtilMath.offset(waypoint, loc);
        
        if ((bestLoc == null) || (bestDist > dist))
        {
          bestLoc = loc;
          bestDist = dist;
        }
      }
      
      this._spawns.add(bestLoc);
      spawns.remove(bestLoc);
    }
    

    if (UtilMath.offset(presents[0], (Location)this._spawns.get(0)) < UtilMath.offset(presents[0], (Location)this._spawns.get(0)))
    {
      this._present = presents[0].getBlock().getLocation();
    }
    else
    {
      this._present = presents[1].getBlock().getLocation();
    }
  }
  

  public void Update()
  {
    if (!this.Host.GetSleigh().HasPresent(this._present))
    {
      if (mineplex.core.common.util.UtilTime.elapsed(this.lastSpawn, 2000L))
      {
        this.lastSpawn = System.currentTimeMillis();
        
        int gap = 1 + UtilMath.r(this._spawns.size() - 1);
        

        while ((Math.abs(this.lastGap - gap) < 5) || (Math.abs(this.lastGap - gap) > 13)) {
          gap = 1 + UtilMath.r(this._spawns.size() - 1);
        }
        this.lastGap = gap;
        
        for (int i = 0; i < this._spawns.size(); i++)
        {
          if (Math.abs(gap - i) > 2)
          {

            Location loc = (Location)this._spawns.get(i);
            this.Host.CreatureAllowOverride = true;
            Snowman ent = (Snowman)loc.getWorld().spawn(loc, Snowman.class);
            this.Host.CreatureAllowOverride = false;
            UtilEnt.Vegetate(ent);
            UtilEnt.ghost(ent, true, false);
            this._ents.add(ent);
          }
        }
      }
    }
    Iterator<Snowman> entIterator = this._ents.iterator();
    

    while (entIterator.hasNext())
    {
      Snowman ent = (Snowman)entIterator.next();
      
      ec = ((CraftCreature)ent).getHandle();
      ec.getControllerMove().a(ent.getLocation().getX() + this.xDir, ent.getLocation().getY(), ent.getLocation().getZ(), 1.799999952316284D);
      
      double dist = Math.abs(((Location)this._spawns.get(0)).getX() - ent.getLocation().getX());
      
      if ((ent.getTicksLived() > 500) || (dist > 52.0D))
      {
        ent.getWorld().playEffect(ent.getLocation(), Effect.STEP_SOUND, 80);
        ent.remove();
        entIterator.remove();
      }
    }
    
    Iterator localIterator;
    for (EntityCreature ec = this.Host.GetPlayers(true).iterator(); ec.hasNext(); 
        



        localIterator.hasNext())
    {
      Player player = (Player)ec.next();
      
      if (!Recharge.Instance.usable(player, "Snowman Hit")) {
        return;
      }
      localIterator = this._ents.iterator(); continue;Snowman snowman = (Snowman)localIterator.next();
      
      if (UtilMath.offset2d(player, snowman) < 1.0D)
      {
        UtilAction.velocity(player, new Vector(this.xDir, 0, 0), 4.0D, false, 0.0D, 1.2D, 1.2D, true);
        Recharge.Instance.useForce(player, "Snowman Hit", 2000L);
        

        this.Host.Manager.GetDamage().NewDamageEvent(player, snowman, null, 
          EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4.0D, false, false, false, 
          null, null);
      }
    }
  }
}
