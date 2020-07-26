package mineplex.core.server;

import mineplex.core.common.util.Callback;

public abstract interface IPurchaseRepository
{
  public abstract void PurchaseSalesPackage(Callback<String> paramCallback, String paramString, boolean paramBoolean, int paramInt);
}
