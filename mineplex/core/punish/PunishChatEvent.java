package mineplex.core.punish;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PunishChatEvent extends Event implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private boolean _cancelled = false;
  private Player _player;
  
  public PunishChatEvent(Player player)
  {
    this._player = player;
  }
  
  public Player GetPlayer()
  {
    return this._player;
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
}
