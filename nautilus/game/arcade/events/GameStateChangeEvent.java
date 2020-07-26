package nautilus.game.arcade.events;

import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private Game _game;
  private Game.GameState _to;
  
  public GameStateChangeEvent(Game game, Game.GameState to)
  {
    this._game = game;
    this._to = to;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public Game GetGame()
  {
    return this._game;
  }
  
  public Game.GameState GetState()
  {
    return this._to;
  }
}
