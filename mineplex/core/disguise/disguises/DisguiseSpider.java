package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseSpider extends DisguiseMonster {
  public DisguiseSpider(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(16, new Byte((byte)0));
  }
  
  public boolean bT()
  {
    return (this.DataWatcher.getByte(16) & 0x1) != 0;
  }
  
  public void a(boolean flag)
  {
    byte b0 = this.DataWatcher.getByte(16);
    
    if (flag) {
      b0 = (byte)(b0 | 0x1);
    } else {
      b0 = (byte)(b0 & 0xFFFFFFFE);
    }
    this.DataWatcher.watch(16, Byte.valueOf(b0));
  }
  

  protected int GetEntityTypeId()
  {
    return 52;
  }
  
  protected String getHurtSound()
  {
    return "mob.spider.say";
  }
}
