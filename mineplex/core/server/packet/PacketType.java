package mineplex.core.server.packet;

import java.io.DataInputStream;
import mineplex.core.common.util.NautHashMap;
import org.bukkit.event.Event;



public enum PacketType
{
  ServerReady((short)61, ServerReadyPacket.class), 
  PlayerGameRequest((short)71, PlayerGameRequestPacket.class), 
  PlayerServerAssignment((short)72, PlayerServerAssignmentPacket.class), 
  GameReady((short)73, GameReadyPacket.class), 
  PlayerVote((short)81, PlayerVotePacket.class);
  

  private short _packetId;
  private Class<? extends Packet> _packetClass;
  private static NautHashMap<Short, PacketType> _typeMapping;
  
  private PacketType(short id, Class<? extends Packet> packetClass)
  {
    this._packetId = id;
    this._packetClass = packetClass;
  }
  
  public short GetPacketId()
  {
    return this._packetId;
  }
  
  public Class<? extends Packet> GetPacketClass()
  {
    return this._packetClass;
  }
  
  public static Event GetPacketEventById(short id, DataInputStream dataInputStream) throws Exception
  {
    if (_typeMapping == null)
    {
      InitializeMapping();
    }
    
    if (!_typeMapping.containsKey(Short.valueOf(id)))
    {
      throw new Exception("Invalid packet id");
    }
    
    Class<? extends Packet> packetClass = ((PacketType)_typeMapping.get(Short.valueOf(id))).GetPacketClass();
    
    Packet newPacket = (Packet)packetClass.newInstance();
    
    newPacket.ParseStream(dataInputStream);
    
    return newPacket.GetEvent();
  }
  
  private static void InitializeMapping()
  {
    _typeMapping = new NautHashMap();
    
    for (PacketType packetType : values())
    {
      _typeMapping.put(Short.valueOf(packetType.GetPacketId()), packetType);
    }
  }
}
