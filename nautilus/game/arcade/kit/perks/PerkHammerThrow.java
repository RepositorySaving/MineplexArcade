package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
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
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class PerkHammerThrow extends Perk implements mineplex.core.projectile.IThrown
{
  private HashMap<Item, Player> _thrown = new HashMap();
  


  public PerkHammerThrow()
  {
    super("Hammer Throw", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Diamond Axe to " + C.cGreen + "Hammer Throw" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("DIAMOND_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    player.setItemInHand(null);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.DIAMOND_AXE));
    mineplex.core.common.util.UtilAction.velocity(item, player.getLocation().getDirection(), 1.2D, false, 0.0D, 0.2D, 10.0D, true);
    

    this.Manager.GetProjectile().AddThrow(item, player, this, -1L, true, true, true, false, 2.5D);
    

    this._thrown.put(item, player);
  }
  
  @EventHandler
  public void Pickup(PlayerPickupItemEvent event)
  {
    if (!this._thrown.containsKey(event.getItem())) {
      return;
    }
    event.setCancelled(true);
    event.getItem().remove();
    
    Player player = (Player)this._thrown.remove(event.getItem());
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.DIAMOND_AXE, 0, 1, "Thor Hammer") });
  }
  
  @EventHandler
  public void Timeout(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    Iterator<Item> itemIterator = this._thrown.keySet().iterator();
    
    while (itemIterator.hasNext())
    {
      Item item = (Item)itemIterator.next();
      
      if (item.getTicksLived() > 200)
      {
        ((Player)this._thrown.get(item)).getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.DIAMOND_AXE, 0, 1, "Thor Hammer") });
        item.remove();
        itemIterator.remove();
      }
    }
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Rebound(data.GetThrower(), data.GetThrown());
    
    if (target == null) {
      return;
    }
    double damage = 16.0D;
    if ((target instanceof Giant)) {
      damage = 8.0D;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.LIGHTNING, damage, true, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
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
    ent.getWorld().playSound(ent.getLocation(), org.bukkit.Sound.ZOMBIE_METAL, 0.6F, 0.5F);
    
    double mult = 0.5D + 0.6D * (mineplex.core.common.util.UtilMath.offset(player.getLocation(), ent.getLocation()) / 16.0D);
    

    ent.setVelocity(player.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize().add(new Vector(0.0D, 0.4D, 0.0D)).multiply(mult));
    

    if ((ent instanceof Item)) {
      ((Item)ent).setPickupDelay(5);
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event) {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.0D);
  }
}
