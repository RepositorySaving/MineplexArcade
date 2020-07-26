package mineplex.core.npc.Commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;
import org.bukkit.entity.Player;

public class HomeCommand
  extends CommandBase<NpcManager>
{
  public HomeCommand(NpcManager plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "home" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args != null)
    {
      ((NpcManager)this.Plugin).Help(caller);
    }
    else
    {
      ((NpcManager)this.Plugin).TeleportNpcsHome();
      UtilPlayer.message(caller, F.main(((NpcManager)this.Plugin).GetName(), "Npcs teleported to home locations."));
    }
  }
}
