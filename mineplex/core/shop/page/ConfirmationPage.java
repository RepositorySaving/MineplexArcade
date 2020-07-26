package mineplex.core.shop.page;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.donation.DonationManager;
import mineplex.core.server.util.TransactionResponse;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ISalesPackage;
import mineplex.core.shop.item.ItemPackage;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.core.shop.item.ShopItem;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class ConfirmationPage<PluginType extends MiniPlugin, ShopType extends ShopBase<PluginType>> extends ShopPageBase<PluginType, ShopType> implements Runnable
{
  private Runnable _runnable;
  private ShopPageBase<PluginType, ShopType> _returnPage;
  private SalesPackageBase _salesItem;
  private int _okSquareSlotStart;
  private boolean _processing;
  private int _progressCount;
  private ShopItem _progressItem;
  private int _taskId;
  
  public ConfirmationPage(PluginType plugin, ShopType shop, CoreClientManager clientManager, DonationManager donationManager, Runnable runnable, ShopPageBase<PluginType, ShopType> returnPage, SalesPackageBase salesItem, CurrencyType currencyType, Player player)
  {
    super(plugin, shop, clientManager, donationManager, "            Confirmation", player);
    
    this._runnable = runnable;
    this._returnPage = returnPage;
    this._salesItem = salesItem;
    this.SelectedCurrency = currencyType;
    this._progressItem = new ShopItem(Material.LAPIS_BLOCK, (byte)11, ChatColor.BLUE + "Processing", null, 1, false, true);
    this._okSquareSlotStart = 27;
    
    if (this.Shop.CanPlayerAttemptPurchase(player))
    {
      BuildPage();
    }
    else
    {
      BuildErrorPage(new String[] { ChatColor.RED + "You have attempted too many invalid transactions.", ChatColor.RED + "Please wait 10 seconds before retrying." });
      this._taskId = plugin.GetScheduler().scheduleSyncRepeatingTask(plugin.GetPlugin(), this, 2L, 2L);
    }
  }
  
  protected void BuildPage()
  {
    getInventory().setItem(22, new ShopItem(this._salesItem.GetDisplayMaterial(), (byte)0, this._salesItem.GetDisplayName(), this._salesItem.GetDescription(), 1, false, true).getHandle());
    
    IButton okClicked = new IButton()
    {

      public void ClickedLeft(Player player)
      {
        ConfirmationPage.this.OkClicked(player);
      }
      

      public void ClickedRight(Player player)
      {
        ConfirmationPage.this.OkClicked(player);
      }
      
    };
    IButton cancelClicked = new IButton()
    {

      public void ClickedLeft(Player player)
      {
        ConfirmationPage.this.CancelClicked(player);
      }
      

      public void ClickedRight(Player player)
      {
        ConfirmationPage.this.CancelClicked(player);
      }
      
    };
    BuildSquareAt(this._okSquareSlotStart, new ShopItem(Material.EMERALD_BLOCK, (byte)0, ChatColor.GREEN + "OK", null, 1, false, true), okClicked);
    BuildSquareAt(this._okSquareSlotStart + 6, new ShopItem(Material.REDSTONE_BLOCK, (byte)0, ChatColor.RED + "CANCEL", null, 1, false, true), cancelClicked);
    
    getInventory().setItem(4, new ShopItem(this.SelectedCurrency.GetDisplayMaterial(), (byte)0, this.SelectedCurrency.toString(), new String[] { C.cGray + this._salesItem.GetCost(this.SelectedCurrency) + " " + this.SelectedCurrency.toString() + " will be deducted from your account balance." }, 1, false, true).getHandle());
  }
  
  protected void OkClicked(Player player)
  {
    ProcessTransaction();
  }
  
  protected void CancelClicked(Player player)
  {
    this.Plugin.GetScheduler().cancelTask(this._taskId);
    
    if (this._returnPage != null) {
      this.Shop.OpenPageForPlayer(player, this._returnPage);
    } else {
      player.closeInventory();
    }
  }
  
  private void BuildSquareAt(int slot, ShopItem item, IButton button) {
    BuildSquareAt(slot, item, new ItemPackage(item, 0, false, -1), button);
  }
  
  private void BuildSquareAt(int slot, ShopItem item, ISalesPackage middleItem, IButton button)
  {
    AddButton(slot, item, button);
    AddButton(slot + 1, item, button);
    AddButton(slot + 2, item, button);
    
    slot += 9;
    
    AddButton(slot, item, button);
    AddButton(slot + 1, item, button);
    AddButton(slot + 2, item, button);
    
    slot += 9;
    
    AddButton(slot, item, button);
    AddButton(slot + 1, item, button);
    AddButton(slot + 2, item, button);
  }
  
  private void ProcessTransaction()
  {
    for (int i = this._okSquareSlotStart; i < 54; i++)
    {
      clear(i);
    }
    
    this._processing = true;
    
    if (this._salesItem.IsKnown())
    {
      this.DonationManager.PurchaseKnownSalesPackage(new Callback()
      {
        public void run(TransactionResponse response)
        {
          ConfirmationPage.this.ShowResultsPage(response);
        }
      }, this.Player.getName(), this._salesItem.GetSalesPackageId());
    }
    else
    {
      this.DonationManager.PurchaseUnknownSalesPackage(new Callback()
      {
        public void run(TransactionResponse response)
        {
          ConfirmationPage.this.ShowResultsPage(response);
        }
      }, this.Player.getName(), this._salesItem.GetName(), this._salesItem.GetCost(this.SelectedCurrency), this._salesItem.OneTimePurchase());
    }
    
    this._taskId = this.Plugin.GetScheduler().scheduleSyncRepeatingTask(this.Plugin.GetPlugin(), this, 2L, 2L);
  }
  
  private void ShowResultsPage(TransactionResponse response)
  {
    this._processing = false;
    
    switch (response)
    {
    case InsufficientFunds: 
      BuildErrorPage(new String[] { ChatColor.RED + "There was an error processing your request." });
      this.Shop.AddPlayerProcessError(this.Player);
      break;
    case Success: 
      BuildErrorPage(new String[] { ChatColor.RED + "You already own this package." });
      this.Shop.AddPlayerProcessError(this.Player);
      break;
    case AlreadyOwns: 
      BuildErrorPage(new String[] { ChatColor.RED + "Your account has insufficient funds." });
      this.Shop.AddPlayerProcessError(this.Player);
      break;
    case Failed: 
      this._salesItem.Sold(this.Player, this.SelectedCurrency);
      
      BuildSuccessPage("Your purchase was successful.");
      
      if (this._runnable != null) {
        this._runnable.run();
      }
      break;
    }
    
    

    this._progressCount = 0;
  }
  
  private void BuildErrorPage(String... message)
  {
    IButton returnButton = new IButton()
    {

      public void ClickedLeft(Player player)
      {
        ConfirmationPage.this.CancelClicked(player);
      }
      

      public void ClickedRight(Player player)
      {
        ConfirmationPage.this.CancelClicked(player);
      }
      
    };
    ShopItem item = new ShopItem(Material.REDSTONE_BLOCK, (byte)0, ChatColor.RED + ChatColor.UNDERLINE + "ERROR", message, 1, false, true);
    for (int i = 0; i < getSize(); i++)
    {
      AddButton(i, item, returnButton);
    }
    
    this.Player.playSound(this.Player.getLocation(), Sound.BLAZE_DEATH, 1.0F, 0.1F);
  }
  
  private void BuildSuccessPage(String message)
  {
    IButton returnButton = new IButton()
    {

      public void ClickedLeft(Player player)
      {
        ConfirmationPage.this.CancelClicked(player);
      }
      

      public void ClickedRight(Player player)
      {
        ConfirmationPage.this.CancelClicked(player);
      }
      
    };
    ShopItem item = new ShopItem(Material.EMERALD_BLOCK, (byte)0, ChatColor.GREEN + message, null, 1, false, true);
    for (int i = 0; i < getSize(); i++)
    {
      AddButton(i, item, returnButton);
    }
    
    this.Player.playSound(this.Player.getLocation(), Sound.NOTE_PLING, 1.0F, 0.9F);
  }
  

  public void PlayerClosed()
  {
    super.PlayerClosed();
    
    Bukkit.getScheduler().cancelTask(this._taskId);
    
    if ((this._returnPage != null) && (this.Shop != null)) {
      this.Shop.SetCurrentPageForPlayer(this.Player, this._returnPage);
    }
  }
  
  public void run()
  {
    if (this._processing)
    {
      if (this._progressCount == 9)
      {
        for (int i = 45; i < 54; i++)
        {
          clear(i);
        }
        
        this._progressCount = 0;
      }
      
      setItem(45 + this._progressCount, this._progressItem);


    }
    else if (this._progressCount >= 20)
    {
      try
      {
        Bukkit.getScheduler().cancelTask(this._taskId);
        
        if ((this._returnPage != null) && (this.Shop != null))
        {
          this.Shop.OpenPageForPlayer(this.Player, this._returnPage);
        }
        else if (this.Player != null)
        {
          this.Player.closeInventory();
        }
      }
      catch (Exception exception)
      {
        exception.printStackTrace();
      }
      finally
      {
        Dispose();
      }
    }
    

    this._progressCount = (this._progressCount + 1);
  }
  

  public void Dispose()
  {
    super.Dispose();
    
    Bukkit.getScheduler().cancelTask(this._taskId);
  }
}
