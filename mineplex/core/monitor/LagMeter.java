package mineplex.core.monitor;

import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LagMeter
  extends MiniPlugin
{
  private CoreClientManager _clientManager;
  private long _lastRun = -1L;
  
  private int _count;
  private double _ticksPerSecond;
  private double _ticksPerSecondAverage;
  private long _lastAverage;
  private HashSet<Player> _monitoring = new HashSet();
  
  public LagMeter(JavaPlugin plugin, CoreClientManager clientManager)
  {
    super("LagMeter", plugin);
    
    this._clientManager = clientManager;
    this._lastRun = System.currentTimeMillis();
    this._lastAverage = System.currentTimeMillis();
  }
  
  @EventHandler
  public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event)
  {
    if (this._clientManager.Get(event.getPlayer()).GetRank().Has(Rank.MODERATOR))
    {
      if (event.getMessage().trim().equalsIgnoreCase("/lag"))
      {
        sendUpdate(event.getPlayer());
        event.setCancelled(true);
      }
      else if (event.getMessage().trim().equalsIgnoreCase("/monitor"))
      {
        if (this._monitoring.contains(event.getPlayer())) {
          this._monitoring.remove(event.getPlayer());
        } else {
          this._monitoring.add(event.getPlayer());
        }
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler
  public void playerQuit(PlayerQuitEvent event)
  {
    this._monitoring.remove(event.getPlayer());
  }
  
  @EventHandler
  public void update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    long now = System.currentTimeMillis();
    this._ticksPerSecond = (1000.0D / (now - this._lastRun) * 20.0D);
    
    sendUpdates();
    
    if (this._count % 30 == 0)
    {
      this._ticksPerSecondAverage = (30000.0D / (now - this._lastAverage) * 20.0D);
      this._lastAverage = now;
    }
    
    this._lastRun = now;
    
    this._count += 1;
  }
  
  public double getTicksPerSecond()
  {
    return this._ticksPerSecond;
  }
  
  private void sendUpdates()
  {
    for (Player player : this._monitoring)
    {
      sendUpdate(player);
    }
  }
  
  private void sendUpdate(Player player)
  {
    player.sendMessage(" ");
    player.sendMessage(" ");
    player.sendMessage(" ");
    player.sendMessage(" ");
    player.sendMessage(" ");
    player.sendMessage(F.main(GetName(), ChatColor.GRAY + "Live-------" + ChatColor.YELLOW + String.format("%.00f", new Object[] { Double.valueOf(this._ticksPerSecond) })));
    player.sendMessage(F.main(GetName(), ChatColor.GRAY + "Avg--------" + ChatColor.YELLOW + String.format("%.00f", new Object[] { Double.valueOf(this._ticksPerSecondAverage * 20.0D) })));
    player.sendMessage(F.main(GetName(), ChatColor.YELLOW + "MEM"));
    player.sendMessage(F.main(GetName(), ChatColor.GRAY + "Free-------" + ChatColor.YELLOW + Runtime.getRuntime().freeMemory() / 1048576L + "MB"));
    player.sendMessage(F.main(GetName(), new StringBuilder().append(ChatColor.GRAY).append("Max--------").append(ChatColor.YELLOW).append(Runtime.getRuntime().maxMemory() / 1048576L).toString()) + "MB");
  }
}
