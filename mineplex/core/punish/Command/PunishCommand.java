package mineplex.core.punish.Command;

import java.util.Iterator;
import java.util.List;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.punish.Punish;
import mineplex.core.punish.PunishRepository;
import mineplex.core.punish.Tokens.PunishClientToken;
import mineplex.core.punish.UI.PunishPage;
import org.bukkit.entity.Player;


public class PunishCommand
  extends CommandBase<Punish>
{
  public PunishCommand(Punish plugin)
  {
    super(plugin, Rank.HELPER, new String[] { "punish", "p" });
  }
  

  public void Execute(final Player caller, String[] args)
  {
    if ((args == null) || (args.length < 2))
    {
      ((Punish)this.Plugin).Help(caller);
    }
    else
    {
      final String playerName = args[0];
      String reason = args[1];
      
      for (int i = 2; i < args.length; i++)
      {
        reason = reason + " " + args[i];
      }
      
      final String finalReason = reason;
      
      ((Punish)this.Plugin).GetRepository().MatchPlayerName(new Callback()
      {
        public void run(List<String> matches)
        {
          boolean matchedExact = false;
          
          for (String match : matches)
          {
            if (match.equalsIgnoreCase(playerName))
            {
              matchedExact = true;
            }
          }
          
          if (matchedExact)
          {
            for (Iterator<String> matchIterator = matches.iterator(); matchIterator.hasNext();)
            {
              if (!((String)matchIterator.next()).equalsIgnoreCase(playerName))
              {
                matchIterator.remove();
              }
            }
          }
          
          UtilPlayer.searchOffline(matches, new Callback()
          {
            public void run(final String target)
            {
              if (target == null)
              {
                return;
              }
              
              ((Punish)PunishCommand.this.Plugin).GetRepository().LoadPunishClient(target, new Callback()
              {
                public void run(PunishClientToken clientToken)
                {
                  ((Punish)PunishCommand.this.Plugin).LoadClient(clientToken);
                  new PunishPage((Punish)PunishCommand.this.Plugin, this.val$caller, target, this.val$finalReason);
                }
                
              });
            }
          }, caller, playerName, true);
        }
      }, playerName);
    }
  }
}
