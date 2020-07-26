package nautilus.game.arcade.game.games.mineware.order;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.game.games.mineware.MineWare;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract class OrderGather extends Order
{
  private HashMap<Player, Integer> _counter = new HashMap();
  
  private int _id;
  private byte _data;
  private int _req;
  
  public OrderGather(MineWare host, String order, int id, int data, int required)
  {
    super(host, order);
    
    this._id = id;
    this._data = ((byte)data);
    this._req = required;
  }
  

  public void SubInitialize()
  {
    this._counter.clear();
    this.Host.BlockBreakAllow.add(Integer.valueOf(this._id));
    this.Host.ItemPickupAllow.add(Integer.valueOf(this._id));
  }
  

  public void FailItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(this._id, this._data, this._req) });
  }
  
  @EventHandler
  public void Pickup(PlayerPickupItemEvent event)
  {
    if (!UtilInv.IsItem(event.getItem().getItemStack(), this._id, this._data)) {
      return;
    }
    if (Has(event.getPlayer()))
    {
      event.setCancelled(true);
      return;
    }
    
    Add(event.getPlayer(), event.getItem().getItemStack().getAmount());
    
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
    player.playSound(player.getLocation(), org.bukkit.Sound.ORB_PICKUP, 2.0F, 1.5F);
  }
  
  public boolean Has(Player player)
  {
    if (!this._counter.containsKey(player)) {
      return false;
    }
    return ((Integer)this._counter.get(player)).intValue() >= this._req;
  }
}
