package mineplex.core.disguise.disguises;

import org.bukkit.entity.Entity;

public class DisguisePigZombie extends DisguiseZombie {
  public DisguisePigZombie(Entity entity) {
    super(entity);
  }
  

  public int GetEntityTypeId()
  {
    return 57;
  }
  
  protected String getHurtSound()
  {
    return "mob.zombiepig.zpighurt";
  }
}
