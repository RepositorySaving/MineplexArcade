package mineplex.core.fakeEntity;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.packethandler.PacketHandler;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public class FakeEntityManager
  extends MiniPlugin
{
  public static FakeEntityManager Instance;
  private PacketHandler _packetHandler;
  private NautHashMap<String, List<FakeEntity>> _playerFakeEntityMap;
  
  public FakeEntityManager(JavaPlugin plugin)
  {
    super("Fake Entity Manager", plugin);
    
    this._playerFakeEntityMap = new NautHashMap();
  }
  
  public static void Initialize(JavaPlugin plugin)
  {
    Instance = new FakeEntityManager(plugin);
  }
  
  public void AddFakeEntity(FakeEntity entity, String name)
  {
    if (!this._playerFakeEntityMap.containsKey(name))
    {
      this._playerFakeEntityMap.put(name, new ArrayList());
    }
    
    ((List)this._playerFakeEntityMap.get(name)).add(entity);
  }
  
  public void ClearFakes(String name)
  {
    this._playerFakeEntityMap.remove(name);
  }
  
  public void ClearFakeFor(FakeEntity entity, String name)
  {
    if (!this._playerFakeEntityMap.containsKey(name))
    {
      this._playerFakeEntityMap.put(name, new ArrayList());
    }
    
    ((List)this._playerFakeEntityMap.get(name)).remove(entity);
  }
  
  public List<FakeEntity> GetFakesFor(String name)
  {
    if (!this._playerFakeEntityMap.containsKey(name))
    {
      this._playerFakeEntityMap.put(name, new ArrayList());
    }
    
    return (List)this._playerFakeEntityMap.get(name);
  }
  
  public void SetPacketHandler(PacketHandler packetHandler)
  {
    this._packetHandler = packetHandler;
  }
  
  public void RemoveForward(Player viewer)
  {
    this._packetHandler.RemoveForward(viewer);
  }
  
  public void ForwardMovement(Player viewer, Player traveller, int entityId)
  {
    this._packetHandler.ForwardMovement(viewer, traveller.getEntityId(), entityId);
  }
  
  public void BlockMovement(Player otherPlayer, int entityId)
  {
    this._packetHandler.BlockMovement(otherPlayer, entityId);
  }
  
  public void FakePassenger(Player viewer, int entityId, Packet attachPacket)
  {
    this._packetHandler.FakePassenger(viewer, entityId, attachPacket);
  }
  
  public void RemoveFakePassenger(Player viewer, int entityId)
  {
    this._packetHandler.RemoveFakePassenger(viewer, entityId);
  }
  
  public void FakeVehicle(Player viewer, int entityId, Packet attachPacket)
  {
    this._packetHandler.FakeVehicle(viewer, entityId, attachPacket);
  }
  
  public void RemoveFakeVehicle(Player viewer, int entityId)
  {
    this._packetHandler.RemoveFakeVehicle(viewer, entityId);
  }
  
  public void SendPacketTo(Packet packet, Player player)
  {
    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
  }
}
