package mineplex.core.pet;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.pet.ui.PetPage;
import mineplex.core.pet.ui.PetTagPage;
import mineplex.core.shop.ShopBase;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

public class PetShop extends ShopBase<PetManager> implements org.bukkit.plugin.messaging.PluginMessageListener
{
  public PetShop(PetManager plugin, CoreClientManager manager, DonationManager donationManager)
  {
    super(plugin, manager, donationManager, "Pet Shop", new CurrencyType[] { CurrencyType.Gems });
    
    plugin.GetPlugin().getServer().getMessenger().registerIncomingPluginChannel(plugin.GetPlugin(), "MC|ItemName", this);
  }
  

  protected mineplex.core.shop.page.ShopPageBase<PetManager, ? extends ShopBase<PetManager>> BuildPagesFor(Player player)
  {
    return new PetPage((PetManager)this.Plugin, this, this.ClientManager, this.DonationManager, "     Pets", player);
  }
  

  public void onPluginMessageReceived(String channel, Player player, byte[] message)
  {
    if (!channel.equalsIgnoreCase("MC|ItemName")) {
      return;
    }
    if ((this.PlayerPageMap.containsKey(player.getName())) && ((this.PlayerPageMap.get(player.getName()) instanceof PetTagPage)))
    {
      if ((message != null) && (message.length >= 1))
      {
        String tagName = new String(message);
        
        ((PetTagPage)this.PlayerPageMap.get(player.getName())).SetTagName(tagName);
      }
    }
  }
}
