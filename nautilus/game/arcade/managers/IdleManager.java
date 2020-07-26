package nautilus.game.arcade.managers;

import java.util.HashMap;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.portal.Portal;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class IdleManager implements org.bukkit.event.Listener
{
  ArcadeManager Manager;
  private HashMap<Player, Float> _yaw = new HashMap();
  private HashMap<Player, Long> _idle = new HashMap();
  private HashMap<Player, Integer> _beep = new HashMap();
  

  public IdleManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler
  public void KickIdlePlayers(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    if ((this.Manager.GetGame() != null) && (!this.Manager.GetGame().IdleKick)) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if ((!this._yaw.containsKey(player)) || (!this._idle.containsKey(player)))
      {
        this._yaw.put(player, Float.valueOf(player.getLocation().getYaw()));
        this._idle.put(player, Long.valueOf(System.currentTimeMillis()));
      }
      
      if (((Float)this._yaw.get(player)).floatValue() == player.getLocation().getYaw())
      {
        if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._idle.get(player)).longValue(), 120000L))
        {
          if ((this.Manager.GetGame().GetState() == nautilus.game.arcade.game.Game.GameState.Recruit) || (this.Manager.GetGame().IsAlive(player)))
          {

            if (!this.Manager.GetClients().Get(player).GetRank().Has(Rank.MODERATOR))
            {


              if (!this._beep.containsKey(player))
              {
                this._beep.put(player, Integer.valueOf(20));

              }
              else
              {
                int count = ((Integer)this._beep.get(player)).intValue();
                
                if (count == 0)
                {
                  player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10.0F, 1.0F);
                  this.Manager.GetPortal().SendPlayerToServer(player, "Lobby");
                }
                else
                {
                  float scale = (float)(0.8D + count / 20.0D * 1.2D);
                  player.playSound(player.getLocation(), Sound.NOTE_PLING, scale, scale);
                  
                  if (count % 2 == 0)
                  {
                    UtilPlayer.message(player, C.cGold + C.Bold + "You will be AFK removed in " + count / 2 + " seconds...");
                  }
                  
                  count--;
                  this._beep.put(player, Integer.valueOf(count));
                }
              }
            }
          }
        }
      }
      else {
        this._yaw.put(player, Float.valueOf(player.getLocation().getYaw()));
        this._idle.put(player, Long.valueOf(System.currentTimeMillis()));
        this._beep.remove(player);
      }
    }
  }
  
  @EventHandler
  public void Quit(PlayerQuitEvent event) {
    this._yaw.remove(event.getPlayer());
    this._idle.remove(event.getPlayer());
    this._beep.remove(event.getPlayer());
  }
}
