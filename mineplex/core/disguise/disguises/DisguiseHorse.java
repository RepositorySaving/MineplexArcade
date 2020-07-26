package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseHorse extends DisguiseAnimal
{
  public DisguiseHorse(org.bukkit.entity.Entity entity)
  {
    super(entity);
    
    this.DataWatcher.a(16, Integer.valueOf(0));
    this.DataWatcher.a(19, Byte.valueOf((byte)0));
    this.DataWatcher.a(20, Integer.valueOf(0));
    this.DataWatcher.a(21, String.valueOf(""));
    this.DataWatcher.a(22, Integer.valueOf(0));
  }
  

  protected int GetEntityTypeId()
  {
    return 100;
  }
  
  public void setType(org.bukkit.entity.Horse.Variant horseType)
  {
    this.DataWatcher.watch(19, Byte.valueOf((byte)horseType.ordinal()));
  }
  
  public org.bukkit.entity.Horse.Variant getType()
  {
    return org.bukkit.entity.Horse.Variant.values()[this.DataWatcher.getByte(19)];
  }
  
  public void setVariant(org.bukkit.entity.Horse.Color color)
  {
    this.DataWatcher.watch(20, Integer.valueOf(color.ordinal()));
  }
  
  public org.bukkit.entity.Horse.Color getVariant()
  {
    return org.bukkit.entity.Horse.Color.values()[this.DataWatcher.getInt(20)];
  }
  
  private boolean w(int i)
  {
    return (this.DataWatcher.getInt(16) & i) != 0;
  }
  
  public void kick()
  {
    b(32, false);
    b(64, true);
  }
  
  public void stopKick()
  {
    b(64, false);
  }
  
  private void b(int i, boolean flag)
  {
    int j = this.DataWatcher.getInt(16);
    
    if (flag) {
      this.DataWatcher.watch(16, Integer.valueOf(j | i));
    } else {
      this.DataWatcher.watch(16, Integer.valueOf(j & (i ^ 0xFFFFFFFF)));
    }
  }
  
  public String getOwnerName() {
    return this.DataWatcher.getString(21);
  }
  
  public void setOwnerName(String s)
  {
    this.DataWatcher.watch(21, s);
  }
  
  public int cf()
  {
    return this.DataWatcher.getInt(22);
  }
  
  public void r(int i)
  {
    this.DataWatcher.watch(22, Integer.valueOf(i));
  }
}
