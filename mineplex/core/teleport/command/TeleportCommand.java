package mineplex.core.teleport.command;

import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.teleport.Teleport;
import org.bukkit.entity.Player;

public class TeleportCommand extends mineplex.core.command.MultiCommandBase<Teleport>
{
  public TeleportCommand(Teleport plugin)
  {
    super(plugin, Rank.MODERATOR, new String[] { "tp", "teleport" });
    
    AddCommand(new AllCommand(plugin));
    AddCommand(new BackCommand(plugin));
    AddCommand(new HereCommand(plugin));
    AddCommand(new SpawnCommand(plugin));
  }
  


  protected void Help(Player caller, String[] args)
  {
    if ((args.length == 1) && (this.CommandCenter.GetClientManager().Get(caller).GetRank().Has(caller, Rank.MODERATOR, true))) {
      ((Teleport)this.Plugin).playerToPlayer(caller, caller.getName(), args[0]);

    }
    else if ((args.length == 2) && (this.CommandCenter.GetClientManager().Get(caller).GetRank().Has(caller, Rank.ADMIN, true))) {
      ((Teleport)this.Plugin).playerToPlayer(caller, args[0], args[1]);

    }
    else if ((args.length == 3) && (this.CommandCenter.GetClientManager().Get(caller).GetRank().Has(caller, Rank.ADMIN, true))) {
      ((Teleport)this.Plugin).playerToLoc(caller, caller.getName(), args[0], args[1], args[2]);

    }
    else if (args.length == 5) {
      ((Teleport)this.Plugin).playerToLoc(caller, args[0], args[1], args[2], args[3], args[4]);

    }
    else if ((args.length == 4) && (this.CommandCenter.GetClientManager().Get(caller).GetRank().Has(caller, Rank.ADMIN, true))) {
      ((Teleport)this.Plugin).playerToLoc(caller, args[0], args[1], args[2], args[3]);
    }
    else {
      UtilPlayer.message(caller, F.main(((Teleport)this.Plugin).GetName(), "Commands List:"));
      UtilPlayer.message(caller, F.help("/tp <target>", "Teleport to Player", Rank.MODERATOR));
      UtilPlayer.message(caller, F.help("/tp b(ack) (amount) (player)", "Undo Teleports", Rank.MODERATOR));
      UtilPlayer.message(caller, F.help("/tp here <player>", "Teleport Player to Self", Rank.ADMIN));
      UtilPlayer.message(caller, F.help("/tp <player> <target>", "Teleport Player to Player", Rank.ADMIN));
      UtilPlayer.message(caller, F.help("/tp <X> <Y> <Z>", "Teleport to Location", Rank.ADMIN));
      UtilPlayer.message(caller, F.help("/tp spawn", "Teleport to Spawn", Rank.ADMIN));
      UtilPlayer.message(caller, F.help("/tp all", "Teleport All to Self", Rank.OWNER));
    }
  }
}
