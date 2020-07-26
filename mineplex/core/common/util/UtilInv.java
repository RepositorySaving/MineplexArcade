package mineplex.core.common.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class UtilInv
{
  public static boolean insert(Player player, ItemStack stack)
  {
    player.getInventory().addItem(new ItemStack[] { stack });
    player.updateInventory();
    return true;
  }
  
  public static boolean contains(Player player, Material item, byte data, int required)
  {
    for (Iterator localIterator = player.getInventory().all(item).keySet().iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
      
      if (required <= 0) {
        return true;
      }
      ItemStack stack = player.getInventory().getItem(i);
      
      if ((stack != null) && (stack.getAmount() > 0) && ((stack.getData() == null) || (stack.getData().getData() == data)))
      {
        required -= stack.getAmount();
      }
    }
    
    if (required <= 0)
    {
      return true;
    }
    
    return false;
  }
  

  public static boolean remove(Player player, Material item, byte data, int toRemove)
  {
    if (!contains(player, item, data, toRemove)) {
      return false;
    }
    for (Iterator localIterator = player.getInventory().all(item).keySet().iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
      
      if (toRemove > 0)
      {

        ItemStack stack = player.getInventory().getItem(i);
        
        if ((stack.getData() == null) || (stack.getData().getData() == data))
        {
          int foundAmount = stack.getAmount();
          
          if (toRemove >= foundAmount)
          {
            toRemove -= foundAmount;
            player.getInventory().setItem(i, null);

          }
          else
          {
            stack.setAmount(foundAmount - toRemove);
            player.getInventory().setItem(i, stack);
            toRemove = 0;
          }
        }
      }
    }
    player.updateInventory();
    return true;
  }
  
  public static void Clear(Player player)
  {
    PlayerInventory inv = player.getInventory();
    
    inv.clear();
    inv.clear(inv.getSize() + 0);
    inv.clear(inv.getSize() + 1);
    inv.clear(inv.getSize() + 2);
    inv.clear(inv.getSize() + 3);
    
    player.saveData();
  }
  
  public static void drop(Player player, boolean clear)
  {
    for (ItemStack cur : player.getInventory().getContents())
    {
      if (cur != null)
      {

        if (cur.getType() != Material.AIR)
        {

          player.getWorld().dropItemNaturally(player.getLocation(), cur); }
      }
    }
    for (ItemStack cur : player.getInventory().getArmorContents())
    {
      if (cur != null)
      {

        if (cur.getType() != Material.AIR)
        {

          player.getWorld().dropItemNaturally(player.getLocation(), cur); }
      }
    }
    if (clear) {
      Clear(player);
    }
  }
  
  public static void Update(Entity player)
  {
    if (!(player instanceof Player)) {
      return;
    }
    ((Player)player).updateInventory();
  }
  
  public static int removeAll(Player player, Material type, byte data)
  {
    HashSet<ItemStack> remove = new HashSet();
    int count = 0;
    
    for (ItemStack item : player.getInventory().getContents()) {
      if ((item != null) && 
        (item.getType() == type) && (
        (data == -1) || (item.getData() == null) || ((item.getData() != null) && (item.getData().getData() == data))))
      {
        count += item.getAmount();
        remove.add(item);
      }
    }
    for (ItemStack item : remove) {
      player.getInventory().remove(item);
    }
    return count;
  }
  
  public static byte GetData(ItemStack stack)
  {
    if (stack == null) {
      return 0;
    }
    if (stack.getData() == null) {
      return 0;
    }
    return stack.getData().getData();
  }
  
  public static boolean IsItem(ItemStack item, Material type, byte data)
  {
    return IsItem(item, type.getId(), data);
  }
  
  public static boolean IsItem(ItemStack item, int id, byte data)
  {
    if (item == null) {
      return false;
    }
    if (item.getTypeId() != id) {
      return false;
    }
    if ((data != -1) && (GetData(item) != data)) {
      return false;
    }
    return true;
  }
  
  public static void DisallowMovementOf(InventoryClickEvent event, String name, Material type, byte data, boolean inform)
  {
    DisallowMovementOf(event, name, type, data, inform, false);
  }
  


















  public static void DisallowMovementOf(InventoryClickEvent event, String name, Material type, byte data, boolean inform, boolean allInventorties)
  {
    if ((!allInventorties) && (event.getInventory().getType() == org.bukkit.event.inventory.InventoryType.CRAFTING)) {
      return;
    }
    
    if ((event.getAction() == InventoryAction.HOTBAR_SWAP) || 
      (event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD))
    {
      boolean match = false;
      
      if (IsItem(event.getCurrentItem(), type, data)) {
        match = true;
      }
      if (IsItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()), type, data)) {
        match = true;
      }
      if (!match) {
        return;
      }
      
      UtilPlayer.message(event.getWhoClicked(), F.main("Inventory", "You cannot hotbar swap " + F.item(name) + "."));
      event.setCancelled(true);

    }
    else
    {
      if (event.getCurrentItem() == null) {
        return;
      }
      IsItem(event.getCurrentItem(), type, data);
      

      if (!IsItem(event.getCurrentItem(), type, data)) {
        return;
      }
      UtilPlayer.message(event.getWhoClicked(), F.main("Inventory", "You cannot move " + F.item(name) + "."));
      event.setCancelled(true);
    }
  }
  
  public static void refreshDurability(Player player, Material type)
  {
    for (ItemStack item : player.getInventory().getContents()) {
      if ((item != null) && 
        (item.getType() == type))
      {
        if (item.getDurability() == 0)
        {
          item.setDurability((short)1);
        }
        else
        {
          item.setDurability((short)0);
        }
      }
    }
  }
}
