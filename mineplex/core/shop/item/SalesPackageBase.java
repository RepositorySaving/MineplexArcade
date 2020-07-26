package mineplex.core.shop.item;

import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.repository.GameSalesPackageToken;
import org.bukkit.Material;
import org.bukkit.entity.Player;






public abstract class SalesPackageBase
  implements ICurrencyPackage, IDisplayPackage
{
  private Material _displayMaterial;
  private byte _displayData;
  private String _name;
  private String _displayName;
  private String[] _description;
  protected int SalesPackageId;
  protected boolean Free;
  protected NautHashMap<CurrencyType, Integer> CurrencyCostMap;
  protected boolean KnownPackage = true;
  protected boolean OneTimePurchaseOnly = true;
  
  public SalesPackageBase(String name, Material material, String... description)
  {
    this(name, material, (byte)0, description);
  }
  
  public SalesPackageBase(String name, Material material, byte displayData, String[] description)
  {
    this(name, material, (byte)0, description, -1);
  }
  
  public SalesPackageBase(String name, Material material, byte displayData, String[] description, int gems)
  {
    this.CurrencyCostMap = new NautHashMap();
    
    this._name = name;
    this._displayName = name;
    this._description = description;
    this._displayMaterial = material;
    this._displayData = displayData;
    
    this.CurrencyCostMap.put(CurrencyType.Gems, Integer.valueOf(gems));
  }
  

  public abstract void Sold(Player paramPlayer, CurrencyType paramCurrencyType);
  
  public String GetName()
  {
    return this._name;
  }
  

  public String[] GetDescription()
  {
    return this._description;
  }
  

  public int GetCost(CurrencyType currencyType)
  {
    return this.CurrencyCostMap.containsKey(currencyType) ? ((Integer)this.CurrencyCostMap.get(currencyType)).intValue() : 0;
  }
  

  public int GetSalesPackageId()
  {
    return this.SalesPackageId;
  }
  

  public boolean IsFree()
  {
    return this.Free;
  }
  

  public Material GetDisplayMaterial()
  {
    return this._displayMaterial;
  }
  

  public byte GetDisplayData()
  {
    return this._displayData;
  }
  

  public void Update(GameSalesPackageToken token)
  {
    this.SalesPackageId = token.GameSalesPackageId.intValue();
    this.Free = token.Free;
    
    if (token.Gems.intValue() > 0)
    {
      this.CurrencyCostMap.put(CurrencyType.Gems, token.Gems);
    }
  }
  
  public boolean IsKnown()
  {
    return this.KnownPackage;
  }
  
  public boolean OneTimePurchase()
  {
    return this.OneTimePurchaseOnly;
  }
  
  public String GetDisplayName()
  {
    return this._displayName;
  }
  
  public void setDisplayName(String name)
  {
    this._displayName = name;
  }
}
