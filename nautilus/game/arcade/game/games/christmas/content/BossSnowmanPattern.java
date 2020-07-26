package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import java.util.Iterator;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.parts.Part5;
import net.minecraft.server.v1_7_R3.ControllerMove;
import net.minecraft.server.v1_7_R3.EntityCreature;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class BossSnowmanPattern
{
  private boolean _active = false;
  private int _difficulty = 0;
  
  private Part5 Host;
  
  private ArrayList<Location> _spawnA;
  private ArrayList<Location> _spawnB;
  private int _aDir = 1;
  private int _bDir = -1;
  
  private long _lastSpawn = 0L;
  private int _lastGap = 0;
  
  private ArrayList<BossSnowman> _ents = new ArrayList();
  
  public BossSnowmanPattern(Part5 host, ArrayList<Location> spawnA, ArrayList<Location> spawnB, Location waypoint)
  {
    this.Host = host;
    
    this._spawnA = new ArrayList();
    this._spawnB = new ArrayList();
    

    while (!spawnA.isEmpty())
    {
      Location bestLoc = null;
      double bestDist = 0.0D;
      
      for (Location loc : spawnA)
      {
        double dist = UtilMath.offset(waypoint, loc);
        
        if ((bestLoc == null) || (bestDist > dist))
        {
          bestLoc = loc;
          bestDist = dist;
        }
      }
      
      this._spawnA.add(bestLoc);
      spawnA.remove(bestLoc);
    }
    

    while (!spawnB.isEmpty())
    {
      Location bestLoc = null;
      double bestDist = 0.0D;
      
      for (Location loc : spawnB)
      {
        double dist = UtilMath.offset(waypoint, loc);
        
        if ((bestLoc == null) || (bestDist > dist))
        {
          bestLoc = loc;
          bestDist = dist;
        }
      }
      
      this._spawnB.add(bestLoc);
      spawnB.remove(bestLoc);
    }
  }
  
  public void SetActive(boolean active, int difficulty)
  {
    this._active = active;
    this._difficulty = difficulty;
  }
  
  public void Update()
  {
    MoveDieHit();
    
    if (!this._active) {
      return;
    }
    
    if (!UtilTime.elapsed(this._lastSpawn, 4000 - 500 * this._difficulty))
      return;
    this._lastSpawn = System.currentTimeMillis();
    
    this.Host.Host.CreatureAllowOverride = true;
    

    for (int i = 0; i < this._spawnA.size(); i++)
    {
      if (i % 6 >= 3)
      {

        Location loc = (Location)this._spawnA.get(i);
        Snowman ent = (Snowman)loc.getWorld().spawn(loc, Snowman.class);
        UtilEnt.Vegetate(ent);
        UtilEnt.ghost(ent, true, false);
        this._ents.add(new BossSnowman(ent, loc, this._aDir));
      }
    }
    
    for (int i = 0; i < this._spawnB.size(); i++)
    {
      if (i % 6 < 3)
      {

        Location loc = (Location)this._spawnB.get(i);
        Snowman ent = (Snowman)loc.getWorld().spawn(loc, Snowman.class);
        UtilEnt.Vegetate(ent);
        UtilEnt.ghost(ent, true, false);
        this._ents.add(new BossSnowman(ent, loc, this._bDir));
      }
    }
    this.Host.Host.CreatureAllowOverride = false;
  }
  
  private void MoveDieHit()
  {
    Iterator<BossSnowman> entIterator = this._ents.iterator();
    

    while (entIterator.hasNext())
    {
      BossSnowman ent = (BossSnowman)entIterator.next();
      
      ec = ((CraftCreature)ent.Entity).getHandle();
      ec.getControllerMove().a(ent.Entity.getLocation().getX() + ent.Direction, ent.Entity.getLocation().getY(), ent.Entity.getLocation().getZ(), 1.25D + 0.25D * this._difficulty);
      
      if ((!ent.Entity.isValid()) || (UtilMath.offset(ent.Entity.getLocation(), ent.Spawn) > 43.0D))
      {
        ent.Entity.remove();
        entIterator.remove();
      }
    }
    
    Iterator localIterator;
    for (EntityCreature ec = this.Host.Host.GetPlayers(true).iterator(); ec.hasNext(); 
        



        localIterator.hasNext())
    {
      Player player = (Player)ec.next();
      
      if (!Recharge.Instance.usable(player, "Snowman Hit")) {
        return;
      }
      localIterator = this._ents.iterator(); continue;BossSnowman snowman = (BossSnowman)localIterator.next();
      
      if (UtilMath.offset2d(player, snowman.Entity) < 1.0D)
      {
        if (Math.abs(player.getLocation().getY() - snowman.Entity.getLocation().getY()) < 2.0D)
        {
          UtilAction.velocity(player, new Vector(snowman.Direction, 0, 0), 2.0D, false, 0.0D, 0.8D, 0.8D, true);
          Recharge.Instance.useForce(player, "Snowman Hit", 1000L);
          

          this.Host.Host.Manager.GetDamage().NewDamageEvent(player, snowman.Entity, null, 
            EntityDamageEvent.DamageCause.ENTITY_ATTACK, 3.0D, false, false, false, 
            null, null);
        }
      }
    }
  }
}
