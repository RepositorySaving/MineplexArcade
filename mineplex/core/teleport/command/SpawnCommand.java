package mineplex.core.teleport.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.teleport.Teleport;
import org.bukkit.entity.Player;

public class SpawnCommand
  extends CommandBase<Teleport>
{
  public SpawnCommand(Teleport plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "spawn", "s" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args.length == 0) {
      ((Teleport)this.Plugin).playerToSpawn(caller, caller.getName());
    } else {
      ((Teleport)this.Plugin).playerToSpawn(caller, args[0]);
    }
  }
}
