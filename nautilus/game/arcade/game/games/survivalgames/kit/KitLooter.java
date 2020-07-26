package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkLooter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitLooter
  extends Kit
{
  public KitLooter(ArcadeManager manager)
  {
    super(manager, "Looter", KitAvailability.Free, new String[] {"Defeat your opponents with your swag loots!" }, new Perk[] {new PerkLooter() }, EntityType.ZOMBIE, new ItemStack(Material.CHEST));
  }
  
  public void GiveItems(Player player) {}
}
