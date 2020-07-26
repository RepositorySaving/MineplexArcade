package nautilus.game.arcade.game.games.spleef.kits;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkKnockback;
import nautilus.game.arcade.kit.perks.PerkSmasher;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitBrawler
  extends Kit
{
  public KitBrawler(ArcadeManager manager)
  {
    super(manager, "Brawler", KitAvailability.Green, new String[] {"Much stronger knockback than other kits." }, new Perk[] {new PerkSmasher(), new PerkKnockback(0.6D) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD) });
  }
}
