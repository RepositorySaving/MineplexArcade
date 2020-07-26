package mineplex.core.message.Commands;

import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.MessageManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AdminCommand extends CommandBase<MessageManager>
{
  public AdminCommand(MessageManager plugin)
  {
    super(plugin, Rank.ALL, new String[] { "a", "admin" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args == null)
    {
      ((MessageManager)this.Plugin).Help(caller);
    }
    else
    {
      if (args.length == 0)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "Message argument missing."));
        return;
      }
      

      String message = F.combine(args, 0, null, false);
      

      UtilPlayer.message(caller, F.rank(((MessageManager)this.Plugin).GetClientManager().Get(caller).GetRank()) + " " + caller.getName() + " " + C.cPurple + message);
      

      boolean staff = false;
      for (Player to : mineplex.core.common.util.UtilServer.getPlayers())
      {
        if (((MessageManager)this.Plugin).GetClientManager().Get(to).GetRank().Has(Rank.HELPER))
        {
          if (!to.equals(caller)) {
            UtilPlayer.message(to, F.rank(((MessageManager)this.Plugin).GetClientManager().Get(caller).GetRank()) + " " + caller.getName() + " " + C.cPurple + message);
          }
          staff = true;
          

          to.playSound(to.getLocation(), Sound.NOTE_PLING, 0.5F, 2.0F);
        }
      }
      
      if (!staff) {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "There are no Staff Members online."));
      }
    }
  }
}
