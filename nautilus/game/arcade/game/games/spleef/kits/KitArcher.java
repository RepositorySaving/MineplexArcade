package nautilus.game.arcade.game.games.spleef.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkKnockback;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitArcher
  extends Kit
{
  public KitArcher(ArcadeManager manager)
  {
    super(manager, "Archer", KitAvailability.Blue, new String[] {"Arrows will damage spleef blocks in a small radius." }, new Perk[] {new PerkFletcher(2, 2, false), new PerkKnockback(0.3D) }, EntityType.SKELETON, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
  }
}
