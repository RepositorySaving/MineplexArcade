package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseWolf extends DisguiseTameableAnimal
{
  public DisguiseWolf(org.bukkit.entity.Entity entity)
  {
    super(entity);
    
    this.DataWatcher.a(18, new Float(20.0F));
    this.DataWatcher.a(19, new Byte((byte)0));
    this.DataWatcher.a(20, new Byte((byte)net.minecraft.server.v1_7_R3.BlockCloth.b(1)));
  }
  
  public boolean isAngry()
  {
    return (this.DataWatcher.getByte(16) & 0x2) != 0;
  }
  
  public void setAngry(boolean angry)
  {
    byte b0 = this.DataWatcher.getByte(16);
    
    if (angry) {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(b0 | 0x2)));
    } else {
      this.DataWatcher.watch(16, Byte.valueOf((byte)(b0 & 0xFFFFFFFD)));
    }
  }
  
  public int getCollarColor() {
    return this.DataWatcher.getByte(20) & 0xF;
  }
  
  public void setCollarColor(int i)
  {
    this.DataWatcher.watch(20, Byte.valueOf((byte)(i & 0xF)));
  }
  
  public void m(boolean flag)
  {
    if (flag) {
      this.DataWatcher.watch(19, Byte.valueOf((byte)1));
    } else {
      this.DataWatcher.watch(19, Byte.valueOf((byte)0));
    }
  }
  
  public boolean ce() {
    return this.DataWatcher.getByte(19) == 1;
  }
  

  protected int GetEntityTypeId()
  {
    return 95;
  }
  
  protected String getHurtSound()
  {
    return "mob.wolf.hurt";
  }
}
