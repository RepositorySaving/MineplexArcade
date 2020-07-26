package mineplex.core.fakeEntity;

import java.io.PrintStream;
import net.minecraft.server.v1_7_R3.EnumEntitySize;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeFallingBlock extends FakeEntity
{
  private int _materialId;
  private byte _data;
  
  public FakeFallingBlock(int materialId, byte data, Location location)
  {
    super(EntityType.FALLING_BLOCK, location);
    
    this._materialId = materialId;
    this._data = data;
  }
  
  public Packet Spawn(int id)
  {
    PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity();
    packet.a = id;
    packet.b = EnumEntitySize.SIZE_2.a(GetLocation().getX());
    packet.c = MathHelper.floor(GetLocation().getY() * 32.0D);
    packet.d = EnumEntitySize.SIZE_2.a(GetLocation().getZ());
    
    double var4 = 0.0D;
    double var6 = 0.045D;
    double var8 = 0.0D;
    double var10 = 3.9D;
    
    if (var4 < -var10)
    {
      var4 = -var10;
    }
    
    if (var6 < -var10)
    {
      var6 = -var10;
    }
    
    if (var8 < -var10)
    {
      var8 = -var10;
    }
    
    if (var4 > var10)
    {
      var4 = var10;
    }
    
    if (var6 > var10)
    {
      var6 = var10;
    }
    
    if (var8 > var10)
    {
      var8 = var10;
    }
    
    packet.e = ((int)(var4 * 8000.0D));
    packet.f = ((int)(var6 * 8000.0D));
    packet.g = ((int)(var8 * 8000.0D));
    packet.h = 0;
    packet.i = 0;
    packet.j = 70;
    packet.k = (this._materialId | this._data << 16);
    
    System.out.println("Creating fake falling block with entityId " + GetEntityId());
    
    return packet;
  }
}
