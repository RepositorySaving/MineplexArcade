package mineplex.minecraft.game.classcombat.item.Throwable;

import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;





public class PoisonBall
  extends ItemUsable
{
  public PoisonBall(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Poison Ball", new String[] { "Thrown:", "Poisons for 6 seconds on hit.", "Bounces back to you.", "Can be intercepted by enemy on return." }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
  }
  



  public void UseAction(PlayerInteractEvent event) {}
  


  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Rebound(data.GetThrower(), data.GetThrown());
    
    if (target == null) {
      return;
    }
    this.Factory.Condition().Factory().Poison(GetName(), target, data.GetThrower(), 6.0D, 0, false, true, true);
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
    
    double mult = 0.5D + 0.6D * (UtilMath.offset(player.getLocation(), ent.getLocation()) / 16.0D);
    

    ent.setVelocity(player.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize().add(new Vector(0.0D, 0.4D, 0.0D)).multiply(mult));
    

    if ((ent instanceof Item)) {
      ((Item)ent).setPickupDelay(5);
    }
  }
}
