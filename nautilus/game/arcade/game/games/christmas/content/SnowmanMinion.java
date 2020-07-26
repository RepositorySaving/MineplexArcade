package nautilus.game.arcade.game.games.christmas.content;

import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilTime;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;




public class SnowmanMinion
{
  public Snowman Ent;
  public Player Target;
  public Location OrbitLocation;
  public long StackDelay;
  public long AttackDelay;
  
  public SnowmanMinion(Snowman ent)
  {
    this.Ent = ent;
    UtilEnt.Vegetate(this.Ent);
    this.Ent.setMaxHealth(30.0D);
    this.Ent.setHealth(this.Ent.getMaxHealth());
    
    this.StackDelay = 0L;
    this.AttackDelay = System.currentTimeMillis();
  }
  
  public boolean CanStack()
  {
    return UtilTime.elapsed(this.StackDelay, 8000L);
  }
}
