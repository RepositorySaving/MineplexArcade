package mineplex.core.disguise.disguises;

import org.bukkit.entity.Entity;

public class DisguiseSnowman extends DisguiseGolem {
  public DisguiseSnowman(Entity entity) {
    super(entity);
  }
  

  protected int GetEntityTypeId()
  {
    return 97;
  }
}
