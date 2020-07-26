package mineplex.core.shop.item;

import org.bukkit.Material;

public abstract interface IDisplayPackage
{
  public abstract String GetName();
  
  public abstract String[] GetDescription();
  
  public abstract Material GetDisplayMaterial();
  
  public abstract byte GetDisplayData();
}
