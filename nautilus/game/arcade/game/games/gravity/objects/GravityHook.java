package nautilus.game.arcade.game.games.gravity.objects;

import nautilus.game.arcade.game.games.gravity.Gravity;
import nautilus.game.arcade.game.games.gravity.GravityObject;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class GravityHook extends GravityObject
{
  public GravityHook(Gravity host, Entity ent, double mass, Vector vel)
  {
    super(host, ent, mass, 1.0D, vel);
  }
  

  public void PlayCollideSound(double power)
  {
    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.IRONGOLEM_HIT, 1.0F, 2.0F);
  }
}
