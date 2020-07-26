package mineplex.core.server.event;

import java.util.List;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameReadyEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private List<String> _players;
  
  public GameReadyEvent(List<String> players)
  {
    this._players = players;
  }
  
  public List<String> GetPlayers()
  {
    return this._players;
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
