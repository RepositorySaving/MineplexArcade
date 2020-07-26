package nautilus.game.arcade.command;

import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;
import nautilus.game.arcade.ArcadeManager;
import org.bukkit.entity.Player;

public class GameCommand
  extends MultiCommandBase<ArcadeManager>
{
  public GameCommand(ArcadeManager plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "game" });
    
    AddCommand(new StartCommand((ArcadeManager)this.Plugin));
    AddCommand(new StopCommand((ArcadeManager)this.Plugin));
  }
  
  protected void Help(Player caller, String[] args) {}
}
