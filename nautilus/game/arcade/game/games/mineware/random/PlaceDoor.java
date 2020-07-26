package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.OrderPlace;

public class PlaceDoor extends OrderPlace
{
  public PlaceDoor(MineWare host)
  {
    super(host, "Place a wooden door", 64, -1, 1);
  }
  
  public void Initialize() {}
  
  public void Uninitialize() {}
}
