package mineplex.core.shop.item;

import org.bukkit.entity.Player;

public abstract interface IButton
{
  public abstract void ClickedLeft(Player paramPlayer);
  
  public abstract void ClickedRight(Player paramPlayer);
}
