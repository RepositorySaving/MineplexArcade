package nautilus.game.arcade.game.games.mineware.order;

import java.util.HashMap;
import java.util.HashSet;
import nautilus.game.arcade.game.games.mineware.MineWare;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public abstract class OrderPlace extends Order
{
  private HashMap<Player, Integer> _counter = new HashMap();
  
  private int _id;
  private byte _data;
  private int _req;
  
  public OrderPlace(MineWare host, String order, int id, int data, int required)
  {
    super(host, order);
    
    this._id = id;
    this._data = ((byte)data);
    this._req = required;
  }
  

  public void SubInitialize()
  {
    this._counter.clear();
    this.Host.BlockPlaceAllow.add(Integer.valueOf(this._id));
  }
  


  public void FailItems(Player player) {}
  


  @EventHandler
  public void Place(BlockPlaceEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (event.getBlock().getTypeId() != this._id) {
      return;
    }
    if ((this._data != -1) && (event.getBlock().getData() != this._data)) {
      return;
    }
    Add(event.getPlayer(), 1);
    
    if (Has(event.getPlayer())) {
      SetCompleted(event.getPlayer());
    }
  }
  
  public void Add(Player player, int add) {
    if (!this._counter.containsKey(player)) {
      this._counter.put(player, Integer.valueOf(add));
    }
    else {
      this._counter.put(player, Integer.valueOf(((Integer)this._counter.get(player)).intValue() + add));
    }
    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2.0F, 1.5F);
  }
  
  public boolean Has(Player player)
  {
    if (!this._counter.containsKey(player)) {
      return false;
    }
    return ((Integer)this._counter.get(player)).intValue() >= this._req;
  }
}
