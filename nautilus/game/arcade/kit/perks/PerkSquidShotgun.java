package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.recharge.RechargedEvent;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.world.FireworkHandler;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkSquidShotgun extends Perk implements mineplex.core.projectile.IThrown
{
  private HashMap<Firework, Vector> _fireworks = new HashMap();
  


  public PerkSquidShotgun()
  {
    super("Ink Shotgun", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Ink Shotgun" });
  }
  

  @EventHandler
  public void Recharge(RechargedEvent event)
  {
    if (!event.GetAbility().equals(GetName())) {
      return;
    }
    event.GetPlayer().playSound(event.GetPlayer().getLocation(), Sound.NOTE_STICKS, 3.0F, 1.0F);
  }
  
  @EventHandler
  public void Shoot(PlayerInteractEvent event)
  {
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
    if (!Recharge.Instance.use(player, GetName(), 2500L, true, true)) {
      return;
    }
    event.setCancelled(true);
    
    mineplex.core.common.util.UtilInv.Update(player);
    

    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.GREEN).with(org.bukkit.FireworkEffect.Type.BURST).trail(false).build();
    
    for (int i = 0; i < 6; i++)
    {

      Vector random = new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D);
      random.normalize();
      random.multiply(0.3D);
      
      try
      {
        Vector vel = player.getLocation().getDirection().multiply(1.4D).add(random);
        Firework fw = this.Manager.GetFirework().launchFirework(player.getEyeLocation().subtract(0.0D, 0.5D, 0.0D).add(player.getLocation().getDirection()), effect, vel);
        this._fireworks.put(fw, vel);
        

        this.Manager.GetProjectile().AddThrow(fw, player, this, -1L, true, true, true, 3.0D, this.Manager.GetDisguise());
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    


    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 0.75F);
  }
  


  @EventHandler
  public void FireworkUpdate(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    Iterator<Firework> fwIterator = this._fireworks.keySet().iterator();
    
    while (fwIterator.hasNext())
    {
      Firework fw = (Firework)fwIterator.next();
      
      if (!fw.isValid())
      {
        fwIterator.remove();
      }
      else
      {
        fw.setVelocity((Vector)this._fireworks.get(fw));
      }
    }
  }
  
  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Explode(data);
    
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.PROJECTILE, 12.0D, false, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
    

    if ((data.GetThrower() instanceof Player))
    {
      Player player = (Player)data.GetThrower();
      Recharge.Instance.recharge(player, GetName());
      player.playSound(player.getLocation(), Sound.NOTE_STICKS, 3.0F, 1.0F);
    }
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
    if (!(data.GetThrown() instanceof Firework))
    {
      data.GetThrown().remove();
      return;
    }
    
    Firework fw = (Firework)data.GetThrown();
    
    try
    {
      this.Manager.GetFirework().detonateFirework(fw);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
