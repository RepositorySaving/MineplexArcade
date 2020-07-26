package mineplex.minecraft.game.classcombat.item.Throwable;

import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;






public class Web
  extends ItemUsable
{
  public Web(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Web", new String[] { "Thrown:", "Used to trap enemies." }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
    
    setFree(true);
  }
  



  public void UseAction(PlayerInteractEvent event) {}
  


  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target != null)
    {
      double distance = UtilMath.offset(target.getLocation(), data.GetThrown().getLocation());
      
      if (distance > 0.75D)
      {
        data.GetThrown().teleport(data.GetThrown().getLocation().add(new Vector(0.0D, -distance / 2.0D, 0.0D)));
      }
    }
    
    CreateWeb(data.GetThrown());
  }
  

  public void Idle(ProjectileUser data)
  {
    CreateWeb(data.GetThrown());
  }
  

  public void Expire(ProjectileUser data)
  {
    CreateWeb(data.GetThrown());
  }
  

  public void CreateWeb(Entity ent)
  {
    ent.getWorld().playEffect(ent.getLocation(), Effect.STEP_SOUND, 30);
    
    if (!UtilBlock.airFoliage(ent.getLocation().getBlock())) {
      return;
    }
    this.Factory.BlockRestore().Add(ent.getLocation().getBlock(), 30, (byte)0, 6000L);
    
    ent.remove();
  }
}
