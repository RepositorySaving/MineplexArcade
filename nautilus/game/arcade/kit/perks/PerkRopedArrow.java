package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkRopedArrow extends Perk
{
  private HashSet<Entity> _arrows = new HashSet();
  
  private String _name;
  
  private double _power;
  
  private long _recharge;
  
  public PerkRopedArrow(String name, double power, long recharge)
  {
    super(name, new String[] {C.cYellow + "Left-Click" + C.cGray + " with Bow to " + C.cGreen + name });
    

    this._name = name;
    this._power = power;
    this._recharge = recharge;
  }
  
  @EventHandler
  public void Fire(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
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
    if (!Recharge.Instance.use(player, this._name, this._recharge, true, true)) {
      return;
    }
    
    Arrow arrow = (Arrow)player.launchProjectile(Arrow.class);
    arrow.setVelocity(player.getLocation().getDirection().multiply(2.4D * this._power));
    this._arrows.add(arrow);
    

    UtilPlayer.message(player, F.main("Game", "You fired " + F.skill(this._name) + "."));
  }
  
  @EventHandler
  public void Hit(ProjectileHitEvent event)
  {
    if (!this._arrows.remove(event.getEntity())) {
      return;
    }
    Projectile proj = event.getEntity();
    
    if (proj.getShooter() == null) {
      return;
    }
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }
    Vector vec = UtilAlg.getTrajectory(proj.getShooter(), proj);
    double mult = proj.getVelocity().length() / 3.0D;
    

    mineplex.core.common.util.UtilAction.velocity(proj.getShooter(), vec, 
      0.4D + mult * this._power, false, 0.0D, 0.6D * mult * this._power, 1.2D * mult * this._power, true);
    

    proj.getWorld().playSound(proj.getLocation(), Sound.ARROW_HIT, 2.5F, 0.5F);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Entity> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Entity arrow = (Entity)arrowIterator.next();
      
      if (!arrow.isValid()) {
        arrowIterator.remove();
      }
    }
  }
}
