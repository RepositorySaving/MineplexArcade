package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderGather;

public class GatherSand extends OrderGather
{
  public GatherSand(MineWare host)
  {
    super(host, "Pick up 16 Sand", 12, -1, 16);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
