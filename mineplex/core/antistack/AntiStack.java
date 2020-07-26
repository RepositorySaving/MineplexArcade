package mineplex.core.antistack;

import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import net.minecraft.server.v1_7_R3.ItemStack;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiStack extends MiniPlugin
{
  private boolean _enabled = true;
  
  private HashSet<Location> _ignoreAround = new HashSet();
  
  public AntiStack(JavaPlugin plugin)
  {
    super("AntiStack", plugin);
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void BlockBreak(BlockBreakEvent event)
  {
    if (!this._enabled) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    this._ignoreAround.add(event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D));
  }
  
  @EventHandler
  public void ClearIgnoreAround(UpdateEvent event)
  {
    if (!this._enabled) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    this._ignoreAround.clear();
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void ItemSpawn(ItemSpawnEvent event)
  {
    if (!this._enabled) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    Item item = event.getEntity();
    
    for (Location loc : this._ignoreAround) {
      if (UtilMath.offset(loc, event.getLocation()) < 2.0D) {
        return;
      }
    }
    if (item.getLocation().getY() < -10.0D) {
      return;
    }
    
    String name = ((CraftItemStack)item.getItemStack()).getHandle().getName();
    

    name = name + ":" + item.getUniqueId();
    

    ((CraftItemStack)item.getItemStack()).getHandle().c(name);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void PlayerPickup(PlayerPickupItemEvent event)
  {
    if (!this._enabled) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    Item item = event.getItem();
    

    String name = ((CraftItemStack)item.getItemStack()).getHandle().getName();
    

    if (name.contains(":")) {
      name = name.substring(0, name.indexOf(":" + item.getUniqueId()));
    }
    
    ((CraftItemStack)item.getItemStack()).getHandle().c(name);
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event)
  {
    if (!this._enabled) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    Item item = event.getItem();
    

    String name = ((CraftItemStack)item.getItemStack()).getHandle().getName();
    

    if (name.contains(":")) {
      name = name.substring(0, name.indexOf(":" + item.getUniqueId()));
    }
    
    ((CraftItemStack)item.getItemStack()).getHandle().c(name);
  }
  
  public void SetEnabled(boolean var)
  {
    this._enabled = var;
  }
}
