package mineplex.core.shop.item;

import java.util.List;
import mineplex.core.account.CoreClient;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.entity.Player;

public abstract interface ISalesPackage
{
  public abstract String GetName();
  
  public abstract int GetGemCost();
  
  public abstract boolean CanFitIn(CoreClient paramCoreClient);
  
  public abstract List<Integer> AddToCategory(IInventory paramIInventory, int paramInt);
  
  public abstract void DeliverTo(Player paramPlayer);
  
  public abstract void PurchaseBy(CoreClient paramCoreClient);
  
  public abstract int ReturnFrom(CoreClient paramCoreClient);
  
  public abstract void DeliverTo(Player paramPlayer, int paramInt);
  
  public abstract int GetSalesPackageId();
  
  public abstract boolean IsFree();
}
