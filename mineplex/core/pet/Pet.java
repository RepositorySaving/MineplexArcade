package mineplex.core.pet;

import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.pet.repository.token.PetSalesToken;
import mineplex.core.shop.item.SalesPackageBase;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Pet
  extends SalesPackageBase
{
  private String _name;
  private EntityType _petType;
  
  public Pet(String name, EntityType petType, int cost)
  {
    super(name, Material.MONSTER_EGG, (byte)petType.getTypeId(), new String[0]);
    
    this._name = name;
    this._petType = petType;
    this.CurrencyCostMap.put(CurrencyType.Gems, Integer.valueOf(cost));
    
    this.KnownPackage = false;
  }
  
  public EntityType GetPetType()
  {
    return this._petType;
  }
  
  public void Update(PetSalesToken petToken)
  {
    this._name = petToken.Name;
  }
  
  public String GetPetName()
  {
    return this._name;
  }
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
