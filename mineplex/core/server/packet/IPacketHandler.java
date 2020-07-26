package mineplex.core.server.packet;

import java.net.Socket;
import org.bukkit.event.Event;

public abstract interface IPacketHandler
{
  public abstract void HandlePacketEvent(Event paramEvent, Socket paramSocket);
}
