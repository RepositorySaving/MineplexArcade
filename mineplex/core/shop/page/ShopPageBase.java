package mineplex.core.shop.page;

import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ShopItem;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public abstract class ShopPageBase<PluginType extends MiniPlugin, ShopType extends ShopBase<PluginType>> extends CraftInventoryCustom implements org.bukkit.event.Listener
{
  protected PluginType Plugin;
  protected CoreClientManager ClientManager;
  protected DonationManager DonationManager;
  protected ShopType Shop;
  protected Player Player;
  protected mineplex.core.account.CoreClient Client;
  protected CurrencyType SelectedCurrency;
  protected NautHashMap<Integer, IButton> ButtonMap;
  protected boolean ShowCurrency = false;
  
  protected int CurrencySlot = 4;
  
  public ShopPageBase(PluginType plugin, ShopType shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player)
  {
    this(plugin, shop, clientManager, donationManager, name, player, 54);
  }
  
  public ShopPageBase(PluginType plugin, ShopType shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player, int slots)
  {
    super(null, slots, name);
    
    this.Plugin = plugin;
    this.ClientManager = clientManager;
    this.DonationManager = donationManager;
    this.Shop = shop;
    this.Player = player;
    this.ButtonMap = new NautHashMap();
    
    this.Client = this.ClientManager.Get(player);
    
    if (shop.GetAvailableCurrencyTypes().size() > 0)
    {
      this.SelectedCurrency = ((CurrencyType)shop.GetAvailableCurrencyTypes().get(0));
    }
  }
  
  protected void ChangeCurrency(Player player)
  {
    PlayAcceptSound(player);
    
    int currentIndex = this.Shop.GetAvailableCurrencyTypes().indexOf(this.SelectedCurrency);
    
    if (currentIndex + 1 < this.Shop.GetAvailableCurrencyTypes().size())
    {
      this.SelectedCurrency = ((CurrencyType)this.Shop.GetAvailableCurrencyTypes().get(currentIndex + 1));
    }
    else
    {
      this.SelectedCurrency = ((CurrencyType)this.Shop.GetAvailableCurrencyTypes().get(0));
    }
  }
  
  protected abstract void BuildPage();
  
  protected void AddItem(int slot, ShopItem item)
  {
    if (slot > this.inventory.getSize() - 1)
    {
      this.Player.getInventory().setItem(getPlayerSlot(slot), item);
    }
    else
    {
      getInventory().setItem(slot, item.getHandle());
    }
  }
  
  protected int getPlayerSlot(int slot)
  {
    return slot >= this.inventory.getSize() + 27 ? slot - (this.inventory.getSize() + 27) : slot - (this.inventory.getSize() - 9);
  }
  
  protected void AddButton(int slot, ShopItem item, IButton button)
  {
    AddItem(slot, item);
    
    this.ButtonMap.put(Integer.valueOf(slot), button);
  }
  
  protected void RemoveButton(int slot)
  {
    getInventory().setItem(slot, null);
    this.ButtonMap.remove(Integer.valueOf(slot));
  }
  
  public void PlayerClicked(InventoryClickEvent event)
  {
    if (this.ButtonMap.containsKey(Integer.valueOf(event.getRawSlot())))
    {
      if (event.isLeftClick())
        ((IButton)this.ButtonMap.get(Integer.valueOf(event.getRawSlot()))).ClickedLeft(this.Player);
      if (event.isRightClick()) {
        ((IButton)this.ButtonMap.get(Integer.valueOf(event.getRawSlot()))).ClickedRight(this.Player);
      }
    } else if (event.getRawSlot() != -999)
    {
      if ((event.getInventory() == this.inventory) && ((this.inventory.getSize() <= event.getSlot()) || (this.inventory.getItem(event.getSlot()) != null)))
      {
        PlayDenySound(this.Player);
      }
      else if ((event.getInventory() == this.Player.getInventory()) && (this.Player.getInventory().getItem(event.getSlot()) != null))
      {
        PlayDenySound(this.Player);
      }
    }
  }
  


  public void PlayerOpened() {}
  

  public void PlayerClosed()
  {
    this.inventory.onClose((CraftPlayer)this.Player);
  }
  
  public void PlayAcceptSound(Player player)
  {
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.6F);
  }
  
  public void PlayRemoveSound(Player player)
  {
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 0.6F);
  }
  
  public void PlayDenySound(Player player)
  {
    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 0.6F);
  }
  
  public void Dispose()
  {
    this.Player = null;
    this.Client = null;
    this.Shop = null;
    this.Plugin = null;
  }
}
