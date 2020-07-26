package nautilus.game.arcade.game.games.bridge.kits;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBomber;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitBomber
  extends Kit
{
  public KitBomber(ArcadeManager manager)
  {
    super(manager, "Bomber", KitAvailability.Blue, new String[] {"Crazy bomb throwing guy." }, new Perk[] {new PerkBomber(30, 2, -1) }, EntityType.ZOMBIE, new ItemStack(Material.TNT));
  }
  
  public void GiveItems(Player player) {}
}
