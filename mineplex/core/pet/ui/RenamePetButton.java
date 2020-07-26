package mineplex.core.pet.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class RenamePetButton
  implements IButton
{
  private PetPage _page;
  
  public RenamePetButton(PetPage page)
  {
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.renameCurrentPet(player);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.renameCurrentPet(player);
  }
}
