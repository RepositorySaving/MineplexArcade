package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;

public class PerkRegeneration extends Perk
{
  private int _level;
  
  public PerkRegeneration(int level)
  {
    super("Regeneration", new String[] {C.cGray + "Permanent Regeneration " + (level + 1) });
    

    this._level = level;
  }
  
  @org.bukkit.event.EventHandler
  public void DigSpeed(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SLOW) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        this.Manager.GetCondition().Factory().Regen(GetName(), player, player, 8.0D, this._level, false, false, true);
      }
    }
  }
}
