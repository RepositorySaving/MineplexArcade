package mineplex.core.disguise;

import mineplex.core.disguise.disguises.DisguiseZombie;
import org.bukkit.entity.Entity;


public class DisguiseFactory
{
  protected DisguiseZombie DisguiseZombie(Entity entity)
  {
    return new DisguiseZombie(entity);
  }
}
