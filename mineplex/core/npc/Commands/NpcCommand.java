package mineplex.core.npc.Commands;

import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;
import mineplex.core.npc.NpcManager;
import org.bukkit.entity.Player;

public class NpcCommand
  extends MultiCommandBase<NpcManager>
{
  public NpcCommand(NpcManager plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "npc" });
    
    AddCommand(new AddCommand(plugin));
    AddCommand(new DeleteCommand(plugin));
    AddCommand(new ClearCommand(plugin));
    AddCommand(new HomeCommand(plugin));
    AddCommand(new ReattachCommand(plugin));
  }
  

  protected void Help(Player caller, String[] args)
  {
    ((NpcManager)this.Plugin).Help(caller);
  }
}
