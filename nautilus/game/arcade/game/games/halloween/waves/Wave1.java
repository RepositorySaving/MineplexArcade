package nautilus.game.arcade.game.games.halloween.waves;

import java.util.ArrayList;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.halloween.creatures.MobSkeletonWarrior;

public class Wave1 extends WaveBase
{
  public Wave1(Halloween host)
  {
    super(host, "Skeletons? Farmers? FARMER SKELETONS!!!", 60000L, host.GetSpawnSet(1));
  }
  

  public void Spawn(int tick)
  {
    if (UtilTime.elapsed(this._start, 30000L)) {
      return;
    }
    if (this.Host.GetCreatures().size() > this.Host.GetMaxMobs()) {
      return;
    }
    if (tick % 10 == 0) {
      this.Host.AddCreature(new MobSkeletonWarrior(this.Host, GetSpawn()));
    }
    if (tick % 20 == 0) {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobSkeletonArcher(this.Host, GetSpawn()));
    }
  }
}
