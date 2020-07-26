package nautilus.game.arcade.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.world.WorldParser;
import org.bukkit.entity.Player;

public class ParseCommand
  extends CommandBase<ArcadeManager>
{
  public ParseCommand(ArcadeManager plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "parse" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    caller.sendMessage("Parsing World");
    WorldParser parser = new WorldParser();
    parser.Parse(caller, args);
  }
}
