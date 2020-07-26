package mineplex.minecraft.game.classcombat.item.Consume;

import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;




public class Apple
  extends ItemUsable
{
  public Apple(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Apple", new String[] { "Consume:", "Heals two hunger points.", " ", "Thrown:", "Does half a heart of damage on hit." }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
  }
  

  public void UseAction(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    

    UtilPlayer.hunger(player, 4);
    

    this.Factory.Energy().ModifyEnergy(player, 10.0D);
    

    player.getWorld().playSound(player.getLocation(), Sound.EAT, 1.0F, 1.0F);
    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 40);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    
    this.Factory.Damage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.CUSTOM, 2.0D, true, true, false, 
      UtilEnt.getName(data.GetThrower()), GetName());
    

    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.6F);
    

    if ((data.GetThrown() instanceof Item)) {
      data.GetThrown().getWorld().dropItem(data.GetThrown().getLocation(), ItemStackFactory.Instance.CreateStack(((Item)data.GetThrown()).getItemStack().getType())).setPickupDelay(60);
    }
    data.GetThrown().remove();
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
