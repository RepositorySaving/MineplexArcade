package mineplex.core.common;

import org.bukkit.Material;

public enum CurrencyType
{
  Tokens(" Tokens", Material.EMERALD), 
  Coins(" Coins", Material.GOLD_INGOT), 
  Gems("Gems", Material.DIAMOND);
  
  private String _prefix;
  private Material _displayMaterial;
  
  private CurrencyType(String prefix, Material displayMaterial)
  {
    this._prefix = prefix;
    this._displayMaterial = displayMaterial;
  }
  
  public String Prefix()
  {
    return this._prefix;
  }
  
  public Material GetDisplayMaterial()
  {
    return this._displayMaterial;
  }
}
