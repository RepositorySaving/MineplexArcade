package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkBaconBlast extends Perk implements IThrown
{
  public PerkBaconBlast()
  {
    super("Bacon Blast", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Bacon Blast" });
  }
  


  @org.bukkit.event.EventHandler
  public void Shoot(PlayerInteractEvent event)
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
    
    UtilInv.Update(player);
    
    org.bukkit.entity.Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.PORK, (byte)0, 16));
    
    mineplex.core.common.util.UtilAction.velocity(ent, player.getLocation().getDirection(), 1.0D, false, 0.0D, 0.2D, 10.0D, false);
    
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, 
      null, 1.0F, 1.0F, 
      null, 1, UpdateType.SLOW, 
      2.0D);
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.PIG_IDLE, 2.0F, 2.0F);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Explode(data);
    
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.PROJECTILE, 6.0D, true, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
  }
  

  public void Idle(ProjectileUser data)
  {
    Explode(data);
  }
  

  public void Expire(ProjectileUser data)
  {
    Explode(data);
  }
  
  public void Explode(ProjectileUser data)
  {
    data.GetThrown().getWorld().createExplosion(data.GetThrown().getLocation(), 0.5F);
    data.GetThrown().remove();
  }
}
