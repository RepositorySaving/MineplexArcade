package mineplex.core.account.event;

import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClientWebRequestEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private JsonWriter _writer;
  
  public ClientWebRequestEvent(JsonWriter writer)
  {
    this._writer = writer;
  }
  
  public JsonWriter GetJsonWriter()
  {
    return this._writer;
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
