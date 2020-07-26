package nautilus.game.arcade.command;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.managers.GameLobbyManager;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class WriteCommand extends CommandBase<ArcadeManager>
{
  public WriteCommand(ArcadeManager plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "write" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if (args.length < 6)
    {
      caller.sendMessage("/write X Y Z BlockFace Line <Text>");
      return;
    }
    

    int x = 0;
    int y = 0;
    int z = 0;
    try
    {
      x = Integer.parseInt(args[0]);
      y = Integer.parseInt(args[1]);
      z = Integer.parseInt(args[2]);
    }
    catch (Exception e)
    {
      caller.sendMessage("Invalid Co-Ordinates");
      return;
    }
    

    BlockFace face = BlockFace.NORTH;
    try
    {
      face = BlockFace.valueOf(args[3].toUpperCase());
    }
    catch (Exception e)
    {
      caller.sendMessage("Invalid BlockFace");
      return;
    }
    

    int line = 0;
    try
    {
      line = Integer.parseInt(args[4]);
    }
    catch (Exception e)
    {
      caller.sendMessage("Invalid Line");
      return;
    }
    

    String text = "";
    for (int i = 5; i < args.length; i++)
      text = text + args[i] + " ";
    text = text.substring(0, text.length() - 1);
    
    ((ArcadeManager)this.Plugin).GetLobby().WriteLine(caller, x, y, z, face, line, text);
  }
}
