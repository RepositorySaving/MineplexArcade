package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public abstract class DisguiseTameableAnimal extends DisguiseAnimal {
  public DisguiseTameableAnimal(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(16, Byte.valueOf((byte)0));
    this.DataWatcher.a(17, "");
  }
  
  public boolean isTamed()
  {
    return (this.DataWatcher.getByte(16) & 0x4) != 0;
  }
  
  public void setTamed(boolean tamed)
  {
    int i = this.DataWatcher.getByte(16);
    
    if (tamed) {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(i | 0x4)));
    } else {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(i | 0xFFFFFFFB)));
    }
  }
  
  public boolean isSitting() {
    return (this.DataWatcher.getByte(16) & 0x1) != 0;
  }
  
  public void setSitting(boolean sitting)
  {
    int i = this.DataWatcher.getByte(16);
    
    if (sitting) {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(i | 0x1)));
    } else {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(i | 0xFFFFFFFE)));
    }
  }
  
  public void setOwnerName(String name) {
    this.DataWatcher.watch(17, name);
  }
  
  public String getOwnerName()
  {
    return this.DataWatcher.getString(17);
  }
}
