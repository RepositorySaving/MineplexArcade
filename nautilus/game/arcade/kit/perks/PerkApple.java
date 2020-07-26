package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.PlayerInventory;

public class PerkApple extends Perk implements mineplex.core.projectile.IThrown
{
  public PerkApple(ArcadeManager manager)
  {
    super("Apple Thrower", new String[] { C.cGray + "Receive 1 Apple every 10 seconds", C.cYellow + "Left-Click" + C.cGray + " with Apple to " + C.cGreen + "Throw Apple" });
  }
  

  @EventHandler
  public void AppleSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        if (this.Manager.GetGame().IsAlive(player))
        {

          if (Recharge.Instance.use(player, "Apple Spawn", 10000L, false, false))
          {

            player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(260) });
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 2.0F, 1.0F);
          } } }
    }
  }
  
  @EventHandler
  public void ThrowApple(PlayerInteractEvent event) {
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.APPLE) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    UtilInv.remove(player, Material.APPLE, (byte)0, 1);
    UtilInv.Update(player);
    
    org.bukkit.entity.Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.APPLE));
    UtilAction.velocity(ent, player.getLocation().getDirection(), 1.2D, false, 0.0D, 0.2D, 10.0D, false);
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, false, 1.0D);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    if ((target instanceof Player))
    {
      if (!this.Manager.GetGame().IsAlive((Player)target))
      {
        return;
      }
    }
    

    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.CUSTOM, 3.0D, true, false, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
    

    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.6F);
    

    if ((data.GetThrown() instanceof net.minecraft.server.v1_7_R3.Item)) {
      data.GetThrown().getWorld().dropItem(data.GetThrown().getLocation(), ItemStackFactory.Instance.CreateStack(Material.APPLE)).setPickupDelay(60);
    }
    data.GetThrown().remove();
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
