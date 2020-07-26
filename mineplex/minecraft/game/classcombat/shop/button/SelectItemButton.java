package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.item.Item;
import mineplex.minecraft.game.classcombat.shop.page.SkillPage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SelectItemButton
  implements IButton
{
  private SkillPage _page;
  private Item _item;
  private boolean _canAfford;
  
  public SelectItemButton(SkillPage page, Item item, boolean canAfford)
  {
    this._page = page;
    this._item = item;
    this._canAfford = canAfford;
  }
  

  public void ClickedLeft(Player player)
  {
    if (!this._canAfford)
    {
      player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1.0F, 0.5F);
      return;
    }
    
    this._page.SelectItem(player, this._item);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.DeselectItem(player, this._item);
  }
}
