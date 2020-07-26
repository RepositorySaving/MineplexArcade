package mineplex.minecraft.game.classcombat.shop;

import java.util.HashSet;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.shop.page.CustomBuildPage;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ClassCombatShop extends ShopBase<ClassShopManager>
{
  private NautHashMap<String, ItemStack[]> _playerInventoryMap = new NautHashMap();
  private NautHashMap<String, ItemStack[]> _playerArmorMap = new NautHashMap();
  private IPvpClass _gameClass;
  private boolean _takeAwayStuff;
  
  public ClassCombatShop(ClassShopManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
  {
    super(plugin, clientManager, donationManager, name, new CurrencyType[] { CurrencyType.Gems });
  }
  
  public ClassCombatShop(ClassShopManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name, IPvpClass iPvpClass)
  {
    super(plugin, clientManager, donationManager, name, new CurrencyType[] { CurrencyType.Gems });
    this._gameClass = iPvpClass;
    this._takeAwayStuff = true;
  }
  
  protected void OpenShopForPlayer(Player player)
  {
    if (this._gameClass != null) {
      ((ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player)).SetGameClass(this._gameClass);
    }
    if (this._takeAwayStuff)
    {
      this._playerInventoryMap.put(player.getName(), player.getInventory().getContents());
      this._playerArmorMap.put(player.getName(), player.getInventory().getArmorContents());
    }
  }
  
  public boolean attemptShopOpen(Player player)
  {
    if (!this.OpenedShop.contains(player.getName()))
    {
      if (!CanOpenShop(player)) {
        return false;
      }
      this.OpenedShop.add(player.getName());
      
      OpenShopForPlayer(player);
      
      CustomBuildPage buildPage = new CustomBuildPage((ClassShopManager)this.Plugin, this, this.ClientManager, this.DonationManager, player);
      
      if (!this.PlayerPageMap.containsKey(player.getName()))
      {
        this.PlayerPageMap.put(player.getName(), buildPage);
      }
      
      OpenPageForPlayer(player, buildPage);
      
      return true;
    }
    
    return false;
  }
  

  protected void CloseShopForPlayer(Player player)
  {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
    
    if ((clientClass != null) && (clientClass.IsSavingCustomBuild()))
    {
      CustomBuildToken customBuild = clientClass.GetSavingCustomBuild();
      clientClass.SaveActiveCustomBuild();
      clientClass.SetActiveCustomBuild(clientClass.GetGameClass(), customBuild);
      clientClass.EquipCustomBuild(customBuild, false);
    }
    
    if (this._takeAwayStuff)
    {
      player.getInventory().clear();
      
      player.getInventory().setContents((ItemStack[])this._playerInventoryMap.remove(player.getName()));
      player.getInventory().setArmorContents((ItemStack[])this._playerArmorMap.remove(player.getName()));
      
      ((CraftPlayer)player).getHandle().updateInventory(((CraftPlayer)player).getHandle().defaultContainer);
    }
  }
  

  protected mineplex.core.shop.page.ShopPageBase<ClassShopManager, ? extends ShopBase<ClassShopManager>> BuildPagesFor(Player player)
  {
    return new CustomBuildPage((ClassShopManager)this.Plugin, this, this.ClientManager, this.DonationManager, player);
  }
  
  @EventHandler
  public void clearPlayerFromMaps(PlayerQuitEvent event)
  {
    this._playerInventoryMap.remove(event.getPlayer().getName());
    this._playerArmorMap.remove(event.getPlayer().getName());
  }
}
