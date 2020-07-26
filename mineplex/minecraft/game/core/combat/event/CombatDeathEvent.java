package mineplex.minecraft.game.core.combat.event;

import mineplex.minecraft.game.core.combat.ClientCombat;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.DeathMessageType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatDeathEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private EntityDeathEvent _event;
  private ClientCombat _clientCombat;
  private CombatLog _log;
  private DeathMessageType _messageType = DeathMessageType.Detailed;
  
  public CombatDeathEvent(EntityDeathEvent event, ClientCombat clientCombat, CombatLog log)
  {
    this._event = event;
    this._clientCombat = clientCombat;
    this._log = log;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public ClientCombat GetClientCombat()
  {
    return this._clientCombat;
  }
  
  public CombatLog GetLog()
  {
    return this._log;
  }
  
  public EntityDeathEvent GetEvent()
  {
    return this._event;
  }
  
  public void SetBroadcastType(DeathMessageType value)
  {
    this._messageType = value;
  }
  
  public DeathMessageType GetBroadcastType()
  {
    return this._messageType;
  }
}
