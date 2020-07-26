package mineplex.core.packethandler;

import net.minecraft.server.v1_7_R3.IPacketVerifier;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_7_R3.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_7_R3.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketVerifier implements IPacketVerifier
{
  private Player _owner;
  private PacketHandler _handler;
  
  public PacketVerifier(Player owner, PacketHandler handler)
  {
    this._owner = owner;
    this._handler = handler;
  }
  

  public boolean verify(Packet o)
  {
    if ((o instanceof PacketPlayOutEntityTeleport))
    {
      PacketPlayOutEntityTeleport packet = (PacketPlayOutEntityTeleport)o;
      


      if ((this._handler.IsForwarding(this._owner)) && (this._handler.IsForwarded(this._owner, packet.a)))
      {
        forceProcess(new PacketPlayOutEntityTeleport(this._handler.GetForwardId(this._owner, packet.a), packet.b, packet.c, packet.d, packet.e, packet.f));
        return true;
      }
      if (this._handler.IsBlocked(this._owner, packet.a)) {
        return false;
      }
    } else if ((o instanceof PacketPlayOutEntityVelocity))
    {
      PacketPlayOutEntityVelocity packet = (PacketPlayOutEntityVelocity)o;
      


      if ((this._handler.IsForwarding(this._owner)) && (this._handler.IsForwarded(this._owner, packet.a)))
      {

        return false;
      }
      if (this._handler.IsBlocked(this._owner, packet.a)) {
        return false;
      }
    } else if ((o instanceof PacketPlayOutRelEntityMove))
    {
      PacketPlayOutRelEntityMove packet = (PacketPlayOutRelEntityMove)o;
      


      if ((this._handler.IsForwarding(this._owner)) && (this._handler.IsForwarded(this._owner, packet.a)))
      {
        forceProcess(new PacketPlayOutRelEntityMove(this._handler.GetForwardId(this._owner, packet.a), packet.b, packet.c, packet.d));
        return true;
      }
      if (this._handler.IsBlocked(this._owner, packet.a)) {
        return false;
      }
    } else if ((o instanceof PacketPlayOutRelEntityMoveLook))
    {
      PacketPlayOutRelEntityMoveLook packet = (PacketPlayOutRelEntityMoveLook)o;
      


      if ((this._handler.IsForwarding(this._owner)) && (this._handler.IsForwarded(this._owner, packet.a)))
      {
        forceProcess(new PacketPlayOutRelEntityMoveLook(this._handler.GetForwardId(this._owner, packet.a), packet.b, packet.c, packet.d, packet.e, packet.f));
        return true;
      }
      if (this._handler.IsBlocked(this._owner, packet.a)) {
        return false;
      }
    }
    return this._handler.FireRunnables(o, this._owner, this);
  }
  
  public void forceProcess(Packet packet)
  {
    ((CraftPlayer)this._owner).getHandle().playerConnection.networkManager.handle(packet, new GenericFutureListener[0]);
  }
  
  public void Deactivate()
  {
    this._owner = null;
  }
}
