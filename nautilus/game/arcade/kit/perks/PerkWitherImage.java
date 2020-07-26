package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.blood.Blood;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkWitherImage extends Perk
{
  private HashMap<Player, Skeleton> _images = new HashMap();
  


  public PerkWitherImage()
  {
    super("Wither Image", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Wither Image" });
  }
  

  @EventHandler
  public void Leap(PlayerInteractEvent event)
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
    if (!this._images.containsKey(player))
    {
      if (!Recharge.Instance.use(player, GetName(), 12000L, true, true)) {
        return;
      }
      
      this.Manager.GetGame().CreatureAllowOverride = true;
      Skeleton skel = (Skeleton)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Skeleton.class);
      this.Manager.GetGame().CreatureAllowOverride = false;
      
      skel.setSkeletonType(Skeleton.SkeletonType.WITHER);
      
      skel.getEquipment().setItemInHand(player.getItemInHand());
      skel.setMaxHealth(20.0D);
      skel.setHealth(skel.getMaxHealth());
      
      skel.setCustomName(C.cYellow + player.getName());
      skel.setCustomNameVisible(true);
      


      mineplex.core.common.util.UtilAction.velocity(skel, player.getLocation().getDirection(), 1.6D, false, 0.0D, 0.2D, 10.0D, true);
      
      this._images.put(player, skel);
      
      Recharge.Instance.use(player, "Wither Swap", 500L, false, false);
      

      player.getWorld().playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
      

      UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    }
    else
    {
      if (!Recharge.Instance.use(player, "Wither Swap", 1000L, true, false)) {
        return;
      }
      Skeleton skel = (Skeleton)this._images.get(player);
      
      Location loc = skel.getLocation();
      skel.teleport(player.getLocation());
      player.teleport(loc);
      

      player.getWorld().playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0F, 2.0F);
      

      UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Wither Swap") + "."));
    }
  }
  
  @EventHandler
  public void EntityTarget(EntityTargetEvent event)
  {
    if ((this._images.containsKey(event.getTarget())) && 
      (((Skeleton)this._images.get(event.getTarget())).equals(event.getEntity()))) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event) {
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this._images.containsKey(damagee)) {
      return;
    }
    LivingEntity damager = event.GetDamagerEntity(false);
    if (damager == null) { return;
    }
    if (((Skeleton)this._images.get(damagee)).equals(damager)) {
      event.SetCancelled("Wither Image");
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    Iterator<Player> playerIterator = this._images.keySet().iterator();
    
    while (playerIterator.hasNext())
    {
      Player player = (Player)playerIterator.next();
      Skeleton skel = (Skeleton)this._images.get(player);
      
      if ((!player.isValid()) || (!skel.isValid()) || (skel.getTicksLived() > 160))
      {

        this.Manager.GetBlood().Effects(skel.getLocation().add(0.0D, 0.5D, 0.0D), 12, 0.3D, Sound.WITHER_HURT, 1.0F, 0.75F, Material.BONE, (byte)0, 40, false);
        
        playerIterator.remove();
        skel.remove();
      }
    }
  }
  

  @EventHandler(priority=org.bukkit.event.EventPriority.LOWEST)
  public void Clean(PlayerDeathEvent event)
  {
    Skeleton skel = (Skeleton)this._images.remove(event.getEntity());
    
    if (skel != null)
    {

      this.Manager.GetBlood().Effects(skel.getLocation().add(0.0D, 0.5D, 0.0D), 12, 0.3D, Sound.WITHER_HURT, 1.0F, 0.75F, Material.BONE, (byte)0, 40, false);
      
      skel.remove();
    }
  }
}
