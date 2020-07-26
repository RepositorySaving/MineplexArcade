package nautilus.game.arcade.game.games.halloween.creatures;

import java.util.ArrayList;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;

public abstract class CreatureBase<T extends LivingEntity>
{
  public Game Host;
  private String _name;
  private T _ent;
  private Location _target;
  private long _targetTime;
  
  public CreatureBase(Game game, String name, Class<T> mobClass, Location loc)
  {
    this.Host = game;
    this._name = name;
    
    game.CreatureAllowOverride = true;
    this._ent = ((LivingEntity)loc.getWorld().spawn(loc, mobClass));
    

    if (this._name != null)
    {
      this._ent.setCustomName(name);
      this._ent.setCustomNameVisible(true);
    }
    
    SpawnCustom(this._ent);
    
    game.CreatureAllowOverride = false;
  }
  
  public abstract void SpawnCustom(T paramT);
  
  public String GetName()
  {
    return this._name;
  }
  
  public T GetEntity()
  {
    return this._ent;
  }
  
  public Location GetTarget()
  {
    return this._target;
  }
  
  public void SetTarget(Location loc)
  {
    this._target = loc;
    this._targetTime = System.currentTimeMillis();
  }
  
  public long GetTargetTime()
  {
    return this._targetTime;
  }
  
  public Location GetPlayerTarget()
  {
    if (this.Host.GetPlayers(true).size() == 0)
    {
      return ((GameTeam)this.Host.GetTeamList().get(0)).GetSpawn();
    }
    

    Player target = (Player)this.Host.GetPlayers(true).get(UtilMath.r(this.Host.GetPlayers(true).size()));
    return target.getLocation();
  }
  

  public Location GetRoamTarget()
  {
    if (Math.random() > 0.75D) {
      return GetPlayerTarget();
    }
    Vector vec = new Vector(UtilMath.r(80) - 40, 0, UtilMath.r(80) - 40);
    return vec.toLocation(this.Host.GetSpectatorLocation().getWorld());
  }
  
  public boolean Updater(UpdateEvent event)
  {
    if ((this._ent == null) || (!this._ent.isValid())) {
      return true;
    }
    Update(event);
    
    return false;
  }
  
  public abstract void Update(UpdateEvent paramUpdateEvent);
  
  public abstract void Damage(CustomDamageEvent paramCustomDamageEvent);
  
  public abstract void Target(EntityTargetEvent paramEntityTargetEvent);
  
  public void DefaultMove()
  {
    EntityCreature ec = ((CraftCreature)GetEntity()).getHandle();
    Navigation nav = ec.getNavigation();
    
    if (UtilMath.offset(GetEntity().getLocation(), GetTarget()) > 16.0D)
    {
      Location target = GetEntity().getLocation();
      
      target.add(UtilAlg.getTrajectory(GetEntity().getLocation(), GetTarget()).multiply(16));
      
      nav.a(target.getX(), target.getY(), target.getZ(), 1.0D);
    }
    else
    {
      nav.a(GetTarget().getX(), GetTarget().getY(), GetTarget().getZ(), 1.0D);
    }
  }
}
