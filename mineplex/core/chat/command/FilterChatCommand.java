package mineplex.core.chat.command;

import mineplex.core.chat.Chat;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import org.bukkit.entity.Player;

public class FilterChatCommand
  extends CommandBase<Chat>
{
  public FilterChatCommand(Chat plugin)
  {
    super(plugin, Rank.ALL, new String[] { "filter" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    ((Chat)this.Plugin).toggleFilterChat(caller);
  }
}
