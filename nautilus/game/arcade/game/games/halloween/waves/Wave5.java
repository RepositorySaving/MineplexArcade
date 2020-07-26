package nautilus.game.arcade.game.games.halloween.waves;

import java.util.ArrayList;
import mineplex.core.common.util.UtilMath;
import nautilus.game.arcade.game.games.halloween.Halloween;
import org.bukkit.Location;

public class Wave5 extends WaveBase
{
  public Wave5(Halloween host)
  {
    super(host, "Double the Giants! Double the fun!", 80000L, host.GetSpawnSet(1));
  }
  

  public void Spawn(int tick)
  {
    if (mineplex.core.common.util.UtilTime.elapsed(this._start, 30000L)) {
      return;
    }
    if (tick == 0) {
      SpawnBeacons(this.Host.GetSpawnSet(2));
    }
    if (tick == 0)
    {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobGiant(this.Host, (Location)this.Host.GetSpawnSet(1).get(UtilMath.r(this.Host.GetSpawnSet(1).size()))));
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobGiant(this.Host, (Location)this.Host.GetSpawnSet(2).get(UtilMath.r(this.Host.GetSpawnSet(2).size()))));
    }
    
    if (this.Host.GetCreatures().size() > this.Host.GetMaxMobs()) {
      return;
    }
    if (tick % 20 == 0)
    {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobZombie(this.Host, (Location)this.Host.GetSpawnSet(1).get(UtilMath.r(this.Host.GetSpawnSet(1).size()))));
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobZombie(this.Host, (Location)this.Host.GetSpawnSet(2).get(UtilMath.r(this.Host.GetSpawnSet(2).size()))));
    }
    
    if (tick % 60 == 0)
    {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobCreeper(this.Host, (Location)this.Host.GetSpawnSet(1).get(UtilMath.r(this.Host.GetSpawnSet(1).size()))));
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobCreeper(this.Host, (Location)this.Host.GetSpawnSet(2).get(UtilMath.r(this.Host.GetSpawnSet(2).size()))));
    }
  }
}
