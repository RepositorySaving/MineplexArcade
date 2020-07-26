package mineplex.core.message.Commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.ClientMessage;
import mineplex.core.message.MessageManager;
import org.bukkit.entity.Player;

public class ResendAdminCommand extends CommandBase<MessageManager>
{
  public ResendAdminCommand(MessageManager plugin)
  {
    super(plugin, Rank.ALL, new String[] { "ra" });
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
      
      if (((ClientMessage)((MessageManager)this.Plugin).Get(caller)).LastAdminTo == null)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "You have not admin messaged anyone recently."));
        return;
      }
      
      Player to = UtilPlayer.searchOnline(caller, ((ClientMessage)((MessageManager)this.Plugin).Get(caller)).LastAdminTo, false);
      if (to == null)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), F.name(((ClientMessage)((MessageManager)this.Plugin).Get(caller)).LastAdminTo) + " is no longer online."));
        return;
      }
      

      if (args.length < 1)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "Message argument missing."));
        return;
      }
      
      String message = F.combine(args, 0, null, false);
      

      ((MessageManager)this.Plugin).DoMessageAdmin(caller, to, message);
    }
  }
}
