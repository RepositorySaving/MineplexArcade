package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkIronHook extends Perk implements mineplex.core.projectile.IThrown
{
  public PerkIronHook()
  {
    super("Iron Hook", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Iron Hook" });
  }
  

  @EventHandler
  public void Activate(PlayerInteractEvent event)
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    
    Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(131));
    UtilAction.velocity(item, player.getLocation().getDirection(), 
      1.6D, false, 0.0D, 0.2D, 10.0D, false);
    
    this.Manager.GetProjectile().AddThrow(item, player, this, -1L, true, true, true, 
      Sound.FIRE_IGNITE, 1.4F, 0.8F, mineplex.core.common.util.UtilParticle.ParticleType.CRIT, null, 0, mineplex.core.updater.UpdateType.TICK, 2.0D);
    

    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
    

    item.getWorld().playSound(item.getLocation(), Sound.IRONGOLEM_THROW, 2.0F, 0.8F);
  }
  


  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    double velocity = data.GetThrown().getVelocity().length();
    data.GetThrown().remove();
    
    if (!(data.GetThrower() instanceof Player)) {
      return;
    }
    Player player = (Player)data.GetThrower();
    
    if (target == null) {
      return;
    }
    
    UtilAction.velocity(target, 
      UtilAlg.getTrajectory(target.getLocation(), player.getLocation()), 
      2.0D, false, 0.0D, 0.8D, 1.5D, true);
    

    this.Manager.GetCondition().Factory().Falling(GetName(), target, player, 10.0D, false, true);
    

    this.Manager.GetDamage().NewDamageEvent(target, player, null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, velocity * 8.0D, false, true, false, 
      player.getName(), GetName());
    

    UtilPlayer.message(target, F.main("Skill", F.name(player.getName()) + " hit you with " + F.skill(GetName()) + "."));
  }
  


  public void Idle(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
  


  public void Expire(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
}
