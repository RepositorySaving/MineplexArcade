package nautilus.game.arcade.game.games.uhc;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;















public class KitUHC
  extends Kit
{
  public KitUHC(ArcadeManager manager)
  {
    super(manager, "UHC Player", KitAvailability.Free, new String[] {"A really unfortunate guy, who has been", "forced to fight to the death against", "a bunch of other guys." }, new Perk[0], EntityType.ZOMBIE, null);
  }
  
  public void GiveItems(Player player) {}
}
