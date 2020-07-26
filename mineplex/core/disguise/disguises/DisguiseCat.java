package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseCat extends DisguiseTameableAnimal {
  public DisguiseCat(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(18, Byte.valueOf((byte)0));
  }
  
  public int getCatType()
  {
    return this.DataWatcher.getByte(18);
  }
  
  public void setCatType(int i)
  {
    this.DataWatcher.watch(18, Byte.valueOf((byte)i));
  }
  

  protected int GetEntityTypeId()
  {
    return 98;
  }
  
  protected String getHurtSound()
  {
    return "mob.cat.hitt";
  }
}
