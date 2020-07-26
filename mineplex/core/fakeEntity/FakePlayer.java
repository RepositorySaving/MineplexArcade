package mineplex.core.fakeEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.EntitySlime;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.EntityType;


public class FakePlayer
  extends FakeEntity
{
  private String _name;
  private static Field _spawnDataWatcherField;
  
  public FakePlayer(String name, Location location)
  {
    super(EntityType.PLAYER, location);
    
    this._name = name;
  }
  
  public Packet Spawn(int id)
  {
    PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
    packet.a = id;
    packet.b = new GameProfile(UUID.randomUUID(), this._name);
    packet.c = MathHelper.floor(GetLocation().getX() * 32.0D);
    packet.d = MathHelper.floor(GetLocation().getY() * 32.0D);
    packet.e = MathHelper.floor(GetLocation().getZ() * 32.0D);
    packet.f = ((byte)(int)(GetLocation().getYaw() * 256.0F / 360.0F));
    packet.g = ((byte)(int)(GetLocation().getPitch() * 256.0F / 360.0F));
    
    DataWatcher dataWatcher = new DataWatcher(new EntitySlime(((CraftWorld)Bukkit.getWorlds().get(0)).getHandle()));
    
    UpdateDataWatcher(dataWatcher);
    
    packet.i = dataWatcher;
    
    return packet;
  }
}
