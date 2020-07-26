package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderCraft;

public class CraftLadder extends OrderCraft
{
  public CraftLadder(MineWare host)
  {
    super(host, "Craft some ladders", 65, -1, 1);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
