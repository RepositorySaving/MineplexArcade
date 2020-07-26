package mineplex.core.shop.item;

import java.util.Arrays;
import java.util.List;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.InventoryUtil;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemPackage
  implements ISalesPackage
{
  private ShopItem _shopItem;
  private boolean _restrictToHotbar;
  private int _gemCost;
  private boolean _free;
  private int _salesPackageId;
  
  public ItemPackage(ShopItem shopItem, int gemCost, boolean isFree, int salesPackageId)
  {
    this(shopItem, true, gemCost, isFree, salesPackageId);
  }
  
  public ItemPackage(ShopItem shopItem, boolean restrictToHotbar, int gemCost, boolean isFree, int salesPackageId)
  {
    this._shopItem = shopItem;
    this._restrictToHotbar = restrictToHotbar;
    this._gemCost = gemCost;
    this._free = isFree;
    this._salesPackageId = salesPackageId;
  }
  

  public String GetName()
  {
    return this._shopItem.GetName();
  }
  

  public int GetSalesPackageId()
  {
    return this._salesPackageId;
  }
  
  public int GetGemCost()
  {
    return this._gemCost;
  }
  

  public boolean IsFree()
  {
    return this._free;
  }
  

  public boolean CanFitIn(CoreClient player)
  {
    if ((this._shopItem.IsLocked()) && (!IsFree())) {
      return false;
    }
    for (ItemStack itemStack : player.GetPlayer().getInventory())
    {
      if ((itemStack != null) && (itemStack.getType() == this._shopItem.getType())) { if (itemStack.getAmount() + this._shopItem.getAmount() <= (itemStack.getType() == Material.ARROW ? itemStack.getMaxStackSize() : 1))
        {
          return true;
        }
      }
    }
    if (this._gemCost == 0) {
      return true;
    }
    if (InventoryUtil.first((CraftInventory)player.GetPlayer().getInventory(), this._restrictToHotbar ? 9 : player.GetPlayer().getInventory().getSize(), null, true) == -1) {
      return false;
    }
    return true;
  }
  

  public void DeliverTo(Player player)
  {
    ShopItem shopItem = this._shopItem.clone();
    shopItem.SetDeliverySettings();
    
    if (shopItem.getType() == Material.ARROW)
    {


      player.getInventory().addItem(new ItemStack[] { shopItem });






    }
    else
    {






      int emptySlot = player.getInventory().firstEmpty();
      
      player.getInventory().setItem(emptySlot, shopItem);
    }
  }
  


  public void DeliverTo(Player player, int slot)
  {
    ShopItem shopItem = this._shopItem.clone();
    shopItem.SetDeliverySettings();
    
    player.getInventory().setItem(slot, shopItem);
  }
  


  public void PurchaseBy(CoreClient player)
  {
    DeliverTo(player.GetPlayer());
  }
  

  public int ReturnFrom(CoreClient player)
  {
    if (this._shopItem.IsDisplay()) {
      return 0;
    }
    ShopItem shopItem = this._shopItem.clone();
    shopItem.SetDeliverySettings();
    
    int count = 0;
    
    count = InventoryUtil.GetCountOfObjectsRemoved((CraftInventory)player.GetPlayer().getInventory(), 9, shopItem);
    







    return count;
  }
  

  public List<Integer> AddToCategory(IInventory inventory, int slot)
  {
    inventory.setItem(slot, this._shopItem.getHandle());
    
    return Arrays.asList(new Integer[] { Integer.valueOf(slot) });
  }
  
  public ShopItem GetItem()
  {
    return this._shopItem;
  }
}
