package nautilus.game.arcade.kit.perks.data;

import java.util.HashSet;
import org.bukkit.entity.Player;


public class ReboundData
{
  public Player Shooter;
  public HashSet<Player> Ignore = new HashSet();
  public int Bounces;
  
  public ReboundData(Player shooter, int bounces, HashSet<Player> previousIgnore)
  {
    this.Shooter = shooter;
    this.Bounces = bounces;
    
    if (previousIgnore != null) {
      this.Ignore = previousIgnore;
    }
    this.Ignore.add(shooter);
  }
}
