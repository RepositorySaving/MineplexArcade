package nautilus.game.arcade.game.games.milkcow;

import org.bukkit.entity.Player;

public class CowScore
{
  public Player Player;
  public double Score;
  
  public CowScore(Player player, double i)
  {
    this.Player = player;
    this.Score = i;
  }
}
