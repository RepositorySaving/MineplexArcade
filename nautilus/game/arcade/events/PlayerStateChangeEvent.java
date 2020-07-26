package nautilus.game.arcade.events;

import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStateChangeEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private Game _game;
  private Player _player;
  private GameTeam.PlayerState _state;
  
  public PlayerStateChangeEvent(Game game, Player player, GameTeam.PlayerState state)
  {
    this._game = game;
    this._player = player;
    this._state = state;
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
  
  public Player GetPlayer()
  {
    return this._player;
  }
  
  public GameTeam.PlayerState GetState()
  {
    return this._state;
  }
}
