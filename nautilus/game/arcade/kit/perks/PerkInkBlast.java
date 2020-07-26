package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkInkBlast extends Perk implements mineplex.core.projectile.IThrown
{
  public PerkInkBlast()
  {
    super("Ink Shotgun", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Ink Shotgun" });
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
    if (!Recharge.Instance.use(player, GetName(), 6000L, true, true)) {
      return;
    }
    event.setCancelled(true);
    
    mineplex.core.common.util.UtilInv.Update(player);
    
    for (int i = 0; i < 5; i++)
    {
      Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.INK_SACK));
      
      Vector random = new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D);
      random.normalize();
      random.multiply(0.2D);
      
      UtilAction.velocity(ent, player.getLocation().getDirection().add(random), 0.8D + 0.4D * Math.random(), false, 0.0D, 0.2D, 10.0D, false);
      
      this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, 
        null, 1.0F, 1.0F, 
        org.bukkit.Effect.SMOKE, 4, mineplex.core.updater.UpdateType.TICK, 
        2.0D);
    }
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1.5F, 0.75F);
    player.getWorld().playSound(player.getLocation(), Sound.SPLASH, 0.75F, 1.0F);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Explode(data);
    
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE, 2.5D, true, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
    
    this.Manager.GetCondition().Factory().Blind(GetName(), target, data.GetThrower(), 2.5D, 0, false, false, false);
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
    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.EXPLODE, 0.75F, 1.25F);
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
