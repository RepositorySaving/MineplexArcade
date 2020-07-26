package mineplex.core.teleport.command;

import java.util.LinkedList;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.teleport.Teleport;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BackCommand extends CommandBase<Teleport>
{
  public BackCommand(Teleport plugin)
  {
    super(plugin, Rank.MODERATOR, new String[] { "back", "b" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args.length == 0) {
      Back(caller, caller.getName(), "1");
    } else if (args.length == 1) {
      Back(caller, args[0], "1");
    } else {
      Back(caller, args[0], args[1]);
    }
  }
  
  private void Back(Player caller, String target, String amountString) {
    int amount = 1;
    try
    {
      amount = Integer.parseInt(amountString);
    }
    catch (Exception e)
    {
      UtilPlayer.message(caller, F.main("Teleport", "Invalid Amount [" + amountString + "]. Defaulting to [1]."));
    }
    

    Player player = UtilPlayer.searchOnline(caller, target, true);
    
    if (player == null) {
      return;
    }
    Location loc = null;
    int back = 0;
    for (int i = 0; i < amount; i++)
    {
      if (((Teleport)this.Plugin).GetTPHistory(player).isEmpty()) {
        break;
      }
      loc = (Location)((Teleport)this.Plugin).GetTPHistory(player).removeFirst();
      back++;
    }
    
    if (loc == null)
    {
      UtilPlayer.message(caller, F.main("Teleport", player.getName() + " has no teleport history."));
      return;
    }
    

    String mA = F.main("Teleport", F.elem(caller.getName()) + " undid your last " + F.count(new StringBuilder().append(back).toString()) + " teleport(s).");
    String mB = F.main("Teleport", "You undid the last " + F.count(new StringBuilder().append(back).toString()) + " teleport(s) for " + F.elem(player.getName()) + ".");
    ((Teleport)this.Plugin).Add(player, loc, mA, false, caller, mB, "Undid last " + back + " teleports for " + player.getName() + " via " + caller.getName());
  }
}
