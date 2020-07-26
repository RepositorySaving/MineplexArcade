package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkExplode extends Perk
{
  private String _name;
  private double _scale;
  private long _recharge;
  
  public PerkExplode(String name, double scale, long recharge)
  {
    super("Explosive", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + name });
    

    this._name = name;
    this._scale = scale;
    this._recharge = recharge;
  }
  
  @EventHandler
  public void Leap(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK) && 
      (event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.TNT) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, this._name, this._recharge, true, false)) {
      return;
    }
    

    HashMap<Player, Double> hit = UtilPlayer.getInRadius(player.getLocation(), 8.0D);
    for (Player other : hit.keySet())
    {

      UtilAction.velocity(other, UtilAlg.getTrajectory(player.getLocation(), 
        other.getEyeLocation()), this._scale * 2.4D * ((Double)hit.get(other)).doubleValue(), false, 0.0D, 0.2D + this._scale * 0.6D * ((Double)hit.get(other)).doubleValue(), 1.6D, true);
      

      this.Manager.GetDamage().NewDamageEvent(other, player, null, 
        EntityDamageEvent.DamageCause.CUSTOM, this._scale * 40.0D * ((Double)hit.get(other)).doubleValue(), false, true, false, 
        UtilEnt.getName(player), GetName());
      

      UtilPlayer.message(other, F.main(GetName(), F.name(UtilEnt.getName(player)) + " hit you with " + F.item(GetName()) + "."));
    }
    

    player.getWorld().createExplosion(player.getLocation(), 0.0F);
  }
}
