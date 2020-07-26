package mineplex.core.spawn.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.spawn.Spawn;
import org.bukkit.entity.Player;

public class ClearCommand
  extends CommandBase<Spawn>
{
  public ClearCommand(Spawn plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "clear" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    ((Spawn)this.Plugin).ClearSpawn(caller);
  }
}
