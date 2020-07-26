package mineplex.core.account.event;

import mineplex.core.account.CoreClient;
import mineplex.core.account.repository.token.ClientToken;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncClientLoadEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private ClientToken _token;
  private CoreClient _client;
  
  public AsyncClientLoadEvent(ClientToken token, CoreClient client)
  {
    this._token = token;
    this._client = client;
  }
  
  public CoreClient GetClient()
  {
    return this._client;
  }
  
  public ClientToken GetClientToken()
  {
    return this._token;
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
