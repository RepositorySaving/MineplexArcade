package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.shop.page.CustomBuildPage;
import org.bukkit.entity.Player;

public class SelectCustomBuildButton
  implements IButton
{
  private CustomBuildPage _page;
  private CustomBuildToken _customBuild;
  
  public SelectCustomBuildButton(CustomBuildPage page, CustomBuildToken customBuild)
  {
    this._page = page;
    this._customBuild = customBuild;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.SelectCustomBuild(this._customBuild);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
