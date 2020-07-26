package mineplex.core.shop.item;

import mineplex.core.common.CurrencyType;
import mineplex.core.donation.repository.GameSalesPackageToken;

public abstract interface ICurrencyPackage
{
  public abstract int GetSalesPackageId();
  
  public abstract int GetCost(CurrencyType paramCurrencyType);
  
  public abstract boolean IsFree();
  
  public abstract void Update(GameSalesPackageToken paramGameSalesPackageToken);
}
