package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.item.Item;
import mineplex.minecraft.game.classcombat.shop.page.SkillPage;
import org.bukkit.entity.Player;

public class DeselectItemButton
  implements IButton
{
  private SkillPage _page;
  private Item _item;
  private int _index;
  
  public DeselectItemButton(SkillPage page, Item item, int index)
  {
    this._page = page;
    this._item = item;
    this._index = index;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.DeselectItem(player, this._item, this._index);
  }
  


  public void ClickedRight(Player player)
  {
    this._page.DeselectItem(player, this._item, this._index);
  }
}
