package mineplex.core.pet.ui;

import mineplex.core.pet.Pet;
import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class ActivatePetButton
  implements IButton
{
  private Pet _pet;
  private PetPage _page;
  
  public ActivatePetButton(Pet pet, PetPage page)
  {
    this._pet = pet;
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.ActivatePet(player, this._pet);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.ActivatePet(player, this._pet);
  }
}
