package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkSeismicSlamCS extends Perk
{
  public PerkSeismicSlamCS()
  {
    super("Ground Pound", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Ground Pound" });
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
    if (UtilBlock.usable(event.getClickedBlock())) {
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
    if (!Recharge.Instance.use(player, GetName(), 10000L, true, true)) {
      return;
    }
    
    double range = 6.0D;
    
    HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(player.getLocation(), range);
    for (LivingEntity cur : targets.keySet())
    {
      if (!(cur instanceof Player)) {
        return;
      }
      if (!cur.equals(player))
      {

        Player other = (Player)cur;
        
        if (this.Manager.GetGame().IsAlive(other))
        {

          if (!this.Manager.GetGame().GetTeam(other).equals(this.Manager.GetGame().GetTeam(player)))
          {


            this.Manager.GetDamage().NewDamageEvent(cur, player, null, 
              EntityDamageEvent.DamageCause.CUSTOM, 8.0D * ((Double)targets.get(cur)).doubleValue() + 1.0D, false, true, false, 
              player.getName(), GetName());
            

            UtilAction.velocity(cur, 
              UtilAlg.getTrajectory2d(player.getLocation().toVector(), cur.getLocation().toVector()), 
              1.8D * ((Double)targets.get(cur)).doubleValue(), true, 0.0D, 0.4D + 1.0D * ((Double)targets.get(cur)).doubleValue(), 1.6D, true);
            

            this.Manager.GetCondition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
            

            if ((cur instanceof Player))
              UtilPlayer.message((Player)cur, F.main("Game", F.name(player.getName()) + " hit you with " + F.skill(GetName()) + "."));
          } }
      }
    }
    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ZOMBIE_WOOD, 2.0F, 0.2F);
    for (Block cur : UtilBlock.getInRadius(player.getLocation(), 4.0D).keySet()) {
      if ((UtilBlock.airFoliage(cur.getRelative(BlockFace.UP))) && (!UtilBlock.airFoliage(cur))) {
        cur.getWorld().playEffect(cur.getLocation(), org.bukkit.Effect.STEP_SOUND, cur.getTypeId());
      }
    }
    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
}
