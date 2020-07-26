package mineplex.core.pet;

import mineplex.core.common.util.NautHashMap;
import mineplex.core.pet.repository.token.ClientPetToken;
import mineplex.core.pet.repository.token.PetToken;
import org.bukkit.entity.EntityType;


public class PetClient
{
  private NautHashMap<EntityType, String> _pets;
  private int _petNameTagCount;
  
  public void Load(ClientPetToken token)
  {
    this._pets = new NautHashMap();
    
    for (PetToken petToken : token.Pets)
    {
      if (petToken.PetName == null) {
        petToken.PetName = ((EntityType)Enum.valueOf(EntityType.class, petToken.PetType)).getName();
      }
      this._pets.put((EntityType)Enum.valueOf(EntityType.class, petToken.PetType), petToken.PetName);
    }
    
    this._petNameTagCount = Math.max(0, token.PetNameTagCount);
  }
  
  public NautHashMap<EntityType, String> GetPets()
  {
    return this._pets;
  }
  
  public Integer GetPetNameTagCount()
  {
    return Integer.valueOf(this._petNameTagCount);
  }
  
  public void SetPetNameTagCount(int count)
  {
    this._petNameTagCount = count;
  }
}
