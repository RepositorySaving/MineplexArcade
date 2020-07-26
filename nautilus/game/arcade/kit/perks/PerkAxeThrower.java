package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkAxeThrower extends Perk implements IThrown
{
  public PerkAxeThrower(ArcadeManager manager)
  {
    super("Axe Thrower", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axes to " + C.cGreen + "Throw Axe" });
  }
  

  @org.bukkit.event.EventHandler
  public void Throw(PlayerInteractEvent event)
  {
    if (!UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R)) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(player.getItemInHand().getType()));
    mineplex.core.common.util.UtilAction.velocity(ent, player.getLocation().getDirection(), 1.2D, false, 0.0D, 0.2D, 10.0D, false);
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, false, 2.0D);
    

    player.setItemInHand(null);
    UtilInv.Update(player);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    if (((target instanceof Player)) && 
      (!this.Manager.GetGame().IsAlive((Player)target))) {
      return;
    }
    Item item = (Item)data.GetThrown();
    
    int damage = 4;
    if (item.getItemStack().getType() == Material.STONE_AXE) { damage = 5;
    } else if (item.getItemStack().getType() == Material.IRON_AXE) { damage = 6;
    } else if (item.getItemStack().getType() == Material.DIAMOND_AXE) { damage = 7;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.CUSTOM, damage, true, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
    

    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), org.bukkit.Sound.ZOMBIE_WOOD, 1.0F, 1.6F);
    

    data.GetThrown().getWorld().dropItem(data.GetThrown().getLocation(), ItemStackFactory.Instance.CreateStack(item.getItemStack().getType())).setPickupDelay(60);
    

    data.GetThrown().remove();
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
