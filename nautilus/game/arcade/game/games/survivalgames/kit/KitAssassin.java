package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBackstab;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitAssassin
  extends Kit
{
  public KitAssassin(ArcadeManager manager)
  {
    super(manager, "Assassin", KitAvailability.Blue, new String[] {"Sneak up on opponents while they're looting chests!" }, new Perk[] {new PerkBackstab() }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  
  public void GiveItems(Player player) {}
}
