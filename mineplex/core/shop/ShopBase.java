package mineplex.core.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.page.ShopPageBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;



public abstract class ShopBase<PluginType extends MiniPlugin>
  implements Listener
{
  private NautHashMap<String, Long> _errorThrottling;
  private NautHashMap<String, Long> _purchaseBlock;
  private List<CurrencyType> _availableCurrencyTypes;
  protected PluginType Plugin;
  protected CoreClientManager ClientManager;
  protected DonationManager DonationManager;
  protected String Name;
  protected NautHashMap<String, ShopPageBase<PluginType, ? extends ShopBase<PluginType>>> PlayerPageMap;
  protected HashSet<String> OpenedShop = new HashSet();
  
  public ShopBase(PluginType plugin, CoreClientManager clientManager, DonationManager donationManager, String name, CurrencyType... currencyTypes)
  {
    this.Plugin = plugin;
    this.ClientManager = clientManager;
    this.DonationManager = donationManager;
    this.Name = name;
    
    this.PlayerPageMap = new NautHashMap();
    this._errorThrottling = new NautHashMap();
    this._purchaseBlock = new NautHashMap();
    
    this._availableCurrencyTypes = new ArrayList();
    this._availableCurrencyTypes.addAll(Arrays.asList(currencyTypes));
    
    this.Plugin.RegisterEvents(this);
  }
  
  public List<CurrencyType> GetAvailableCurrencyTypes()
  {
    return this._availableCurrencyTypes;
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void OnPlayerDamageEntity(EntityDamageByEntityEvent event)
  {
    if ((event.getEntity() instanceof LivingEntity))
    {
      if ((event.getDamager() instanceof Player))
      {
        if (AttemptShopOpen((Player)event.getDamager(), (LivingEntity)event.getEntity()))
        {
          event.setCancelled(true);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void OnPlayerInteractEntity(PlayerInteractEntityEvent event)
  {
    if ((event.getRightClicked() instanceof LivingEntity))
    {
      if (AttemptShopOpen(event.getPlayer(), (LivingEntity)event.getRightClicked())) {
        event.setCancelled(true);
      }
    }
  }
  
  private boolean AttemptShopOpen(Player player, LivingEntity entity) {
    if ((!this.OpenedShop.contains(player.getName())) && (entity.isCustomNameVisible()) && (entity.getCustomName() != null) && (ChatColor.stripColor(entity.getCustomName()).equalsIgnoreCase(ChatColor.stripColor(this.Name))))
    {
      if (!CanOpenShop(player)) {
        return false;
      }
      this.OpenedShop.add(player.getName());
      
      OpenShopForPlayer(player);
      if (!this.PlayerPageMap.containsKey(player.getName()))
      {
        this.PlayerPageMap.put(player.getName(), BuildPagesFor(player));
      }
      
      OpenPageForPlayer(player, GetOpeningPageForPlayer(player));
      
      return true;
    }
    
    return false;
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
      if (!this.PlayerPageMap.containsKey(player.getName()))
      {
        this.PlayerPageMap.put(player.getName(), BuildPagesFor(player));
      }
      
      OpenPageForPlayer(player, GetOpeningPageForPlayer(player));
      
      return true;
    }
    
    return false;
  }
  
  protected ShopPageBase<PluginType, ? extends ShopBase<PluginType>> GetOpeningPageForPlayer(Player player)
  {
    return (ShopPageBase)this.PlayerPageMap.get(player.getName());
  }
  
  @EventHandler
  public void OnInventoryClick(InventoryClickEvent event)
  {
    if ((this.PlayerPageMap.containsKey(event.getWhoClicked().getName())) && (((ShopPageBase)this.PlayerPageMap.get(event.getWhoClicked().getName())).getName().equalsIgnoreCase(event.getInventory().getName())))
    {
      ((ShopPageBase)this.PlayerPageMap.get(event.getWhoClicked().getName())).PlayerClicked(event);
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void OnInventoryClose(InventoryCloseEvent event)
  {
    if ((this.PlayerPageMap.containsKey(event.getPlayer().getName())) && (((ShopPageBase)this.PlayerPageMap.get(event.getPlayer().getName())).getTitle() != null) && (((ShopPageBase)this.PlayerPageMap.get(event.getPlayer().getName())).getTitle().equalsIgnoreCase(event.getInventory().getTitle())))
    {
      ((ShopPageBase)this.PlayerPageMap.get(event.getPlayer().getName())).PlayerClosed();
      ((ShopPageBase)this.PlayerPageMap.get(event.getPlayer().getName())).Dispose();
      
      this.PlayerPageMap.remove(event.getPlayer().getName());
      
      CloseShopForPlayer((Player)event.getPlayer());
      
      this.OpenedShop.remove(event.getPlayer().getName());
    }
  }
  
  protected boolean CanOpenShop(Player player)
  {
    return true;
  }
  
  protected void OpenShopForPlayer(Player player) {}
  
  protected void CloseShopForPlayer(Player player) {}
  
  @EventHandler
  public void OnPlayerQuit(PlayerQuitEvent event)
  {
    if (this.PlayerPageMap.containsKey(event.getPlayer().getName()))
    {
      ((ShopPageBase)this.PlayerPageMap.get(event.getPlayer().getName())).PlayerClosed();
      ((ShopPageBase)this.PlayerPageMap.get(event.getPlayer().getName())).Dispose();
      
      event.getPlayer().closeInventory();
      CloseShopForPlayer(event.getPlayer());
      
      this.PlayerPageMap.remove(event.getPlayer().getName());
      
      this.OpenedShop.remove(event.getPlayer().getName());
    }
  }
  
  public void OpenPageForPlayer(Player player, ShopPageBase<PluginType, ? extends ShopBase<PluginType>> page)
  {
    if (this.PlayerPageMap.containsKey(player.getName()))
    {
      ((ShopPageBase)this.PlayerPageMap.get(player.getName())).PlayerClosed();
    }
    
    SetCurrentPageForPlayer(player, page);
    
    player.openInventory(page);
  }
  
  public void SetCurrentPageForPlayer(Player player, ShopPageBase<PluginType, ? extends ShopBase<PluginType>> page)
  {
    this.PlayerPageMap.put(player.getName(), page);
  }
  
  public void AddPlayerProcessError(Player player)
  {
    if ((this._errorThrottling.containsKey(player.getName())) && (System.currentTimeMillis() - ((Long)this._errorThrottling.get(player.getName())).longValue() <= 5000L)) {
      this._purchaseBlock.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
    }
    this._errorThrottling.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
  }
  
  public boolean CanPlayerAttemptPurchase(Player player)
  {
    return (!this._purchaseBlock.containsKey(player.getName())) || (System.currentTimeMillis() - ((Long)this._purchaseBlock.get(player.getName())).longValue() > 10000L);
  }
  
  public NautHashMap<String, ShopPageBase<PluginType, ? extends ShopBase<PluginType>>> GetPageMap()
  {
    return this.PlayerPageMap;
  }
  
  protected abstract ShopPageBase<PluginType, ? extends ShopBase<PluginType>> BuildPagesFor(Player paramPlayer);
}
