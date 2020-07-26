package mineplex.core.pet.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;


public class CloseButton
  implements IButton
{
  public void ClickedLeft(Player player)
  {
    player.closeInventory();
  }
  

  public void ClickedRight(Player player)
  {
    player.closeInventory();
  }
}
