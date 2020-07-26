package mineplex.core.teleport.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.teleport.Teleport;
import org.bukkit.entity.Player;

public class AllCommand
  extends CommandBase<Teleport>
{
  public AllCommand(Teleport plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "all" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    ((Teleport)this.Plugin).playerToPlayer(caller, "%ALL%", caller.getName());
  }
}
