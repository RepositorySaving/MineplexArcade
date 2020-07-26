package mineplex.core.account.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClientWebResponseEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private String _response;
  
  public ClientWebResponseEvent(String response)
  {
    this._response = response;
  }
  
  public String GetResponse()
  {
    return this._response;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
}
