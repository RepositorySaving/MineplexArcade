package mineplex.core.sound;

import mineplex.core.MiniPlugin;
import mineplex.core.packethandler.IPacketRunnable;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.packethandler.PacketVerifier;
import net.minecraft.server.v1_7_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SoundManager
  extends MiniPlugin
  implements IPacketRunnable
{
  public SoundManager(JavaPlugin plugin, PacketHandler packetHandler)
  {
    super("Sound Manager", plugin);
    
    packetHandler.AddPacketRunnable(this);
  }
  

  public boolean run(Packet packet, Player owner, PacketVerifier packetList)
  {
    return false;
  }
}
