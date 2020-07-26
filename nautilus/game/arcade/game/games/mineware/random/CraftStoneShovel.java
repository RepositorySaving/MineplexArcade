package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderCraft;

public class CraftStoneShovel extends OrderCraft
{
  public CraftStoneShovel(MineWare host)
  {
    super(host, "Craft a stone shovel", 273, -1, 1);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
