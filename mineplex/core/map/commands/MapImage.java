package mineplex.core.map.commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.map.Map;
import org.bukkit.entity.Player;

public class MapImage
  extends CommandBase<Map>
{
  public MapImage(Map plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "mi" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    ((Map)this.Plugin).SpawnMap(caller, args);
  }
}
