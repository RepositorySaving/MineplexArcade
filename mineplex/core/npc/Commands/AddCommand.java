package mineplex.core.npc.Commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnum;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddCommand
  extends CommandBase<NpcManager>
{
  public AddCommand(NpcManager plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "add" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args == null)
    {
      ((NpcManager)this.Plugin).Help(caller);
    }
    else
    {
      try
      {
        int radius = Integer.parseInt(args[0]);
        String mobName = null;
        
        if (args.length > 1)
        {
          mobName = args[1];
          
          if (args.length > 2)
          {
            for (int i = 2; i < args.length; i++)
            {
              mobName = mobName + " " + args[i];
            }
          }
          
          while (mobName.indexOf('(') != -1)
          {
            int startIndex = mobName.indexOf('(');
            
            if (mobName.indexOf(')') == -1) {
              break;
            }
            int endIndex = mobName.indexOf(')');
            
            if (endIndex < startIndex) {
              break;
            }
            String originalText = mobName.substring(startIndex, endIndex + 1);
            String colorString = mobName.substring(startIndex + 1, endIndex);
            
            ChatColor color = (ChatColor)UtilEnum.fromString(ChatColor.class, colorString);
            
            mobName = mobName.replace(originalText, color);
          }
        }
        









        ((NpcManager)this.Plugin).SetNpcInfo(caller, radius, mobName, caller.getLocation());
        UtilPlayer.message(caller, F.main(((NpcManager)this.Plugin).GetName(), "Location set, now right click entity."));
      }
      catch (NumberFormatException exception)
      {
        ((NpcManager)this.Plugin).Help(caller, "Invalid radius.");
      }
      catch (IllegalArgumentException exception)
      {
        ((NpcManager)this.Plugin).Help(caller, "Invalid color.");
      }
    }
  }
}
