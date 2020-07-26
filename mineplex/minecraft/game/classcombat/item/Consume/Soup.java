package mineplex.minecraft.game.classcombat.item.Consume;

import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;






public class Soup
  extends ItemUsable
{
  public Soup(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Mushroom Soup", new String[] { "Consume:", "Heals two hunger points.", "Gives Regen II boost for 4 seconds" }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
    
    setFree(true);
  }
  

  public void UseAction(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    

    UtilPlayer.hunger(player, 4);
    

    this.Factory.Condition().Factory().Custom(GetName(), player, player, 
      Condition.ConditionType.REGENERATION, 4.0D, 1, false, 
      Material.MUSHROOM_SOUP, (byte)0, true);
    

    player.getWorld().playSound(player.getLocation(), Sound.EAT, 1.0F, 1.0F);
    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 39);
    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 40);
  }
  
  public void Collide(LivingEntity target, Block block, ProjectileUser data) {}
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
