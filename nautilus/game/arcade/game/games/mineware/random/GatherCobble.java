package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderGather;

public class GatherCobble extends OrderGather
{
  public GatherCobble(MineWare host)
  {
    super(host, "Pick up 10 Cobblestone", 4, -1, 10);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
