package nautilus.game.arcade.game.games.halloween.waves;

import mineplex.core.common.util.UtilServer;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.halloween.creatures.CreatureBase;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WaveVictory extends WaveBase
{
  public WaveVictory(Halloween host)
  {
    super(host, "Celebration!", 15000L, host.GetSpawnSet(3));
  }
  

  public void Spawn(int tick)
  {
    if (mineplex.core.common.util.UtilTime.elapsed(this._start, 20000L)) {
      return;
    }
    
    if (tick == 0) {
      for (Player player : UtilServer.getPlayers()) {
        player.playEffect((Location)this.Host.WorldData.GetDataLocs("BLACK").get(0), Effect.RECORD_PLAY, 2259);
      }
    }
    for (CreatureBase mob : this.Host.GetCreatures()) {
      mob.GetEntity().damage(5.0D);
    }
    
    if (this.Host.WorldTimeSet != 6000)
    {
      this.Host.WorldTimeSet = ((this.Host.WorldTimeSet + 50) % 24000);
      this.Host.WorldData.World.setTime(this.Host.WorldTimeSet);
    }
  }
}
