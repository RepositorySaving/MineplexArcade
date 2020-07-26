package nautilus.game.arcade.game.games.dragonescape.kits;

import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkDisruptor;
import nautilus.game.arcade.kit.perks.PerkLeap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitDisruptor
  extends Kit
{
  public KitDisruptor(ArcadeManager manager)
  {
    super(manager, "Disruptor", KitAvailability.Green, new String[] {"Place mini-explosives to stop other players!" }, new Perk[] {new PerkLeap("Leap", 1.0D, 1.0D, 8000L, 3), new PerkDisruptor(8, 2) }, EntityType.ZOMBIE, new ItemStack(Material.TNT));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.TNT, 0, 1, F.item("Disruptor")) });
    player.setExp(0.99F);
  }
}
