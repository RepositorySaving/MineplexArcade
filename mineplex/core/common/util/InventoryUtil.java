package mineplex.core.common.util;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil
{
  public static HashMap<Integer, ItemStack> removeItem(CraftInventory inventory, int endingSlot, ItemStack... items)
  {
    HashMap<Integer, ItemStack> leftover = new HashMap();
    
    if (endingSlot >= 54) {
      return leftover;
    }
    for (int i = 0; i < items.length; i++)
    {
      ItemStack item = items[i];
      int toDelete = item.getAmount();
      
      do
      {
        int first = first(inventory, endingSlot, item, false);
        
        if (first == -1)
        {
          item.setAmount(toDelete);
          leftover.put(Integer.valueOf(i), item);
          break;
        }
        

        ItemStack itemStack = inventory.getItem(first);
        int amount = itemStack.getAmount();
        
        if (amount <= toDelete)
        {
          toDelete -= amount;
          inventory.clear(first);
        }
        else
        {
          itemStack.setAmount(amount - toDelete);
          inventory.setItem(first, itemStack);
          toDelete = 0;
        }
        
      }
      while (toDelete > 0);
    }
    


    return leftover;
  }
  
  public static int first(CraftInventory craftInventory, int endingSlot, ItemStack item, boolean withAmount)
  {
    if (endingSlot >= 54) {
      return -1;
    }
    ItemStack[] inventory = craftInventory.getContents();
    
    for (int i = 0; i < endingSlot; i++)
    {
      if (inventory[i] == null)
      {
        if (item == null) {
          return i;
        }
        
      }
      else if (item != null)
      {

        boolean equals = false;
        
        if (withAmount)
        {
          equals = item.equals(inventory[i]);
        }
        else
        {
          equals = (item.getTypeId() == inventory[i].getTypeId()) && (item.getDurability() == inventory[i].getDurability()) && (item.getEnchantments().equals(inventory[i].getEnchantments()));
        }
        
        if (equals)
        {
          return i;
        }
      }
    }
    return -1;
  }
  
  public static int GetCountOfObjectsRemoved(CraftInventory getInventory, int i, ItemStack itemStack)
  {
    int count = 0;
    
    do
    {
      count++;
      if (!getInventory.contains(itemStack.getType(), itemStack.getAmount())) break; } while (removeItem(getInventory, i, new ItemStack[] { itemStack }).size() == 0);
    



    return count;
  }
  
  public static int GetCountOfObjectsRemovedInSlot(CraftInventory getInventory, int slot, ItemStack itemStack)
  {
    int count = 0;
    ItemStack slotStack = getInventory.getItem(slot);
    
    while ((slotStack.getType() == itemStack.getType()) && (slotStack.getAmount() >= itemStack.getAmount()))
    {
      slotStack.setAmount(slotStack.getAmount() - itemStack.getAmount());
      count++;
    }
    
    if (slotStack.getAmount() == 0) {
      getInventory.setItem(slot, null);
    }
    return count;
  }
}
