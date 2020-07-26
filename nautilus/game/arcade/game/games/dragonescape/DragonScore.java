package nautilus.game.arcade.game.games.dragonescape;

import org.bukkit.entity.Player;

public class DragonScore
{
  public Player Player;
  public double Score;
  
  public DragonScore(Player player, double i)
  {
    this.Player = player;
    this.Score = i;
  }
}
