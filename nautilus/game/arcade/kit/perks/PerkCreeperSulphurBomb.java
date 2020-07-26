package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkCreeperSulphurBomb extends Perk implements mineplex.core.projectile.IThrown
{
  public PerkCreeperSulphurBomb()
  {
    super("Sulphur Bomb", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Sulphur Bomb" });
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
    
    UtilInv.Update(player);
    
    org.bukkit.entity.Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.FIREWORK_CHARGE, (byte)6));
    
    UtilAction.velocity(ent, player.getLocation().getDirection(), 1.0D, false, 0.0D, 0.2D, 10.0D, false);
    
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, 
      null, 1.0F, 1.0F, 
      null, 1, UpdateType.SLOW, 
      2.0D);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.CREEPER_DEATH, 2.0F, 1.5F);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Explode(data);
    
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.PROJECTILE, 4.0D, true, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
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
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.0D);
  }
}
