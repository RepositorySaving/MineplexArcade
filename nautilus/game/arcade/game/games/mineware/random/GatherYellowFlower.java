package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderGather;

public class GatherYellowFlower extends OrderGather
{
  public GatherYellowFlower(MineWare host)
  {
    super(host, "Pick 4 Yellow Flowers", 37, -1, 4);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
