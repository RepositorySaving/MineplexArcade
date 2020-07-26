package mineplex.core.account.event;

import mineplex.core.account.CoreClient;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GetClientEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private String _name;
  
  private CoreClient _client;
  
  public GetClientEvent(String name)
  {
    this._name = name;
  }
  
  public GetClientEvent(Player player)
  {
    this._name = player.getName();
  }
  
  public CoreClient GetClient()
  {
    return this._client;
  }
  
  public void SetClient(CoreClient client)
  {
    this._client = client;
  }
  
  public void SetName(String name)
  {
    this._name = name;
  }
  
  public String GetName()
  {
    return this._name;
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
