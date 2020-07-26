package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mineplex.core.server.event.PlayerVoteEvent;
import org.bukkit.event.Event;


public class PlayerVotePacket
  extends Packet
{
  private String _playerName;
  private int _points;
  
  public PlayerVotePacket() {}
  
  public PlayerVotePacket(String playerName, int points)
  {
    this._playerName = playerName;
    this._points = points;
  }
  
  public void ParseStream(DataInputStream dataInput) throws IOException
  {
    this._playerName = readString(dataInput, 16);
    this._points = dataInput.readInt();
  }
  
  public void Write(DataOutputStream dataOutput) throws IOException
  {
    dataOutput.writeShort(81);
    writeString(this._playerName, dataOutput);
    dataOutput.writeInt(this._points);
  }
  
  public String GetPlayerName()
  {
    return this._playerName;
  }
  
  public int GetPointReward()
  {
    return this._points;
  }
  
  public Event GetEvent()
  {
    return new PlayerVoteEvent(this._playerName, this._points);
  }
}
