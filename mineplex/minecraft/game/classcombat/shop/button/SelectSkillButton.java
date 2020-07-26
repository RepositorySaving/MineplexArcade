package mineplex.minecraft.game.classcombat.shop.button;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.shop.page.SkillPage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SelectSkillButton
  implements IButton
{
  private SkillPage _page;
  private ISkill _skill;
  private int _level;
  private boolean _canAfford;
  
  public SelectSkillButton(SkillPage page, ISkill skill, int level, boolean canAfford)
  {
    this._page = page;
    this._skill = skill;
    this._level = level;
    this._canAfford = canAfford;
  }
  

  public void ClickedLeft(Player player)
  {
    if (!this._canAfford)
    {
      player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1.0F, 0.5F);
      return;
    }
    
    this._page.SelectSkill(player, this._skill, this._level);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.DeselectSkill(player, this._skill);
  }
}
