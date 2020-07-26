package nautilus.game.arcade.game.games.bridge.kits;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkApple;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitApple
  extends Kit
{
  public KitApple(ArcadeManager manager)
  {
    super(manager, "Apple", KitAvailability.Free, new String[] {"Possess the rare skill of finding apples frequently!" }, new Perk[] {new PerkApple(manager) }, EntityType.ZOMBIE, new ItemStack(Material.APPLE));
  }
  
  public void GiveItems(Player player) {}
}
