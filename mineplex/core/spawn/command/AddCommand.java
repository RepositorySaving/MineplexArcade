package mineplex.core.spawn.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.spawn.Spawn;
import org.bukkit.entity.Player;

public class AddCommand
  extends CommandBase<Spawn>
{
  public AddCommand(Spawn plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "add", "a" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    ((Spawn)this.Plugin).AddSpawn(caller);
  }
}
