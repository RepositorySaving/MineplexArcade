package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mineplex.core.server.event.GameReadyEvent;
import org.bukkit.event.Event;


public class GameReadyPacket
  extends Packet
{
  private List<String> _players;
  
  public GameReadyPacket() {}
  
  public GameReadyPacket(List<String> players)
  {
    this._players = players;
  }
  
  public void ParseStream(DataInputStream dataInput) throws IOException
  {
    int playerCount = dataInput.readShort();
    
    if (this._players == null) {
      this._players = new ArrayList();
    }
    for (int i = 0; i < playerCount; i++)
    {
      this._players.add(readString(dataInput, 16));
    }
  }
  
  public void Write(DataOutputStream dataOutput) throws IOException
  {
    dataOutput.writeShort(73);
    dataOutput.writeShort(this._players.size());
    
    for (int i = 0; i < this._players.size(); i++)
    {
      writeString((String)this._players.get(i), dataOutput);
    }
  }
  
  public Event GetEvent()
  {
    return new GameReadyEvent(this._players);
  }
  
  public List<String> GetPlayers()
  {
    return this._players;
  }
}
