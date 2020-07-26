package nautilus.game.arcade.game.games.mineware.order;

import java.util.ArrayList;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.games.mineware.MineWare;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;




public abstract class Order
  implements Listener
{
  public MineWare Host;
  private String _order;
  private long _startTime;
  private long _duration;
  private HashSet<Player> _completed = new HashSet();
  
  public Order(MineWare host, String order)
  {
    this.Host = host;
    
    this._order = order;
  }
  
  public void StartOrder(int stage)
  {
    this._completed.clear();
    
    this._startTime = System.currentTimeMillis();
    
    this._duration = 60000L;
    
    SubInitialize();
    Initialize();
  }
  


  public void SubInitialize() {}
  

  public void EndOrder()
  {
    this.Host.BlockBreakAllow.clear();
    this.Host.BlockPlaceAllow.clear();
    this.Host.ItemDropAllow.clear();
    this.Host.ItemPickupAllow.clear();
    Uninitialize();
  }
  
  public abstract void Initialize();
  
  public abstract void Uninitialize();
  
  public String GetOrder() {
    return this._order;
  }
  
  public boolean Finish()
  {
    if (GetRemainingPlaces() <= 0) {
      return true;
    }
    return UtilTime.elapsed(this._startTime, this._duration);
  }
  
  public int GetTimeLeft()
  {
    return (int)((this._duration - (System.currentTimeMillis() - this._startTime)) / 1000L);
  }
  
  public void SetCompleted(Player player)
  {
    if (this._completed.contains(player)) {
      return;
    }
    this._completed.add(player);
    UtilPlayer.message(player, C.cGreen + C.Bold + "You completed the task!");
    player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
  }
  
  public boolean IsCompleted(Player player)
  {
    return this._completed.contains(player);
  }
  
  public abstract void FailItems(Player paramPlayer);
  
  public float GetTimeLeftPercent()
  {
    float a = (float)(this._duration - (System.currentTimeMillis() - this._startTime));
    float b = (float)this._duration;
    return a / b;
  }
  
  public int GetRemainingPlaces()
  {
    return (int)Math.max(0.0D, this.Host.GetPlayers(true).size() * 0.5D - this._completed.size());
  }
  
  public boolean PlayerHasCompleted()
  {
    return !this._completed.isEmpty();
  }
}
