package mineplex.core.teleport.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.teleport.Teleport;
import org.bukkit.entity.Player;

public class HereCommand
  extends CommandBase<Teleport>
{
  public HereCommand(Teleport plugin)
  {
    super(plugin, Rank.MODERATOR, new String[] { "here", "h" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args.length == 1) {
      ((Teleport)this.Plugin).playerToPlayer(caller, args[0], caller.getName());
    } else if (args.length == 2) {
      ((Teleport)this.Plugin).playerToPlayer(caller, args[0], args[1]);
    }
  }
}
