package mineplex.minecraft.game.classcombat.item.Throwable;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ProximityExplosive extends ItemUsable
{
  private HashMap<Entity, LivingEntity> _armed = new HashMap();
  
















  public ProximityExplosive(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Proximity Explosive", new String[] {"Thrown Item:", "Activates after 4 seconds.", "Detonates on player proximity;", "* 8 Range", "* 1 Damage", "* Strong Knockback", "All effects scale down with range." }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
  }
  




  public void UseAction(PlayerInteractEvent event) {}
  




  public void Collide(LivingEntity target, Block block, ProjectileUser data) {}
  



  public void Idle(ProjectileUser data) {}
  



  public void Expire(ProjectileUser data)
  {
    this._armed.put(data.GetThrown(), data.GetThrower());
    

    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 7);
    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.NOTE_PLING, 0.5F, 2.0F);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Pickup(PlayerPickupItemEvent event)
  {
    if (((CraftPlayer)event.getPlayer()).getHandle().spectating) {
      return;
    }
    if (this._armed.containsKey(event.getItem()))
    {
      event.setCancelled(true);
      Detonate(event.getItem());
    }
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (this._armed.containsValue(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  public void Detonate(Entity ent)
  {
    ent.remove();
    LivingEntity thrower = (LivingEntity)this._armed.remove(ent);
    

    HashMap<Player, Double> hit = UtilPlayer.getInRadius(ent.getLocation(), 8.0D);
    for (Player player : hit.keySet())
    {

      UtilAction.velocity(player, UtilAlg.getTrajectory(ent.getLocation(), 
        player.getEyeLocation()), 2.4D * ((Double)hit.get(player)).doubleValue(), false, 0.0D, 0.6D * ((Double)hit.get(player)).doubleValue(), 1.6D, true);
      

      this.Factory.Damage().NewDamageEvent(player, thrower, null, 
        EntityDamageEvent.DamageCause.CUSTOM, 10.0D * ((Double)hit.get(player)).doubleValue(), false, true, false, 
        UtilEnt.getName(thrower), GetName());
      

      UtilPlayer.message(player, F.main(GetName(), F.name(UtilEnt.getName(thrower)) + " hit you with " + F.item(GetName()) + "."));
    }
    

    ent.getWorld().playSound(ent.getLocation(), Sound.NOTE_PLING, 0.5F, 0.5F);
    ent.getWorld().playSound(ent.getLocation(), Sound.EXPLODE, 4.0F, 0.8F);
    UtilParticle.PlayParticle(UtilParticle.ParticleType.HUGE_EXPLOSION, ent.getLocation(), 0.0F, 0.5F, 0.0F, 0.0F, 1);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Entity> expired = new HashSet();
    
    for (Entity ent : this._armed.keySet())
    {
      if ((ent.isDead()) || (!ent.isValid()) || (ent.getTicksLived() >= 3600)) {
        expired.add(ent);
      }
    }
    for (Entity ent : expired)
    {
      Detonate(ent);
    }
  }
}
