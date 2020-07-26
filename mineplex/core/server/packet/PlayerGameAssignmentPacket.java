package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mineplex.core.server.event.PlayerGameAssignmentEvent;
import org.bukkit.event.Event;


public class PlayerGameAssignmentPacket
  extends Packet
{
  private String _playerName;
  
  public PlayerGameAssignmentPacket() {}
  
  public PlayerGameAssignmentPacket(String playerName)
  {
    this._playerName = playerName;
  }
  
  public void ParseStream(DataInputStream dataInput) throws IOException
  {
    this._playerName = readString(dataInput, 16);
  }
  
  public void Write(DataOutputStream dataOutput) throws IOException
  {
    dataOutput.writeShort(71);
    writeString(this._playerName, dataOutput);
  }
  
  public Event GetEvent()
  {
    return new PlayerGameAssignmentEvent(this._playerName);
  }
  
  public String GetPlayerName()
  {
    return this._playerName;
  }
}
