package mineplex.core.disguise.disguises;

import org.bukkit.entity.Entity;

public class DisguiseChicken extends DisguiseAnimal {
  public DisguiseChicken(Entity entity) {
    super(entity);
  }
  

  protected int GetEntityTypeId()
  {
    return 93;
  }
  
  public String getHurtSound()
  {
    return "mob.chicken.hurt";
  }
}
