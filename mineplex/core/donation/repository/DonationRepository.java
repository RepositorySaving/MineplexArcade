package mineplex.core.donation.repository;

import mineplex.core.common.util.Callback;
import mineplex.core.donation.repository.token.GemRewardToken;
import mineplex.core.donation.repository.token.PurchaseToken;
import mineplex.core.donation.repository.token.UnknownPurchaseToken;
import mineplex.core.server.remotecall.AsyncJsonWebCall;
import mineplex.core.server.util.TransactionResponse;

public class DonationRepository
{
  private String _webAddress;
  
  public DonationRepository(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public void PurchaseKnownSalesPackage(Callback<TransactionResponse> callback, String name, int salesPackageId)
  {
    PurchaseToken token = new PurchaseToken();
    token.AccountName = name;
    token.UsingCredits = false;
    token.SalesPackageId = salesPackageId;
    
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/PurchaseKnownSalesPackage").Execute(TransactionResponse.class, callback, token);
  }
  
  public void PurchaseUnknownSalesPackage(Callback<TransactionResponse> callback, String name, String packageName, int gemCost)
  {
    UnknownPurchaseToken token = new UnknownPurchaseToken();
    token.AccountName = name;
    token.SalesPackageName = packageName;
    token.Cost = gemCost;
    token.Premium = false;
    
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/PurchaseUnknownSalesPackage").Execute(TransactionResponse.class, callback, token);
  }
  
  public void PlayerUpdate(Callback<Boolean> callback, String giver, String name, int greenGems)
  {
    GemRewardToken token = new GemRewardToken();
    token.Source = giver;
    token.Name = name;
    token.Amount = greenGems;
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/GemReward").Execute(Boolean.class, callback, token);
  }
}
