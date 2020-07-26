package nautilus.game.arcade.game.games.mineware.order;

import java.util.HashMap;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.game.games.mineware.MineWare;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class OrderCraft
  extends Order
{
  private HashMap<Player, Integer> _counter = new HashMap();
  
  private int _id;
  private byte _data;
  private int _req;
  
  public OrderCraft(MineWare host, String order, int id, int data, int required)
  {
    super(host, order);
    
    this._id = id;
    this._data = ((byte)data);
    this._req = required;
  }
  

  public void SubInitialize()
  {
    this._counter.clear();
  }
  

  public void FailItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(this._id, this._data, this._req) });
  }
  
  @EventHandler
  public void Craft(InventoryClickEvent event)
  {
    if (event.getSlotType() != InventoryType.SlotType.RESULT) {
      return;
    }
    if (!UtilInv.IsItem(event.getCurrentItem(), this._id, this._data)) {
      return;
    }
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getWhoClicked();
    
    if (!event.isShiftClick())
    {
      Add(player, event.getCurrentItem().getAmount());
    }
    else
    {
      CraftingInventory inv = (CraftingInventory)event.getInventory();
      
      int make = 128;
      

      for (ItemStack item : inv.getMatrix()) {
        if ((item != null) && (item.getType() != Material.AIR) && 
          (item.getAmount() < make))
          make = item.getAmount();
      }
      make *= event.getCurrentItem().getAmount();
      
      Add(player, make);
    }
    
    if (Has(player)) {
      SetCompleted(player);
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
