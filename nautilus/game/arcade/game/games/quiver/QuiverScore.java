package nautilus.game.arcade.game.games.quiver;

import org.bukkit.entity.Player;

public class QuiverScore
{
  public Player Player;
  public int Kills;
  
  public QuiverScore(Player player, int i)
  {
    this.Player = player;
    this.Kills = i;
  }
}
