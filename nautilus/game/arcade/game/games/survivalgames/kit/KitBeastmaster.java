package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkWolfPet;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitBeastmaster
  extends Kit
{
  public KitBeastmaster(ArcadeManager manager)
  {
    super(manager, "Beastmaster", KitAvailability.Blue, new String[] {"Woof woof woof!!" }, new Perk[] {new PerkWolfPet(30, 1, false, true) }, EntityType.ZOMBIE, new ItemStack(Material.BONE));
  }
  
  public void GiveItems(Player player) {}
}
