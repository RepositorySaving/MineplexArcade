package mineplex.core.npc.Commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;
import org.bukkit.entity.Player;

public class ReattachCommand
  extends CommandBase<NpcManager>
{
  public ReattachCommand(NpcManager plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "reattach" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args != null)
    {
      ((NpcManager)this.Plugin).Help(caller);
    }
    else
    {
      ((NpcManager)this.Plugin).ReattachNpcs();
      UtilPlayer.message(caller, F.main(((NpcManager)this.Plugin).GetName(), "Npcs reattached."));
    }
  }
}
