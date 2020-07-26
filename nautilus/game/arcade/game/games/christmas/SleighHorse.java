package nautilus.game.arcade.game.games.christmas;

import mineplex.core.common.util.UtilEnt;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.LivingEntity;

public class SleighHorse
{
  public Horse Ent;
  public double OffsetX;
  public double OffsetZ;
  
  public SleighHorse(Location loc, double x, double z)
  {
    this.Ent = ((Horse)loc.getWorld().spawn(loc.add(x, 0.0D, z), Horse.class));
    
    UtilEnt.Vegetate(this.Ent);
    UtilEnt.ghost(this.Ent, true, false);
    
    this.Ent.setStyle(Horse.Style.WHITE_DOTS);
    this.Ent.setColor(Horse.Color.CHESTNUT);
    
    this.OffsetX = x;
    this.OffsetZ = z;
  }
  
  public boolean HasEntity(LivingEntity ent)
  {
    if (this.Ent.equals(ent)) {
      return true;
    }
    return false;
  }
}
