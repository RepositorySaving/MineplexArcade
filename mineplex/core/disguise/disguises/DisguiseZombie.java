package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;

public class DisguiseZombie extends DisguiseMonster
{
  public DisguiseZombie(org.bukkit.entity.Entity entity)
  {
    super(entity);
    
    this.DataWatcher.a(12, Byte.valueOf((byte)0));
    this.DataWatcher.a(13, Byte.valueOf((byte)0));
    this.DataWatcher.a(14, Byte.valueOf((byte)0));
  }
  

  protected int GetEntityTypeId()
  {
    return 54;
  }
  
  public boolean IsBaby()
  {
    return this.DataWatcher.getByte(12) == 1;
  }
  
  public void SetBaby(boolean baby)
  {
    this.DataWatcher.watch(12, Byte.valueOf((byte)(baby ? 1 : 0)));
  }
  
  public boolean IsVillager()
  {
    return this.DataWatcher.getByte(13) == 1;
  }
  
  public void SetVillager(boolean villager)
  {
    this.DataWatcher.watch(13, Byte.valueOf((byte)(villager ? 1 : 0)));
  }
  
  protected String getHurtSound()
  {
    return "mob.zombie.hurt";
  }
}
