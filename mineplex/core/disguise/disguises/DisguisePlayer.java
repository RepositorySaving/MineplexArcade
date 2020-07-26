package mineplex.core.disguise.disguises;

import java.util.UUID;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class DisguisePlayer
  extends DisguiseHuman
{
  private String _name;
  
  public DisguisePlayer(org.bukkit.entity.Entity entity, String name)
  {
    super(entity);
    
    if (name.length() > 16)
    {
      name = name.substring(0, 16);
    }
    
    this._name = name;
  }
  

  public Packet GetSpawnPacket()
  {
    PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
    packet.a = this.Entity.getId();
    packet.b = new GameProfile(UUID.randomUUID(), this._name);
    packet.c = MathHelper.floor(this.Entity.locX * 32.0D);
    packet.d = MathHelper.floor(this.Entity.locY * 32.0D);
    packet.e = MathHelper.floor(this.Entity.locZ * 32.0D);
    packet.f = ((byte)(int)(this.Entity.yaw * 256.0F / 360.0F));
    packet.g = ((byte)(int)(this.Entity.pitch * 256.0F / 360.0F));
    packet.i = this.DataWatcher;
    
    return packet;
  }
}
