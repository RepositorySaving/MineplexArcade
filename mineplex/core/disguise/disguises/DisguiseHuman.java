package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public abstract class DisguiseHuman extends DisguiseLiving {
  public DisguiseHuman(org.bukkit.entity.Entity entity) {
    super(entity);
    
    this.DataWatcher.a(16, Byte.valueOf((byte)0));
    this.DataWatcher.a(17, Float.valueOf(0.0F));
    this.DataWatcher.a(18, Integer.valueOf(0));
  }
}
