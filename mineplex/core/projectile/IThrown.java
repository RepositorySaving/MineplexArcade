package mineplex.core.projectile;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public abstract interface IThrown
{
  public abstract void Collide(LivingEntity paramLivingEntity, Block paramBlock, ProjectileUser paramProjectileUser);
  
  public abstract void Idle(ProjectileUser paramProjectileUser);
  
  public abstract void Expire(ProjectileUser paramProjectileUser);
}
