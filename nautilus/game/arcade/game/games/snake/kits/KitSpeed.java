package nautilus.game.arcade.game.games.snake.kits;

import mineplex.core.common.util.C;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;















public class KitSpeed
  extends Kit
{
  public KitSpeed(ArcadeManager manager)
  {
    super(manager, "Speedy Snake", KitAvailability.Free, new String[] {"Can quickly speed up to take out other snakes.", "", C.cYellow + "Click" + C.cGray + " with Feather to use " + C.cGreen + "Speed Boost" }, new Perk[0], EntityType.SHEEP, new ItemStack(Material.WOOL));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.FEATHER, 0, 6, 
      C.cYellow + C.Bold + "Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Speed Boost") });
  }
}
