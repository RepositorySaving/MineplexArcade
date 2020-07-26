package nautilus.game.arcade.game.games.halloween.creatures;

import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Ghast;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;

public class MobGhast
  extends CreatureBase<Ghast>
{
  public MobGhast(Game game, Location loc)
  {
    super(game, null, Ghast.class, loc);
  }
  

  public void SpawnCustom(Ghast ent)
  {
    ent.setMaxHealth(80.0D);
    ent.setHealth(80.0D);
    
    ent.setCustomName("Ghast");
  }
  

  public void Damage(CustomDamageEvent event)
  {
    if (event.GetCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
      event.SetCancelled("Suffocation Cancel");
    }
  }
  


  public void Target(EntityTargetEvent event) {}
  


  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.SLOW) {
      Teleport();
    }
  }
  
  private void Teleport() {
    Location loc = ((Ghast)GetEntity()).getLocation();
    loc.setY(30.0D);
    loc.setX(0.0D);
    loc.setZ(0.0D);
    
    if ((UtilMath.offset2d(((Ghast)GetEntity()).getLocation(), loc) > 50.0D) || (((Ghast)GetEntity()).getLocation().getY() > 80.0D))
    {
      loc.setY(30.0D + 20.0D * Math.random());
      loc.setX(60.0D * Math.random() - 30.0D);
      loc.setZ(60.0D * Math.random() - 30.0D);
      ((Ghast)GetEntity()).teleport(loc);
    }
  }
}
