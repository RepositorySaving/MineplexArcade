package mineplex.core.donation;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.common.CurrencyType;
import mineplex.core.donation.repository.token.DonorToken;



public class Donor
{
  private int _gems;
  private boolean _donated;
  private List<Integer> _salesPackagesOwned;
  private List<String> _unknownSalesPackagesOwned;
  private boolean _update = true;
  
  public Donor(DonorToken token)
  {
    this._gems = token.Gems;
    this._donated = token.Donated;
    
    this._salesPackagesOwned = token.SalesPackages;
    this._unknownSalesPackagesOwned = token.UnknownSalesPackages;
    
    if (this._salesPackagesOwned == null)
    {
      this._salesPackagesOwned = new ArrayList();
    }
    
    if (this._unknownSalesPackagesOwned == null)
    {
      this._unknownSalesPackagesOwned = new ArrayList();
    }
  }
  
  public int GetGems()
  {
    return this._gems;
  }
  
  public List<Integer> GetSalesPackagesOwned()
  {
    return this._salesPackagesOwned;
  }
  
  public List<String> GetUnknownSalesPackagesOwned()
  {
    return this._unknownSalesPackagesOwned;
  }
  
  public boolean Owns(Integer salesPackageId)
  {
    return (salesPackageId.intValue() == -1) || (this._salesPackagesOwned.contains(salesPackageId));
  }
  
  public void AddSalesPackagesOwned(int salesPackageId)
  {
    this._salesPackagesOwned.add(Integer.valueOf(salesPackageId));
  }
  
  public boolean HasDonated()
  {
    return this._donated;
  }
  
  public void DeductCost(int cost, CurrencyType currencyType)
  {
    switch (currencyType)
    {
    case Tokens: 
      this._gems -= cost;
      this._update = true;
      break;
    }
    
  }
  

  public int GetBalance(CurrencyType currencyType)
  {
    switch (currencyType)
    {
    case Tokens: 
      return this._gems;
    case Coins: 
      return 0;
    }
    return 0;
  }
  

  public void AddGems(int gems)
  {
    this._gems += gems;
  }
  
  public boolean OwnsUnknownPackage(String packageName)
  {
    return this._unknownSalesPackagesOwned.contains(packageName);
  }
  
  public boolean Updated()
  {
    return this._update;
  }
  
  public void AddUnknownSalesPackagesOwned(String packageName)
  {
    this._unknownSalesPackagesOwned.add(packageName);
  }
  
  public boolean OwnsUltraPackage()
  {
    for (String packageName : this._unknownSalesPackagesOwned)
    {
      if (packageName.contains("ULTRA")) {
        return true;
      }
    }
    return false;
  }
}
