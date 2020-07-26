package nautilus.game.arcade.game.games.snake.kits;

import mineplex.core.common.util.C;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

















public class KitOther
  extends Kit
{
  public KitOther(ArcadeManager manager)
  {
    super(manager, "Other Snake", KitAvailability.Blue, new String[] {"Does something else?", "", C.cYellow + "Click" + C.cGray + " with Wool to use " + C.cGreen + "Other" }, new Perk[0], EntityType.SHEEP, new ItemStack(Material.WOOL));
  }
  
  public void GiveItems(Player player) {}
}
