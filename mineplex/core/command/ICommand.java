package mineplex.core.command;

import java.util.Collection;
import mineplex.core.common.Rank;
import org.bukkit.entity.Player;

public abstract interface ICommand
{
  public abstract void SetCommandCenter(CommandCenter paramCommandCenter);
  
  public abstract void Execute(Player paramPlayer, String[] paramArrayOfString);
  
  public abstract Collection<String> Aliases();
  
  public abstract void SetAliasUsed(String paramString);
  
  public abstract Rank GetRequiredRank();
}
