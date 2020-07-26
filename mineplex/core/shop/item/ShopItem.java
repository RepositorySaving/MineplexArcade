package mineplex.core.shop.item;

import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagList;
import net.minecraft.server.v1_7_R3.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;

public class ShopItem
  extends CraftItemStack
{
  protected String _name;
  private String _deliveryName;
  protected String[] _lore;
  private int _deliveryAmount;
  private boolean _locked;
  private boolean _displayItem;
  
  public ShopItem(org.bukkit.inventory.ItemStack itemStack, String name, String deliveryName, int deliveryAmount, boolean locked, boolean displayItem)
  {
    super(itemStack);
    
    this._name = name;
    this._deliveryName = deliveryName;
    this._displayItem = displayItem;
    this._deliveryAmount = deliveryAmount;
    
    getHandle().tag = ((CraftItemStack)itemStack).getHandle().tag;
    
    UpdateVisual(true);
    getHandle().tag.set("AttributeModifiers", new NBTTagList());
  }
  
  public ShopItem(Material type, String name, int deliveryAmount, boolean locked)
  {
    this(type, name, null, deliveryAmount, locked);
  }
  
  public ShopItem(Material type, String name, String[] lore, int deliveryAmount, boolean locked)
  {
    this(type, name, lore, deliveryAmount, locked, false);
  }
  
  public ShopItem(Material type, String name, String[] lore, int deliveryAmount, boolean locked, boolean displayItem)
  {
    this(type, (byte)0, name, null, lore, deliveryAmount, locked, displayItem);
  }
  
  public ShopItem(Material type, byte data, String name, String[] lore, int deliveryAmount, boolean locked, boolean displayItem)
  {
    this(type, data, name, null, lore, deliveryAmount, locked, displayItem);
  }
  
  public ShopItem(Material type, byte data, String name, String deliveryName, String[] lore, int deliveryAmount, boolean locked, boolean displayItem)
  {
    super(type.getId(), Math.max(deliveryAmount, 1), data, null);
    
    this._name = name;
    this._deliveryName = deliveryName;
    this._lore = lore;
    this._displayItem = displayItem;
    this._deliveryAmount = deliveryAmount;
    this._locked = locked;
    
    UpdateVisual(false);
    




    getHandle().tag.setByte("Count", (byte)Math.max(deliveryAmount, 1));
    getHandle().tag.set("AttributeModifiers", new NBTTagList());
  }
  
  public boolean IsLocked()
  {
    return this._locked;
  }
  
  public void SetDeliverySettings()
  {
    setAmount(this._deliveryAmount);
    

    if (this._deliveryName != null) {
      getHandle().c(this._deliveryName);
    }
  }
  
  public ShopItem clone() {
    return new ShopItem(super.clone(), this._name, this._deliveryName, this._deliveryAmount, this._locked, this._displayItem);
  }
  

  public boolean equals(Object obj)
  {
    if (!super.equals(obj))
    {
      return false;
    }
    
    net.minecraft.server.v1_7_R3.ItemStack original = getHandle();
    net.minecraft.server.v1_7_R3.ItemStack comparison = ((CraftItemStack)obj).getHandle();
    
    return (original.tag == null) || (original.tag.equals(comparison.tag));
  }
  
  protected void UpdateVisual(boolean clone)
  {
    if (!clone)
    {
      if ((this._locked) && (!this._displayItem))
      {
        getHandle().c(ChatColor.RED + "§l" + this._name);
      }
      else
      {
        getHandle().c(ChatColor.GREEN + "§l" + this._name);
      }
    }
    
    NBTTagList lore = new NBTTagList();
    
    if (this._lore != null)
    {
      for (String line : this._lore)
      {
        if ((line != null) && (!line.isEmpty())) {
          lore.add(new NBTTagString(line));
        }
      }
    }
    getHandle().tag.getCompound("display").set("Lore", lore);
  }
  
  public boolean IsDisplay()
  {
    return this._displayItem;
  }
  
  public void SetLocked(boolean owns)
  {
    this._locked = owns;
    UpdateVisual(false);
  }
  
  public String GetName()
  {
    return this._name;
  }
  
  public void SetName(String name)
  {
    this._name = name;
  }
  
  public void SetLore(String[] string)
  {
    this._lore = string;
    
    NBTTagList lore = new NBTTagList();
    
    if (this._lore != null)
    {
      for (String line : this._lore)
      {
        if ((line != null) && (!line.isEmpty())) {
          lore.add(new NBTTagString(line));
        }
      }
    }
    getHandle().tag.getCompound("display").set("Lore", lore);
  }
}
