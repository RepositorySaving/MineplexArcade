package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mineplex.core.server.event.ServerReadyEvent;
import org.bukkit.event.Event;


public class ServerReadyPacket
  extends Packet
{
  private String _serverPath;
  
  public ServerReadyPacket() {}
  
  public ServerReadyPacket(String serverPath)
  {
    this._serverPath = serverPath;
  }
  
  public void ParseStream(DataInputStream inputStream)
    throws IOException
  {
    this._serverPath = readString(inputStream, 21);
  }
  
  public void Write(DataOutputStream dataOutput)
    throws IOException
  {
    dataOutput.writeShort(61);
    writeString(this._serverPath, dataOutput);
  }
  

  public Event GetEvent()
  {
    return new ServerReadyEvent(this._serverPath);
  }
}
