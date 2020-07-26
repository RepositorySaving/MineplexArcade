package nautilus.game.arcade.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.perks.PerkNull;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;












public class NullKit
  extends Kit
{
  public NullKit(ArcadeManager manager)
  {
    super(manager, "Null Kit", KitAvailability.Null, new String[] {"It does nothing!" }, new Perk[] {new PerkNull() }, null, null);
  }
  



  public void GiveItems(Player player) {}
  



  public Creature SpawnEntity(Location loc)
  {
    return null;
  }
}
