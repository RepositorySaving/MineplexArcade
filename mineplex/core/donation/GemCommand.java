package mineplex.core.donation;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.entity.Player;

public class GemCommand
  extends CommandBase<DonationManager>
{
  public GemCommand(DonationManager plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "gem" });
  }
  

  public void Execute(final Player caller, String[] args)
  {
    if (args.length < 2)
    {
      UtilPlayer.message(caller, F.main("Gem", "Missing Args"));
      return;
    }
    

    final Player target = UtilPlayer.searchOnline(caller, args[0], true);
    
    if (target == null) {
      return;
    }
    
    try
    {
      final int gems = Integer.parseInt(args[1]);
      ((DonationManager)this.Plugin).RewardGems(new Callback()
      {
        public void run(Boolean completed)
        {
          UtilPlayer.message(caller, F.main("Gem", "You gave " + F.elem(new StringBuilder(String.valueOf(gems)).append(" Gems").toString()) + " to " + F.name(target.getName()) + "."));
          UtilPlayer.message(target, F.main("Gem", F.name(caller.getName()) + " gave you " + F.elem(new StringBuilder(String.valueOf(gems)).append(" Gems").toString()) + "."));
        }
      }, caller.getName(), target.getName(), gems);
    }
    catch (Exception e)
    {
      UtilPlayer.message(caller, F.main("Gem", "Invalid Gem Amount"));
    }
  }
}
