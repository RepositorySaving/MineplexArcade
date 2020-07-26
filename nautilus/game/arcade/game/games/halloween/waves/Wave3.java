package nautilus.game.arcade.game.games.halloween.waves;

import java.util.ArrayList;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.halloween.creatures.MobSpiderLeaper;

public class Wave3 extends WaveBase
{
  public Wave3(Halloween host)
  {
    super(host, "Spiders Spiders Spiders!", 70000L, host.GetSpawnSet(2));
  }
  

  public void Spawn(int tick)
  {
    if (this.Host.GetCreatures().size() > this.Host.GetMaxMobs()) {
      return;
    }
    if ((tick > 200) && (tick % 10 == 0) && (!UtilTime.elapsed(this._start, 35000L))) {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobSpiderSmasher(this.Host, GetSpawn()));
    }
    if ((tick % 8 == 0) && (!UtilTime.elapsed(this._start, 25000L))) {
      this.Host.AddCreature(new MobSpiderLeaper(this.Host, GetSpawn()));
    }
  }
}
