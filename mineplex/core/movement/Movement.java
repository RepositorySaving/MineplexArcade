package mineplex.core.movement;

import mineplex.core.MiniClientPlugin;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Movement extends MiniClientPlugin<ClientMovement>
{
  public Movement(JavaPlugin plugin)
  {
    super("Movement", plugin);
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.TICK)
    {
      for (Player cur : GetPlugin().getServer().getOnlinePlayers())
      {
        ClientMovement player = (ClientMovement)Get(cur);
        
        if ((player.LastLocation != null) && 
          (mineplex.core.common.util.UtilMath.offset(player.LastLocation, cur.getLocation()) > 0.0D)) {
          player.LastMovement = System.currentTimeMillis();
        }
        player.LastLocation = cur.getLocation();
        

        if (((CraftPlayer)cur).getHandle().onGround) {
          player.LastGrounded = System.currentTimeMillis();
        }
      }
    }
  }
  
  protected ClientMovement AddPlayer(String player)
  {
    return new ClientMovement();
  }
}
