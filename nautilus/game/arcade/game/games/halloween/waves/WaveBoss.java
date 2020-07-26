package nautilus.game.arcade.game.games.halloween.waves;

import java.util.ArrayList;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.halloween.creatures.MobZombie;
import nautilus.game.arcade.game.games.halloween.creatures.PumpkinKing;
import org.bukkit.entity.Skeleton;

public class WaveBoss extends WaveBase
{
  private PumpkinKing _king;
  
  public WaveBoss(Halloween host)
  {
    super(host, "The Pumpkin King", 0L, host.GetSpawnSet(0));
  }
  

  public void Spawn(int tick)
  {
    if (tick == 0)
    {
      this._king = new PumpkinKing(this.Host, (org.bukkit.Location)this.Host.WorldData.GetDataLocs("BLACK").get(0));
      this.Host.AddCreature(this._king);
    }
    

    if ((this.Host.GetCreatures().size() < 20 + tick / 200) && (!this._king.IsFinal()))
    {
      if (tick % Math.max(5, 15 - tick / 400) == 0) {
        if (Math.random() > 0.1D) {
          this.Host.AddCreature(new MobZombie(this.Host, this.Host.GetRandomSpawn()));
        } else {
          this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobCreeper(this.Host, this.Host.GetRandomSpawn()));
        }
      }
    }
    if ((tick % 3000 == 0) && (!this._king.IsFinal()))
    {
      this.Host.AddCreature(new nautilus.game.arcade.game.games.halloween.creatures.MobGiant(this.Host, GetSpawn()));
    }
  }
  

  public boolean CanEnd()
  {
    return (this._king == null) || (!((Skeleton)this._king.GetEntity()).isValid());
  }
}
