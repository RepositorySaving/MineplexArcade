package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseBat extends DisguiseAnimal {
  public DisguiseBat(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(16, new Byte((byte)0));
  }
  
  public boolean isSitting()
  {
    return (this.DataWatcher.getByte(16) & 0x1) != 0;
  }
  
  public void setSitting(boolean paramBoolean)
  {
    int i = this.DataWatcher.getByte(16);
    if (paramBoolean) {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(i | 0x1)));
    } else {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(i & 0xFFFFFFFE)));
    }
  }
  
  protected int GetEntityTypeId()
  {
    return 65;
  }
  
  public String getHurtSound()
  {
    return "mob.bat.hurt";
  }
}
