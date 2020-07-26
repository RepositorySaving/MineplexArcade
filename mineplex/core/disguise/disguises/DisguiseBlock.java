package mineplex.core.disguise.disguises;

import java.util.Random;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;

public class DisguiseBlock
  extends DisguiseBase
{
  private static Random _random = new Random();
  
  private int _blockId;
  private int _blockData;
  
  public DisguiseBlock(org.bukkit.entity.Entity entity, int blockId, int blockData)
  {
    super(entity);
    
    this._blockId = blockId;
    this._blockData = blockData;
  }
  
  public int GetBlockId()
  {
    return this._blockId;
  }
  
  public byte GetBlockData()
  {
    return (byte)this._blockData;
  }
  

  public Packet GetSpawnPacket()
  {
    PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity();
    packet.a = this.Entity.getId();
    packet.b = MathHelper.floor(this.Entity.locX * 32.0D);
    packet.c = MathHelper.floor(this.Entity.locY * 32.0D);
    packet.d = MathHelper.floor(this.Entity.locZ * 32.0D);
    packet.h = MathHelper.d(this.Entity.pitch * 256.0F / 360.0F);
    packet.i = MathHelper.d(this.Entity.yaw * 256.0F / 360.0F);
    packet.j = 70;
    packet.k = (this._blockId | this._blockData << 16);
    
    double d1 = this.Entity.motX;
    double d2 = this.Entity.motY;
    double d3 = this.Entity.motZ;
    double d4 = 3.9D;
    
    if (d1 < -d4) d1 = -d4;
    if (d2 < -d4) d2 = -d4;
    if (d3 < -d4) d3 = -d4;
    if (d1 > d4) d1 = d4;
    if (d2 > d4) d2 = d4;
    if (d3 > d4) { d3 = d4;
    }
    packet.e = ((int)(d1 * 8000.0D));
    packet.f = ((int)(d2 * 8000.0D));
    packet.g = ((int)(d3 * 8000.0D));
    
    return packet;
  }
  
  protected String getHurtSound()
  {
    return "damage.hit";
  }
  
  protected float getVolume()
  {
    return 1.0F;
  }
  
  protected float getPitch()
  {
    return (_random.nextFloat() - _random.nextFloat()) * 0.2F + 1.0F;
  }
}
