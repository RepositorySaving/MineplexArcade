package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguisePigZombie;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.DamageManager;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkPigBaconBounce extends Perk implements IThrown
{
  public PerkPigBaconBounce()
  {
    super("Bouncy Bacon", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Bouncy Bacon" });
  }
  

  @EventHandler
  public void Skill(PlayerInteractEvent event)
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
    float energy = 0.2F;
    
    DisguiseBase disguise = this.Manager.GetDisguise().getDisguise(player);
    if ((disguise != null) && ((disguise instanceof DisguisePigZombie))) {
      energy = 0.1F;
    }
    
    if (player.getExp() < energy)
    {
      UtilPlayer.message(player, F.main("Energy", "Not enough Energy to use " + F.skill(GetName()) + "."));
      return;
    }
    

    if (!Recharge.Instance.use(player, GetName(), 100L, false, false)) {
      return;
    }
    
    player.setExp(Math.max(0.0F, player.getExp() - energy));
    

    Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.PORK));
    UtilAction.velocity(ent, player.getLocation().getDirection(), 1.2D, false, 0.0D, 0.2D, 10.0D, false);
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, false, 1.0D);
    ent.setPickupDelay(9999);
    

    player.getWorld().playSound(player.getLocation(), Sound.PIG_IDLE, 2.0F, 1.5F);
    

    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Rebound(data.GetThrower(), data.GetThrown());
    
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.CUSTOM, 3.5D, true, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
    
    Item item = (Item)data.GetThrown();
    item.setItemStack(new ItemStack(Material.GRILLED_PORK));
  }
  

  public void Idle(ProjectileUser data)
  {
    Rebound(data.GetThrower(), data.GetThrown());
  }
  

  public void Expire(ProjectileUser data)
  {
    Rebound(data.GetThrower(), data.GetThrown());
  }
  
  public void Rebound(LivingEntity player, Entity ent)
  {
    ent.getWorld().playSound(ent.getLocation(), Sound.ITEM_PICKUP, 1.0F, 0.5F);
    
    double mult = 0.5D + 0.035D * UtilMath.offset(player.getLocation(), ent.getLocation());
    

    ent.setVelocity(player.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize().add(new Vector(0.0D, 0.4D, 0.0D)).multiply(mult));
    

    if ((ent instanceof Item)) {
      ((Item)ent).setPickupDelay(5);
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.LOWEST)
  public void Pickup(PlayerPickupItemEvent event) {
    if (!this.Kit.HasKit(event.getPlayer())) {
      return;
    }
    if ((event.getItem().getItemStack().getType() != Material.PORK) && (event.getItem().getItemStack().getType() != Material.GRILLED_PORK)) {
      return;
    }
    
    event.getItem().remove();
    

    event.getPlayer().setExp(Math.min(0.999F, event.getPlayer().getExp() + 0.05F));
    

    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.EAT, 2.0F, 1.0F);
    

    if (event.getItem().getItemStack().getType() == Material.GRILLED_PORK)
    {
      UtilPlayer.health(event.getPlayer(), 1.0D);
      UtilParticle.PlayParticle(UtilParticle.ParticleType.HEART, event.getPlayer().getLocation().add(0.0D, 0.5D, 0.0D), 0.2F, 0.2F, 0.2F, 0.0F, 4);
    }
  }
}
