package mineplex.core.pet.repository;

import java.util.List;
import mineplex.core.pet.repository.token.PetChangeToken;
import mineplex.core.pet.repository.token.PetExtraToken;
import mineplex.core.pet.repository.token.PetSalesToken;
import mineplex.core.server.remotecall.AsyncJsonWebCall;
import mineplex.core.server.remotecall.JsonWebCall;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;



public class PetRepository
{
  private String _webAddress;
  
  public PetRepository(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public List<PetSalesToken> GetPets(List<PetSalesToken> petTokens)
  {
    (List)new JsonWebCall(this._webAddress + "Pets/GetPets").Execute(new TypeToken() {}.getType(), petTokens);
  }
  
  public void AddPet(PetChangeToken token)
  {
    new AsyncJsonWebCall(this._webAddress + "Pets/AddPet").Execute(token);
  }
  
  public void RemovePet(PetChangeToken token)
  {
    new AsyncJsonWebCall(this._webAddress + "Pets/RemovePet").Execute(token);
  }
  
  public List<PetExtraToken> GetPetExtras(List<PetExtraToken> petExtraTokens)
  {
    (List)new JsonWebCall(this._webAddress + "Pets/GetPetExtras").Execute(new TypeToken() {}.getType(), petExtraTokens);
  }
  
  public void UpdatePet(PetChangeToken token)
  {
    new AsyncJsonWebCall(this._webAddress + "Pets/UpdatePet").Execute(token);
  }
  
  public void AddPetNameTag(String name)
  {
    new AsyncJsonWebCall(this._webAddress + "Pets/AddPetNameTag").Execute(name);
  }
  
  public void RemovePetNameTag(String name)
  {
    new AsyncJsonWebCall(this._webAddress + "Pets/RemovePetNameTag").Execute(name);
  }
}
