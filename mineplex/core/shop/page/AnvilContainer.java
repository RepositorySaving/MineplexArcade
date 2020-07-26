package mineplex.core.shop.page;

import net.minecraft.server.v1_7_R3.Container;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.IInventory;
import net.minecraft.server.v1_7_R3.InventoryLargeChest;
import net.minecraft.server.v1_7_R3.PlayerInventory;
import net.minecraft.server.v1_7_R3.Slot;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventoryView;

public class AnvilContainer
  extends Container
{
  public IInventory _container;
  private CraftInventoryView _bukkitEntity = null;
  private PlayerInventory _playerInventory;
  
  public AnvilContainer(PlayerInventory playerInventory, IInventory anvilInventory)
  {
    this._playerInventory = playerInventory;
    this._container = anvilInventory;
    
    a(new Slot(anvilInventory, 0, 27, 47));
    a(new Slot(anvilInventory, 1, 76, 47));
    a(new Slot(anvilInventory, 2, 134, 47));
    
    for (int l = 0; l < 3; l++)
    {
      for (int i1 = 0; i1 < 9; i1++)
      {
        a(new Slot(playerInventory, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
      }
    }
    
    for (int l = 0; l < 9; l++) {
      a(new Slot(playerInventory, l, 8 + l * 18, 142));
    }
  }
  
  public CraftInventoryView getBukkitView()
  {
    if (this._bukkitEntity != null)
      return this._bukkitEntity;
    CraftInventory inventory;
    CraftInventory inventory;
    if ((this._container instanceof PlayerInventory))
    {
      inventory = new CraftInventoryPlayer((PlayerInventory)this._container);
    }
    else {
      CraftInventory inventory;
      if ((this._container instanceof InventoryLargeChest)) {
        inventory = new CraftInventoryDoubleChest((InventoryLargeChest)this._container);
      } else {
        inventory = new CraftInventory(this._container);
      }
    }
    this._bukkitEntity = new CraftInventoryView(this._playerInventory.player.getBukkitEntity(), inventory, this);
    
    return this._bukkitEntity;
  }
  

  public boolean a(EntityHuman arg0)
  {
    return true;
  }
}
