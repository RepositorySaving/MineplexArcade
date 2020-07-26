package nautilus.game.arcade.kit.perks.data;

import java.util.HashSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FireflyData
{
  public Player Player;
  public org.bukkit.Location Location;
  public long Time;
  public HashSet<Entity> Targets = new HashSet();
  
  public FireflyData(Player player)
  {
    this.Player = player;
    this.Location = player.getLocation();
    this.Time = System.currentTimeMillis();
  }
}
