package mineplex.minecraft.game.classcombat.item.event;

import mineplex.minecraft.game.classcombat.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemTriggerEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private Player _player;
  private Item _item;
  private boolean _cancelled = false;
  
  public ItemTriggerEvent(Player player, Item item)
  {
    this._player = player;
    this._item = item;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public Player GetPlayer()
  {
    return this._player;
  }
  
  public Item GetItemType()
  {
    return this._item;
  }
  
  public boolean IsCancelled() {
    return this._cancelled;
  }
  
  public void SetCancelled(boolean cancelled)
  {
    this._cancelled = cancelled;
  }
}
