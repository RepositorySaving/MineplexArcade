package nautilus.game.arcade.game.games.survivalgames.kit;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBarrage;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;













public class KitArcher
  extends Kit
{
  public KitArcher(ArcadeManager manager)
  {
    super(manager, "Archer", KitAvailability.Green, new String[] {"Passively crafts arrows from surrounding terrain." }, new Perk[] {new PerkFletcher(20, 3, true), new PerkBarrage(5, 250L, true, false) }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  
  public void GiveItems(Player player) {}
}
