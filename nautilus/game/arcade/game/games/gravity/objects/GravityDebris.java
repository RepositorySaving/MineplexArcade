package nautilus.game.arcade.game.games.gravity.objects;

import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import nautilus.game.arcade.game.games.gravity.Gravity;
import nautilus.game.arcade.game.games.gravity.GravityObject;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class GravityDebris
  extends GravityObject
{
  public GravityDebris(Gravity host, Entity ent, double mass, Vector vel)
  {
    super(host, ent, mass, 2.0D, vel);
    
    this.CollideDelay = (System.currentTimeMillis() + 500L);
  }
  

  public void CustomCollide(GravityObject other)
  {
    this.Ent.remove();
    UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, this.Ent.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
  }
  
  public boolean CanCollide(GravityObject other)
  {
    return !(other instanceof GravityDebris);
  }
}
