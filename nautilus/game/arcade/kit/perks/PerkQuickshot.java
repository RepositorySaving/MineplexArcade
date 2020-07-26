package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkQuickshot extends Perk
{
  private String _name;
  private double _power;
  private long _recharge;
  
  public PerkQuickshot(String name, double power, long recharge)
  {
    super("Quickshot", new String[] {C.cYellow + "Left-Click" + C.cGray + " with Bow to " + C.cGreen + name });
    

    this._name = name;
    this._power = power;
    this._recharge = recharge;
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
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != org.bukkit.Material.BOW) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, this._name, this._recharge, true, true)) {
      return;
    }
    Arrow arrow = (Arrow)player.launchProjectile(Arrow.class);
    arrow.setVelocity(player.getLocation().getDirection().multiply(this._power));
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(this._name) + "."));
  }
}
