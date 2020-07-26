package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mineplex.core.server.event.PlayerServerAssignmentEvent;
import org.bukkit.event.Event;


public class PlayerServerAssignmentPacket
  extends Packet
{
  private String _playerName;
  private String _serverName;
  
  public PlayerServerAssignmentPacket() {}
  
  public PlayerServerAssignmentPacket(String playerName, String serverName)
  {
    this._playerName = playerName;
    this._serverName = serverName;
  }
  
  public void ParseStream(DataInputStream inputStream)
    throws IOException
  {
    this._playerName = readString(inputStream, 16);
    this._serverName = readString(inputStream, 16);
  }
  
  public void Write(DataOutputStream dataOutput) throws IOException
  {
    dataOutput.writeShort(72);
    writeString(this._playerName, dataOutput);
    writeString(this._serverName, dataOutput);
  }
  

  public Event GetEvent()
  {
    return new PlayerServerAssignmentEvent(this._playerName, this._serverName);
  }
}
