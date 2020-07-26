package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.shop.page.SkillPage;
import org.bukkit.entity.Player;

public class PurchaseSkillButton
  implements IButton
{
  private SkillPage _page;
  private ISkill _skill;
  
  public PurchaseSkillButton(SkillPage page, ISkill skill)
  {
    this._page = page;
    this._skill = skill;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.PurchaseSkill(player, this._skill);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
