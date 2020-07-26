package mineplex.core.pet;

import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.pet.repository.token.PetExtraToken;
import mineplex.core.shop.item.SalesPackageBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;


public class PetExtra
  extends SalesPackageBase
{
  private String _name;
  private Material _material;
  
  public PetExtra(String name, Material material, int cost)
  {
    super(name, material, (byte)0, new String[0]);
    
    this._name = name;
    this._material = material;
    this.CurrencyCostMap.put(CurrencyType.Gems, Integer.valueOf(cost));
    
    this.KnownPackage = false;
    this.OneTimePurchaseOnly = false;
  }
  


  public void Update(PetExtraToken token) {}
  

  public String GetName()
  {
    return this._name;
  }
  
  public Material GetMaterial()
  {
    return this._material;
  }
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
