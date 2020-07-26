package nautilus.game.arcade.game.games.survivalgames;

import org.bukkit.Location;

public class BlockChange
{
  public Location Location;
  public int Id;
  public byte Data;
  
  public BlockChange(Location loc, int id, byte data)
  {
    this.Location = loc;
    this.Id = id;
    this.Data = data;
  }
}
