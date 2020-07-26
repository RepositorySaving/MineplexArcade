package nautilus.game.arcade.shop;

import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.shop.item.SalesPackageBase;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.entity.Player;

public class KitPackage extends SalesPackageBase
{
  public KitPackage(String gameName, Kit kit)
  {
    super(gameName + " " + kit.GetName(), kit.getDisplayMaterial(), kit.GetDesc());
    this.KnownPackage = false;
    this.CurrencyCostMap.put(CurrencyType.Gems, Integer.valueOf(kit.GetCost()));
  }
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
