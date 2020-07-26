package nautilus.game.arcade.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class StopCommand extends CommandBase<ArcadeManager>
{
  public StopCommand(ArcadeManager plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "stop" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (((ArcadeManager)this.Plugin).GetGame() == null) {
      return;
    }
    HandlerList.unregisterAll(((ArcadeManager)this.Plugin).GetGame());
    
    if ((((ArcadeManager)this.Plugin).GetGame().GetState() == Game.GameState.End) || (((ArcadeManager)this.Plugin).GetGame().GetState() == Game.GameState.End))
    {
      caller.sendMessage("Game is already ending...");
      return;
    }
    if (((ArcadeManager)this.Plugin).GetGame().GetState() == Game.GameState.Recruit)
    {
      ((ArcadeManager)this.Plugin).GetGame().SetState(Game.GameState.Dead);
    }
    else
    {
      ((ArcadeManager)this.Plugin).GetGame().SetState(Game.GameState.End);
    }
    

    ((ArcadeManager)this.Plugin).GetGame().Announce(C.cAqua + C.Bold + caller.getName() + " has stopped the game.");
  }
}
