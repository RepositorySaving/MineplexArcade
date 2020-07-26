package mineplex.core.pet.ui;

import mineplex.core.pet.Pet;
import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class DeactivatePetButton
  implements IButton
{
  private Pet _pet;
  private PetPage _page;
  
  public DeactivatePetButton(Pet pet, PetPage page)
  {
    this._pet = pet;
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.DeactivatePet(player);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.DeactivatePet(player);
  }
}
