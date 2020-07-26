package mineplex.minecraft.game.classcombat.item.Throwable;

import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.fire.Fire;
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
import org.bukkit.event.player.PlayerInteractEvent;











public class WaterBottle
  extends ItemUsable
{
  public WaterBottle(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Water Bottle", new String[] {"Thrown, giving AoE effect;", "* 3 Range", "* Douses Players", "* Extinguishes Fires", "Used, giving personal effect;", "* Douses Player", "* Fire Resistance for 4 Seconds" }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
    
    setFree(true);
  }
  

  public void UseAction(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (((CraftPlayer)player).getHandle().spectating) {
      return;
    }
    
    player.setFireTicks(-20);
    

    this.Factory.Condition().Factory().FireResist(GetName(), player, player, 4.0D, 0, false, true, true);
    

    player.getWorld().playSound(player.getLocation(), Sound.SPLASH, 1.0F, 1.4F);
    player.getWorld().playEffect(player.getEyeLocation(), Effect.STEP_SOUND, 8);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Break(data);
  }
  

  public void Idle(ProjectileUser data)
  {
    Break(data);
  }
  

  public void Expire(ProjectileUser data)
  {
    Break(data);
  }
  

  public void Break(ProjectileUser data)
  {
    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);
    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 8);
    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.SPLASH, 1.0F, 1.4F);
    

    this.Factory.Fire().RemoveNear(data.GetThrown().getLocation(), 3.0D);
    

    data.GetThrown().remove();
    
    for (Player player : UtilPlayer.getNearby(data.GetThrown().getLocation(), 3.0D))
    {

      player.setFireTicks(-20);
    }
  }
}
