package nautilus.game.arcade.game.games.halloween.waves;

import java.util.ArrayList;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.halloween.creatures.MobGhast;
import nautilus.game.arcade.game.games.halloween.creatures.MobPigZombie;
import org.bukkit.Location;

public class Wave4 extends WaveBase
{
  public Wave4(Halloween host)
  {
    super(host, "Ghasts and friends!", 80000L, host.GetSpawnSet(3));
  }
  

  public void Spawn(int tick)
  {
    if (UtilTime.elapsed(this._start, 30000L)) {
      return;
    }
    if ((tick > 0) && (tick % 70 == 0))
    {
      Location loc = GetSpawn();
      loc.setY(30.0D + 20.0D * Math.random());
      loc.setX(80.0D * Math.random() - 40.0D);
      loc.setZ(80.0D * Math.random() - 40.0D);
      this.Host.AddCreature(new MobGhast(this.Host, loc));
    }
    
    if (this.Host.GetCreatures().size() > this.Host.GetMaxMobs()) {
      return;
    }
    if (tick % 10 == 0) {
      this.Host.AddCreature(new MobPigZombie(this.Host, GetSpawn()));
    }
  }
}
