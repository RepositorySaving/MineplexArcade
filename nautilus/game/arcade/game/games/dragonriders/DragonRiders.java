package nautilus.game.arcade.game.games.dragonriders;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.dragonriders.kits.KitRider;
import nautilus.game.arcade.kit.Kit;








public class DragonRiders
  extends SoloGame
{
  public DragonRiders(ArcadeManager manager)
  {
    super(manager, GameType.DragonRiders, new Kit[] {new KitRider(manager) }, new String[0]);
    


    this.Damage = false;
    this.HungerSet = 20;
  }
}
