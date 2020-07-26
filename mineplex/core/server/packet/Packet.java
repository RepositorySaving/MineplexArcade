package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.event.Event;

public abstract class Packet
{
  public abstract void ParseStream(DataInputStream paramDataInputStream)
    throws IOException;
  
  public abstract void Write(DataOutputStream paramDataOutputStream) throws IOException;
  
  public abstract Event GetEvent();
  
  protected String readString(DataInputStream dataInputStream, int maxLength) throws IOException
  {
    short length = dataInputStream.readShort();
    
    if (length > maxLength)
    {
      throw new IOException("Received string length longer than maximum allowed (" + length + " > " + maxLength + ")");
    }
    if (length < 0)
    {
      throw new IOException("Received string length is less than zero! Weird string!");
    }
    

    StringBuilder stringBuilder = new StringBuilder();
    
    for (int i = 0; i < length; i++)
    {
      stringBuilder.append(dataInputStream.readChar());
    }
    
    return stringBuilder.toString();
  }
  
  protected void writeString(String string, DataOutputStream dataOutputStream)
    throws IOException
  {
    dataOutputStream.writeShort(string.length());
    dataOutputStream.writeChars(string);
  }
}
