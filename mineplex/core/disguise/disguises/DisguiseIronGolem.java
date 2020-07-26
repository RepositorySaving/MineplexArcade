package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseIronGolem extends DisguiseGolem {
  public DisguiseIronGolem(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(16, Byte.valueOf((byte)0));
  }
  

  protected int GetEntityTypeId()
  {
    return 99;
  }
  
  public boolean bW()
  {
    return (this.DataWatcher.getByte(16) & 0x1) != 0;
  }
  
  public void setPlayerCreated(boolean flag)
  {
    byte b0 = this.DataWatcher.getByte(16);
    
    if (flag) {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(b0 | 0x1)));
    } else {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(b0 & 0xFFFFFFFE)));
    }
  }
  
  protected String getHurtSound() {
    return "mob.irongolem.hit";
  }
}
