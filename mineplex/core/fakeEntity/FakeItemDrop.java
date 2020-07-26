package mineplex.core.fakeEntity;

import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;


public class FakeItemDrop
  extends FakeEntity
{
  private net.minecraft.server.v1_7_R3.ItemStack _itemStack;
  
  public FakeItemDrop(org.bukkit.inventory.ItemStack itemStack, Location location)
  {
    super(EntityType.DROPPED_ITEM, location);
    
    this._itemStack = CraftItemStack.asNMSCopy(itemStack);
  }
  
  public Packet Spawn()
  {
    PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity();
    packet.a = GetEntityId();
    packet.b = MathHelper.floor(GetLocation().getX() * 32.0D);
    packet.c = MathHelper.floor(GetLocation().getY() * 32.0D);
    packet.d = MathHelper.floor(GetLocation().getZ() * 32.0D);
    packet.h = MathHelper.d(GetLocation().getYaw() * 256.0F / 360.0F);
    packet.i = MathHelper.d(GetLocation().getPitch() * 256.0F / 360.0F);
    packet.j = 2;
    packet.k = 1;
    
    double d0 = 0.0D;
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 3.9D;
    
    if (d0 < -d3) {
      d0 = -d3;
    }
    
    if (d1 < -d3) {
      d1 = -d3;
    }
    
    if (d2 < -d3) {
      d2 = -d3;
    }
    
    if (d0 > d3) {
      d0 = d3;
    }
    
    if (d1 > d3) {
      d1 = d3;
    }
    
    if (d2 > d3) {
      d2 = d3;
    }
    
    packet.e = ((int)(d0 * 8000.0D));
    packet.f = ((int)(d1 * 8000.0D));
    packet.g = ((int)(d2 * 8000.0D));
    
    return packet;
  }
  
  protected void UpdateDataWatcher(DataWatcher dataWatcher)
  {
    dataWatcher.a(0, Byte.valueOf((byte)0));
    dataWatcher.a(1, Short.valueOf((short)300));
    dataWatcher.a(8, Integer.valueOf(0));
    dataWatcher.a(9, Byte.valueOf((byte)0));
    dataWatcher.a(10, new net.minecraft.server.v1_7_R3.ItemStack(this._itemStack.getItem(), this._itemStack.count));
  }
  
  public void SetItemStack(org.bukkit.inventory.ItemStack itemStack)
  {
    this._itemStack = CraftItemStack.asNMSCopy(itemStack);
  }
}
