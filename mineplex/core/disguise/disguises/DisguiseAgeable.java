package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public abstract class DisguiseAgeable extends DisguiseCreature {
  public DisguiseAgeable(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(12, new Integer(0));
  }
  
  public boolean isBaby()
  {
    return this.DataWatcher.getInt(12) < 0;
  }
  
  public void setBaby()
  {
    this.DataWatcher.watch(12, Integer.valueOf(-24000));
  }
}
