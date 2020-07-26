package mineplex.core.donation;

import mineplex.core.MiniPlugin;
import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.repository.DonationRepository;
import mineplex.core.donation.repository.token.DonorTokenWrapper;
import mineplex.core.server.util.TransactionResponse;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;



public class DonationManager
  extends MiniPlugin
{
  private DonationRepository _repository;
  private NautHashMap<String, Donor> _donors;
  private Object _donorLock = new Object();
  
  public DonationManager(JavaPlugin plugin, String webAddress)
  {
    super("Donation", plugin);
    
    this._repository = new DonationRepository(webAddress);
    
    this._donors = new NautHashMap();
  }
  

  public void AddCommands()
  {
    AddCommand(new GemCommand(this));
  }
  
  @EventHandler
  public void OnClientWebResponse(ClientWebResponseEvent event)
  {
    DonorTokenWrapper token = (DonorTokenWrapper)new Gson().fromJson(event.GetResponse(), DonorTokenWrapper.class);
    LoadDonor(token);
  }
  
  @EventHandler
  public void UnloadDonor(ClientUnloadEvent event)
  {
    synchronized (this._donorLock)
    {
      this._donors.remove(event.GetName());
    }
  }
  
  private void LoadDonor(DonorTokenWrapper token)
  {
    synchronized (this._donorLock)
    {
      this._donors.put(token.Name, new Donor(token.DonorToken));
    }
  }
  
  public Donor Get(String name)
  {
    synchronized (this._donorLock)
    {
      return (Donor)this._donors.get(name);
    }
  }
  
  public void PurchaseUnknownSalesPackage(final Callback<TransactionResponse> callback, final String name, final String packageName, final int gemCost, boolean oneTimePurchase)
  {
    Donor donor = Get(name);
    
    if (donor != null)
    {
      if ((oneTimePurchase) && (donor.OwnsUnknownPackage(packageName)))
      {
        if (callback != null) {
          callback.run(TransactionResponse.AlreadyOwns);
        }
        return;
      }
    }
    
    this._repository.PurchaseUnknownSalesPackage(new Callback()
    {
      public void run(TransactionResponse response)
      {
        if (response == TransactionResponse.Success)
        {
          Donor donor = DonationManager.this.Get(name);
          
          if (donor != null)
          {
            donor.AddUnknownSalesPackagesOwned(packageName);
            donor.DeductCost(gemCost, CurrencyType.Gems);
          }
        }
        
        if (callback != null)
          callback.run(response);
      }
    }, name, packageName, gemCost);
  }
  
  public void PurchaseKnownSalesPackage(final Callback<TransactionResponse> callback, final String name, final int salesPackageId)
  {
    this._repository.PurchaseKnownSalesPackage(new Callback()
    {
      public void run(TransactionResponse response)
      {
        if (response == TransactionResponse.Success)
        {
          Donor donor = DonationManager.this.Get(name);
          
          if (donor != null)
          {
            donor.AddSalesPackagesOwned(salesPackageId);
          }
        }
        
        if (callback != null)
          callback.run(response);
      }
    }, name, salesPackageId);
  }
  
  public void RewardGems(final Callback<Boolean> callback, String caller, final String name, final int greenGems)
  {
    this._repository.PlayerUpdate(new Callback()
    {
      public void run(Boolean success)
      {
        if (success.booleanValue())
        {
          Donor donor = DonationManager.this.Get(name);
          
          if (donor != null)
          {
            donor.AddGems(greenGems);
          }
          
          if (callback != null)
            callback.run(Boolean.valueOf(true));
        }
      }
    }, caller, name, greenGems);
  }
}
