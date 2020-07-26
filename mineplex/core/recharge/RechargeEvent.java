package mineplex.core.recharge;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RechargeEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private Player _player;
  private String _ability;
  private long _recharge;
  
  public RechargeEvent(Player player, String ability, long recharge)
  {
    this._player = player;
    this._ability = ability;
    this._recharge = recharge;
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
  
  public String GetAbility()
  {
    return this._ability;
  }
  
  public long GetRecharge()
  {
    return this._recharge;
  }
  
  public void SetRecharge(long time)
  {
    this._recharge = time;
  }
}
