package mineplex.core.pet.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectTagButton
  implements IButton
{
  private PetTagPage _page;
  
  public SelectTagButton(PetTagPage page)
  {
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.SelectTag();
  }
  

  public void ClickedRight(Player player)
  {
    this._page.SelectTag();
  }
}
