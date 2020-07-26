package nautilus.game.arcade.kit.perks;

import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkWebShot extends Perk implements mineplex.core.projectile.IThrown
{
  public PerkWebShot()
  {
    super("Web Shot", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Web Shot" });
  }
  


  @EventHandler
  public void ShootWeb(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
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
    if (!Recharge.Instance.use(player, GetName(), 3000L, true, true)) {
      return;
    }
    event.setCancelled(true);
    
    UtilInv.remove(player, Material.WEB, (byte)0, 1);
    UtilInv.Update(player);
    
    Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.WEB));
    UtilAction.velocity(ent, player.getLocation().getDirection(), 1.0D, false, 0.0D, 0.2D, 10.0D, false);
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, false, 2.0D);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SPIDER_IDLE, 1.0F, 0.6F);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target != null)
    {
      data.GetThrown().remove();
      
      this.Manager.GetBlockRestore().Add(target.getLocation().getBlock(), 30, (byte)0, 2500L);
      
      return;
    }
    
    Web(data);
  }
  

  public void Idle(ProjectileUser data)
  {
    Web(data);
  }
  

  public void Expire(ProjectileUser data)
  {
    Web(data);
  }
  
  public void Web(ProjectileUser data)
  {
    Location loc = data.GetThrown().getLocation();
    data.GetThrown().remove();
    
    this.Manager.GetBlockRestore().Add(loc.getBlock(), 30, (byte)0, 2500L);
  }
}
