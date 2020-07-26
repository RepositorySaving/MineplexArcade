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















public class KitInvulnerable
  extends Kit
{
  public KitInvulnerable(ArcadeManager manager)
  {
    super(manager, "Super Snake", KitAvailability.Blue, new String[] {"Able to temporarily travel through tails.", "", C.cYellow + "Click" + C.cGray + " with Wool to use " + C.cGreen + "Invulnerability" }, new Perk[0], EntityType.SHEEP, new ItemStack(Material.WOOL));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WOOL, 0, 2, 
      C.cYellow + C.Bold + "Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Invulnerability") });
  }
}
