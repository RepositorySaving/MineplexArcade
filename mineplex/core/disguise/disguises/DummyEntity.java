package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.World;

public class DummyEntity extends Entity
{
  public DummyEntity(World world)
  {
    super(world);
  }
  
  protected void c() {}
  
  protected void a(NBTTagCompound nbttagcompound) {}
  
  protected void b(NBTTagCompound nbttagcompound) {}
}
