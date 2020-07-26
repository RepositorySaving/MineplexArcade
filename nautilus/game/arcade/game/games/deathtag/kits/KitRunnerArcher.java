package nautilus.game.arcade.game.games.deathtag.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkKnockbackArrow;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitRunnerArcher
  extends Kit
{
  public KitRunnerArcher(ArcadeManager manager)
  {
    super(manager, "Runner Archer", KitAvailability.Green, new String[] {"Fight off the Chasers with Arrows!" }, new Perk[] {new PerkKnockbackArrow(3.0D), new PerkFletcher(2, 2, true) }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
  }
}
