package nautilus.game.arcade.game.games.deathtag.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkKnockback;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitRunnerTraitor
  extends Kit
{
  public KitRunnerTraitor(ArcadeManager manager)
  {
    super(manager, "Runner Traitor", KitAvailability.Blue, new String[] {"You can deal knockback to other runners!" }, new Perk[] {new PerkKnockback(0.8D) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
  }
}
