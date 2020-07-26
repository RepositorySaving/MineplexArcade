package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.EnumEntitySize;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;

public class DisguiseSlime extends DisguiseInsentient
{
  public DisguiseSlime(org.bukkit.entity.Entity entity)
  {
    super(entity);
    
    this.DataWatcher.a(16, new Byte((byte)1));
  }
  
  public void SetSize(int i)
  {
    this.DataWatcher.watch(16, new Byte((byte)i));
  }
  
  public int GetSize()
  {
    return this.DataWatcher.getByte(16);
  }
  
  public Packet GetSpawnPacket()
  {
    PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
    packet.a = this.Entity.getId();
    packet.b = 55;
    packet.c = EnumEntitySize.SIZE_2.a(this.Entity.locX);
    packet.d = net.minecraft.server.v1_7_R3.MathHelper.floor(this.Entity.locY * 32.0D);
    packet.e = EnumEntitySize.SIZE_2.a(this.Entity.locZ);
    packet.i = ((byte)(int)(this.Entity.yaw * 256.0F / 360.0F));
    packet.j = ((byte)(int)(this.Entity.pitch * 256.0F / 360.0F));
    packet.k = ((byte)(int)(this.Entity.yaw * 256.0F / 360.0F));
    
    double var2 = 3.9D;
    double var4 = 0.0D;
    double var6 = 0.0D;
    double var8 = 0.0D;
    
    if (var4 < -var2)
    {
      var4 = -var2;
    }
    
    if (var6 < -var2)
    {
      var6 = -var2;
    }
    
    if (var8 < -var2)
    {
      var8 = -var2;
    }
    
    if (var4 > var2)
    {
      var4 = var2;
    }
    
    if (var6 > var2)
    {
      var6 = var2;
    }
    
    if (var8 > var2)
    {
      var8 = var2;
    }
    
    packet.f = ((int)(var4 * 8000.0D));
    packet.g = ((int)(var6 * 8000.0D));
    packet.h = ((int)(var8 * 8000.0D));
    packet.l = this.DataWatcher;
    packet.m = this.DataWatcher.b();
    
    return packet;
  }
  
  protected String getHurtSound()
  {
    return "mob.slime." + (GetSize() > 1 ? "big" : "small");
  }
  
  protected float getVolume()
  {
    return 0.4F * GetSize();
  }
}
