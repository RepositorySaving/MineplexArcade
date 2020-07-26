package mineplex.minecraft.game.classcombat.item.Throwable;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import net.minecraft.server.v1_7_R3.EntityPlayer;
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
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ProximityZapper extends ItemUsable
{
  private HashMap<Entity, LivingEntity> _armed = new HashMap();
  
















  public ProximityZapper(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Proximity Zapper", new String[] {"Thrown Item:", "Activates after 4 seconds.", "Detonates on player proximity;", "* Lightning strikes the Zapper", "* Silence for 6 seconds", "* Shock for 6 seconds", "* Slow IV for 6 seconds" }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
  }
  




  public void UseAction(PlayerInteractEvent event) {}
  




  public void Collide(LivingEntity target, Block block, ProjectileUser data) {}
  



  public void Idle(ProjectileUser data) {}
  



  public void Expire(ProjectileUser data)
  {
    this._armed.put(data.GetThrown(), data.GetThrower());
    

    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), org.bukkit.Effect.STEP_SOUND, 7);
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
      Detonate(event.getItem(), event.getPlayer());
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
  
  public void Detonate(Entity ent, Player player)
  {
    ent.remove();
    LivingEntity thrower = (LivingEntity)this._armed.remove(ent);
    

    if (player != null)
    {

      UtilPlayer.message(player, F.main(GetName(), F.name(UtilEnt.getName(thrower)) + " hit you with " + F.item(GetName()) + "."));
      
      this.Factory.Condition().Factory().Silence(GetName(), player, thrower, 6.0D, false, true);
      this.Factory.Condition().Factory().Shock(GetName(), player, thrower, 6.0D, false, false);
      this.Factory.Condition().Factory().Slow(GetName(), player, thrower, 6.0D, 3, false, true, true, true);
    }
    

    ent.getWorld().strikeLightning(ent.getLocation());
    

    ent.getWorld().playSound(ent.getLocation(), Sound.NOTE_PLING, 0.5F, 0.5F);
    mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, ent.getLocation(), 0.0F, 0.5F, 0.0F, 0.0F, 1);
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
      Detonate(ent, null);
    }
  }
}
