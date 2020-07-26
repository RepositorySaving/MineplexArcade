package mineplex.core.server.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVoteEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private String _playerName;
  private int _pointsReceived;
  
  public PlayerVoteEvent(String playerName, int pointsReceived)
  {
    this._playerName = playerName;
    this._pointsReceived = pointsReceived;
  }
  
  public String GetPlayerName()
  {
    return this._playerName;
  }
  
  public int GetPointsReceived()
  {
    return this._pointsReceived;
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
