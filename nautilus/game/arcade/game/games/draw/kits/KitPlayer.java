package nautilus.game.arcade.game.games.draw.kits;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;













public class KitPlayer
  extends Kit
{
  public KitPlayer(ArcadeManager manager)
  {
    super(manager, "Player", KitAvailability.Free, new String[] {"" }, new Perk[0], EntityType.SKELETON, null);
  }
  
  public void GiveItems(Player player) {}
}
