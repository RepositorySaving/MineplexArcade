package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mineplex.core.server.event.PlayerGameRequestEvent;
import org.bukkit.event.Event;


public class PlayerGameRequestPacket
  extends Packet
{
  private String _playerName;
  
  public PlayerGameRequestPacket() {}
  
  public PlayerGameRequestPacket(String playerName)
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
    return new PlayerGameRequestEvent(this._playerName);
  }
  
  public String GetPlayerName()
  {
    return this._playerName;
  }
}
