package nautilus.game.arcade.managers;

import java.util.List;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.portal.Portal;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;

public class MiscManager implements org.bukkit.event.Listener
{
  private List<String> _dontGiveClockList = new java.util.ArrayList();
  private ArcadeManager Manager;
  
  public MiscManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void InteractActive(PlayerInteractEvent event)
  {
    event.setCancelled(false);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void InteractClickCancel(PlayerInteractEvent event)
  {
    if (this.Manager.GetGame() == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Manager.GetGame().IsAlive(player))
    {
      event.setCancelled(true);

    }
    else if ((event.getPlayer().getItemInHand().getType() == Material.INK_SACK) && (event.getPlayer().getItemInHand().getData().getData() == 15))
    {
      if ((event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) && (this.Manager.GetGame().GetType() != GameType.UHC)) {
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void InventoryClickCancel(InventoryClickEvent event) {
    if (this.Manager.GetGame() == null) {
      return;
    }
    Player player = UtilPlayer.searchExact(event.getWhoClicked().getName());
    
    if ((this.Manager.GetGame().IsLive()) && (!this.Manager.GetGame().IsAlive(player)))
    {
      event.setCancelled(true);
      player.closeInventory();
    }
  }
  
  @EventHandler
  public void addClockPrevent(InventoryOpenEvent event)
  {
    if ((event.getPlayer() instanceof Player))
    {
      this._dontGiveClockList.add(event.getPlayer().getName());
    }
  }
  
  @EventHandler
  public void removeClockPrevent(InventoryCloseEvent event)
  {
    if ((event.getPlayer() instanceof Player))
    {
      this._dontGiveClockList.remove(event.getPlayer().getName());
    }
  }
  
  @EventHandler
  public void HubClockUpdate(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SLOW) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (this.Manager.GetGame().GetType() == GameType.UHC) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (!this.Manager.GetGame().IsAlive(player))
      {
        if ((!this._dontGiveClockList.contains(player.getName())) && (!player.getInventory().contains(Material.WATCH)))
        {
          this.Manager.HubClock(player);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void HubClockInteract(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (player.getItemInHand() == null) {
      return;
    }
    if (player.getItemInHand().getType() != Material.WATCH) {
      return;
    }
    this.Manager.GetPortal().SendPlayerToServer(player, "Lobby");
  }
  
  @EventHandler
  public void HubCommand(PlayerCommandPreprocessEvent event)
  {
    if ((event.getMessage().startsWith("/hub")) || (event.getMessage().startsWith("/leave")))
    {
      this.Manager.GetPortal().SendPlayerToServer(event.getPlayer(), "Lobby");
      event.setCancelled(true);
    }
  }
}
