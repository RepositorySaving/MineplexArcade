package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseWitch extends DisguiseMonster {
  public DisguiseWitch(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(21, Byte.valueOf((byte)0));
  }
  

  protected int GetEntityTypeId()
  {
    return 66;
  }
  
  public String getHurtSound()
  {
    return "mob.witch.hurt";
  }
  
  public void a(boolean flag)
  {
    this.DataWatcher.watch(21, Byte.valueOf((byte)(flag ? 1 : 0)));
  }
  
  public boolean bT()
  {
    return this.DataWatcher.getByte(21) == 1;
  }
}
