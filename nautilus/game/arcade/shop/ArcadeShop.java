package nautilus.game.arcade.shop;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import nautilus.game.arcade.ArcadeManager;
import org.bukkit.entity.Player;

public class ArcadeShop
  extends ShopBase<ArcadeManager>
{
  public ArcadeShop(ArcadeManager plugin, CoreClientManager clientManager, DonationManager donationManager)
  {
    super(plugin, clientManager, donationManager, "Shop", new CurrencyType[] { CurrencyType.Gems });
  }
  

  protected ShopPageBase<ArcadeManager, ? extends ShopBase<ArcadeManager>> BuildPagesFor(Player player)
  {
    return null;
  }
}
