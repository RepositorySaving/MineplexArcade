package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkMagmaBlast extends Perk
{
  public HashMap<LargeFireball, Location> _proj = new HashMap();
  


  public PerkMagmaBlast()
  {
    super("Magma Blast", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Magma Blast" });
  }
  


  @EventHandler
  public void Shoot(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 6000L, true, true)) {
      return;
    }
    event.setCancelled(true);
    

    LargeFireball ball = (LargeFireball)player.launchProjectile(LargeFireball.class);
    ball.setShooter(player);
    ball.setIsIncendiary(false);
    ball.setYield(0.0F);
    ball.setBounce(false);
    ball.teleport(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)));
    ball.setVelocity(new Vector(0, 0, 0));
    

    UtilAction.velocity(player, player.getLocation().getDirection().multiply(-1), 1.2D, false, 0.0D, 0.2D, 1.2D, true);
    

    this._proj.put(ball, player.getLocation());
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.CREEPER_DEATH, 2.0F, 1.5F);
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<LargeFireball> projIterator = this._proj.keySet().iterator();
    
    while (projIterator.hasNext())
    {
      LargeFireball proj = (LargeFireball)projIterator.next();
      
      if (!proj.isValid())
      {
        projIterator.remove();
        proj.remove();
      }
      else
      {
        proj.setDirection(((Location)this._proj.get(proj)).clone().getDirection());
        proj.setVelocity(((Location)this._proj.get(proj)).clone().getDirection().multiply(0.6D));
      }
    }
  }
  
  @EventHandler
  public void Collide(ProjectileHitEvent event) {
    Projectile proj = event.getEntity();
    
    if (!(proj instanceof LargeFireball)) {
      return;
    }
    if (proj.getShooter() == null) {
      return;
    }
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }
    
    HashMap<Player, Double> hitMap = UtilPlayer.getInRadius(proj.getLocation(), 8.0D);
    for (Player cur : hitMap.keySet())
    {
      double range = ((Double)hitMap.get(cur)).doubleValue();
      

      UtilAction.velocity(cur, UtilAlg.getTrajectory(proj.getLocation().add(0.0D, -0.5D, 0.0D), cur.getEyeLocation()), 
        1.0D + 2.0D * range, false, 0.0D, 0.2D + 0.4D * range, 1.2D, true);
    }
    

    UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, proj.getLocation(), 0.1F, 0.1F, 0.1F, 0.1F, 50);
  }
}
