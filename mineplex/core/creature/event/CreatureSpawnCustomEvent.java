package mineplex.core.creature.event;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CreatureSpawnCustomEvent extends Event implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private boolean _cancelled = false;
  
  private Location _location;
  
  public CreatureSpawnCustomEvent(Location location)
  {
    this._location = location;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  

  public boolean isCancelled()
  {
    return this._cancelled;
  }
  

  public void setCancelled(boolean cancel)
  {
    this._cancelled = cancel;
  }
  
  public Location GetLocation()
  {
    return this._location;
  }
}
