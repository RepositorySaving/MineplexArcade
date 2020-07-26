package mineplex.minecraft.game.classcombat.shop.salespackage;

import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.minecraft.game.classcombat.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ItemSalesPackage
  extends SalesPackageBase
{
  public ItemSalesPackage(Item item)
  {
    super("Champions " + item.GetName(), Material.BOOK, (byte)0, item.GetDesc(), item.GetGemCost());
    this.Free = item.isFree();
    this.KnownPackage = false;
  }
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
