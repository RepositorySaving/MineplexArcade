package mineplex.core.spawn.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.spawn.Spawn;
import org.bukkit.entity.Player;

public class SpawnCommand
  extends CommandBase<Spawn>
{
  public SpawnCommand(Spawn plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "spawn" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    UtilPlayer.message(caller, F.main("Spawn", "Commands List:"));
    UtilPlayer.message(caller, F.help("/spawn add", "Add Location as Spawn", Rank.ADMIN));
    UtilPlayer.message(caller, F.help("/spawn clear", "Remove All Spawns", Rank.ADMIN));
  }
}
