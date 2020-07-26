package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilInv;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class PerkQuickshotRobinHood extends Perk
{
  public PerkQuickshotRobinHood()
  {
    super("Quick Shot", new String[] {C.cYellow + "Left-Click" + C.cGray + " with Bow to " + C.cGreen + "Quick Shot" });
  }
  

  @EventHandler
  public void Leap(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.BOW) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!player.getInventory().contains(Material.ARROW)) {
      return;
    }
    UtilInv.remove(player, Material.ARROW, (byte)1, 1);
    
    Arrow arrow = (Arrow)player.launchProjectile(Arrow.class);
    arrow.setVelocity(player.getLocation().getDirection().multiply(2));
    
    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SHOOT_ARROW, 1.0F, 1.0F);
  }
}
