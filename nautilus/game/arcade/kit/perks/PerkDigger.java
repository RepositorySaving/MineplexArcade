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

public class PerkDigger extends Perk
{
  public PerkDigger()
  {
    super("Digger", new String[] {C.cGray + "Permanent Fast Digging II" });
  }
  

  @org.bukkit.event.EventHandler
  public void DigSpeed(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        this.Manager.GetCondition().Factory().DigFast(GetName(), player, player, 1.9D, 1, false, false, true);
      }
    }
  }
}
