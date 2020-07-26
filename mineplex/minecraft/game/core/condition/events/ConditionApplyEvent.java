package mineplex.minecraft.game.core.condition.events;

import mineplex.minecraft.game.core.condition.Condition;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ConditionApplyEvent
  extends Event implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private boolean _cancelled = false;
  
  private Condition _cond;
  
  public ConditionApplyEvent(Condition cond)
  {
    this._cond = cond;
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
  
  public Condition GetCondition()
  {
    return this._cond;
  }
}
