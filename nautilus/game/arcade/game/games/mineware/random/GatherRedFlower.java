package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderGather;

public class GatherRedFlower extends OrderGather
{
  public GatherRedFlower(MineWare host)
  {
    super(host, "Pick 3 Red Roses", 38, -1, 3);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
