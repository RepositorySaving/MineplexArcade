package nautilus.game.arcade.game.games.halloween.waves;

import java.util.ArrayList;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.halloween.creatures.MobGiant;
import nautilus.game.arcade.game.games.halloween.creatures.MobZombie;

public class Wave2 extends WaveBase
{
  public Wave2(Halloween host)
  {
    super(host, "A GIANT!? Better kill that guy fast!", 65000L, host.GetSpawnSet(0));
  }
  

  public void Spawn(int tick)
  {
    if (UtilTime.elapsed(this._start, 30000L)) {
      return;
    }
    if (tick == 0) {
      this.Host.AddCreature(new MobGiant(this.Host, GetSpawn()));
    }
    if (this.Host.GetCreatures().size() > this.Host.GetMaxMobs()) {
      return;
    }
    if (tick % 10 == 0) {
      this.Host.AddCreature(new MobZombie(this.Host, GetSpawn()));
    }
    if (tick % 25 == 0) {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobCreeper(this.Host, GetSpawn()));
    }
  }
}
