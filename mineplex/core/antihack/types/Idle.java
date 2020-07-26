package mineplex.core.antihack.types;

import java.util.HashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.antihack.AntiHack;
import mineplex.core.antihack.Detector;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.portal.Portal;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Idle
  extends MiniPlugin
  implements Detector
{
  private AntiHack Host;
  private HashMap<Player, Long> _idleTime = new HashMap();
  
  public Idle(AntiHack host)
  {
    super("Idle Detector", host.GetPlugin());
    this.Host = host;
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void updateFlyhack(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    
    this._idleTime.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void updateFreeCam(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {

      if (!this.Host.isValid(player, true))
      {

        if (this._idleTime.containsKey(player))
        {

          if (UtilTime.elapsed(((Long)this._idleTime.get(player)).longValue(), this.Host.IdleTime))
          {




            UtilPlayer.message(player, C.cRed + C.Bold + "Mineplex Anti-Cheat detected Lagging / Fly (Idle)");
            UtilPlayer.message(player, C.cRed + C.Bold + "You have been returned to Lobby.");
            this.Host.Portal.SendPlayerToServer(player, "Lobby");
          } }
      }
    }
  }
  
  public void Reset(Player player) {
    this._idleTime.remove(player);
  }
}
