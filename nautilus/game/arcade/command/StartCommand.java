package nautilus.game.arcade.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import org.bukkit.entity.Player;

public class StartCommand extends CommandBase<ArcadeManager>
{
  public StartCommand(ArcadeManager plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "start" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (((ArcadeManager)this.Plugin).GetGame() == null) {
      return;
    }
    if (((ArcadeManager)this.Plugin).GetGame().GetState() != Game.GameState.Recruit)
    {
      caller.sendMessage("Game is already in progress...");
      return;
    }
    
    ((ArcadeManager)this.Plugin).GetGameManager().StateCountdown(((ArcadeManager)this.Plugin).GetGame(), 10, true);
    
    ((ArcadeManager)this.Plugin).GetGame().Announce(C.cAqua + C.Bold + caller.getName() + " has started the game.");
  }
}
