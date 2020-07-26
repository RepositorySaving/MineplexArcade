package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.item.Item;
import mineplex.minecraft.game.classcombat.shop.page.SkillPage;
import org.bukkit.entity.Player;

public class PurchaseItemButton
  implements IButton
{
  private SkillPage _page;
  private Item _item;
  
  public PurchaseItemButton(SkillPage page, Item item)
  {
    this._page = page;
    this._item = item;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.PurchaseItem(player, this._item);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
