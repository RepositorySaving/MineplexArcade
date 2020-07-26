package mineplex.minecraft.game.core.combat.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClearCombatEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private Player _player;
  
  public ClearCombatEvent(Player player)
  {
    this._player = player;
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
}
