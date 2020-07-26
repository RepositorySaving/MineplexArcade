package nautilus.game.arcade.game.games.evolution;

import org.bukkit.entity.Player;

public class EvoScore
{
  public Player Player;
  public int Kills;
  
  public EvoScore(Player player, int i)
  {
    this.Player = player;
    this.Kills = i;
  }
}
