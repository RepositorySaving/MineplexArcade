package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.recharge.Recharge;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkWitherSkull extends Perk
{
  private HashMap<WitherSkull, Vector> _active = new HashMap();
  private HashSet<Player> _ignoreControl = new HashSet();
  


  public PerkWitherSkull()
  {
    super("Wither Skull", new String[] {C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Wither Skull" });
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
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 6000L, true, true)) {
      return;
    }
    
    WitherSkull skull = (WitherSkull)player.launchProjectile(WitherSkull.class);
    skull.setDirection(player.getLocation().getDirection());
    
    this._active.put(skull, player.getLocation().getDirection().multiply(0.6D));
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.WITHER_SHOOT, 1.0F, 1.0F);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You launched " + F.skill(GetName()) + "."));
    

    this._ignoreControl.remove(player);
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<WitherSkull> skullIterator = this._active.keySet().iterator();
    
    while (skullIterator.hasNext())
    {
      WitherSkull skull = (WitherSkull)skullIterator.next();
      Player player = (Player)skull.getShooter();
      
      if (!skull.isValid())
      {
        skullIterator.remove();
        skull.remove();


      }
      else if ((player.isBlocking()) && (!this._ignoreControl.contains(player)))
      {
        skull.setDirection(player.getLocation().getDirection());
        skull.setVelocity(player.getLocation().getDirection().multiply(0.6D));
        this._active.put(skull, player.getLocation().getDirection().multiply(0.6D));
      }
      else
      {
        this._ignoreControl.add(player);
        skull.setDirection((Vector)this._active.get(skull));
        skull.setVelocity((Vector)this._active.get(skull));
      }
    }
  }
  
  @EventHandler
  public void Explode(EntityExplodeEvent event)
  {
    if (!this._active.containsKey(event.getEntity())) {
      return;
    }
    event.setCancelled(true);
    
    WitherSkull skull = (WitherSkull)event.getEntity();
    
    Explode(skull, event.getLocation(), skull.getShooter());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void ExplodeDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetProjectile() != null) && ((event.GetProjectile() instanceof WitherSkull))) {
      event.SetCancelled("Wither Skull Cancel");
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DirectHitDamage(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    if (event.GetDamageInitial() != 7.0D) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this.Manager.IsAlive(damager)) {
      return;
    }
    event.SetCancelled("Wither Skull Direct Hit");
  }
  
  private void Explode(WitherSkull skull, Location loc, LivingEntity shooter)
  {
    double scale = 0.4D + 0.6D * Math.min(1.0D, skull.getTicksLived() / 20.0D);
    

    for (Entity ent : skull.getWorld().getEntities())
    {
      if ((ent instanceof LivingEntity))
      {

        if (mineplex.core.common.util.UtilMath.offset(loc, ent.getLocation()) <= 2.0D)
        {

          if ((!(ent instanceof Player)) || 
            (this.Manager.GetGame().IsAlive((Player)ent)))
          {

            LivingEntity livingEnt = (LivingEntity)ent;
            

            this.Manager.GetDamage().NewDamageEvent(livingEnt, shooter, null, 
              EntityDamageEvent.DamageCause.CUSTOM, 12.0D * scale, false, true, false, 
              UtilEnt.getName(shooter), GetName());
            
            UtilAction.velocity(livingEnt, UtilAlg.getTrajectory2d(loc, livingEnt.getLocation()), 1.6D * scale, true, 0.8D * scale, 0.0D, 10.0D, true);
          } }
      }
    }
    loc.getWorld().createExplosion(loc, 2.5F);
  }
}
