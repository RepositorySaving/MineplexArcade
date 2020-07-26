package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBomberHG;
import nautilus.game.arcade.kit.perks.PerkTNTArrow;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitBomber
  extends Kit
{
  public KitBomber(ArcadeManager manager)
  {
    super(manager, "Bomber", KitAvailability.Blue, new String[] {"BOOM! BOOM! BOOM!" }, new Perk[] {new PerkBomberHG(30, 2), new PerkTNTArrow() }, EntityType.ZOMBIE, new ItemStack(Material.TNT));
  }
  
  public void GiveItems(Player player) {}
}
