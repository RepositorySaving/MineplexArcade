package mineplex.core.disguise.disguises;

import org.bukkit.entity.Entity;

public class DisguisePig extends DisguiseAnimal {
  public DisguisePig(Entity entity) {
    super(entity);
  }
  

  protected int GetEntityTypeId()
  {
    return 90;
  }
  
  public String getHurtSound()
  {
    return "mob.pig.say";
  }
}
