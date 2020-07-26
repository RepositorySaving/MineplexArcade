package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PerkFood extends Perk
{
  private int _amount;
  
  public PerkFood(int amount)
  {
    super("Strength", new String[] {C.cGray + "Your Hunger is permanently " + amount });
    

    this._amount = amount;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {
        player.setFoodLevel(this._amount);
      }
    }
  }
}
