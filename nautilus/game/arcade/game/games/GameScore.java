package nautilus.game.arcade.game.games;

import org.bukkit.entity.Player;

public class GameScore
{
  public Player Player;
  public double Score;
  
  public GameScore(Player player, double i)
  {
    this.Player = player;
    this.Score = i;
  }
}
