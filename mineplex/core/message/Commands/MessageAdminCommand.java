package mineplex.core.message.Commands;

import mineplex.core.account.CoreClient;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.MessageManager;
import org.bukkit.entity.Player;

public class MessageAdminCommand extends CommandBase<MessageManager>
{
  public MessageAdminCommand(MessageManager plugin)
  {
    super(plugin, Rank.ALL, new String[] { "ma" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args == null)
    {
      ((MessageManager)this.Plugin).Help(caller);
    }
    else
    {
      if (!((MessageManager)this.Plugin).GetClientManager().Get(caller).GetRank().Has(caller, Rank.HELPER, true)) {
        return;
      }
      if (args.length == 0)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "Player argument missing."));
        return;
      }
      

      Player to = UtilPlayer.searchOnline(caller, args[0], true);
      if (to == null) {
        return;
      }
      
      if (args.length < 2)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "Message argument missing."));
        return;
      }
      
      String message = F.combine(args, 1, null, false);
      

      ((MessageManager)this.Plugin).DoMessageAdmin(caller, to, message);
    }
  }
}
