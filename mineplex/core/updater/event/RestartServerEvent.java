package mineplex.core.updater.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RestartServerEvent extends Event implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private boolean _cancelled = false;
  
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
}
