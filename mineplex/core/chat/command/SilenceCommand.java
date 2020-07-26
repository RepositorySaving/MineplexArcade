package mineplex.core.chat.command;

import mineplex.core.chat.Chat;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.entity.Player;

public class SilenceCommand
  extends CommandBase<Chat>
{
  public SilenceCommand(Chat plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "silence" });
  }
  


  public void Execute(Player caller, String[] args)
  {
    try
    {
      if (args.length == 0)
      {

        if (((Chat)this.Plugin).Silenced() != 0L)
        {
          ((Chat)this.Plugin).Silence(0L, true);

        }
        else
        {
          ((Chat)this.Plugin).Silence(-1L, true);
        }
        
      }
      else
      {
        long time = (Double.valueOf(args[0]).doubleValue() * 3600000.0D);
        
        ((Chat)this.Plugin).Silence(time, true);
      }
    }
    catch (Exception e)
    {
      UtilPlayer.message(caller, F.main("Chat", "Invalid Time Parameter."));
    }
  }
}
