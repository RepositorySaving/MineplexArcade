package mineplex.core.message.Commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.ClientMessage;
import mineplex.core.message.MessageManager;
import org.bukkit.entity.Player;

public class ResendCommand extends CommandBase<MessageManager>
{
  public ResendCommand(MessageManager plugin)
  {
    super(plugin, Rank.ALL, new String[] { "r" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args == null)
    {
      ((MessageManager)this.Plugin).Help(caller);

    }
    else
    {
      if (((ClientMessage)((MessageManager)this.Plugin).Get(caller)).LastTo == null)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), "You have not messaged anyone recently."));
        return;
      }
      
      Player to = UtilPlayer.searchOnline(caller, ((ClientMessage)((MessageManager)this.Plugin).Get(caller)).LastTo, false);
      if (to == null)
      {
        UtilPlayer.message(caller, F.main(((MessageManager)this.Plugin).GetName(), F.name(((ClientMessage)((MessageManager)this.Plugin).Get(caller)).LastTo) + " is no longer online."));
        return;
      }
      

      String message = "Beep!";
      if (args.length > 0)
      {
        message = F.combine(args, 0, null, false);
      }
      else
      {
        message = ((MessageManager)this.Plugin).GetRandomMessage();
      }
      

      ((MessageManager)this.Plugin).DoMessage(caller, to, message);
    }
  }
}
