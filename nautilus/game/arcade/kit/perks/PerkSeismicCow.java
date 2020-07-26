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
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkSeismicCow extends Perk
{
  private HashMap<LivingEntity, Long> _live = new HashMap();
  


  public PerkSeismicCow()
  {
    super("Seismic Slam", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Shovel to " + C.cGreen + "Seismic Slam" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SPADE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 6000L, true, true)) {
      return;
    }
    
    Vector vec = player.getLocation().getDirection();
    if (vec.getY() < 0.0D) {
      vec.setY(vec.getY() * -1.0D);
    }
    UtilAction.velocity(player, vec, 1.0D, true, 1.0D, 0.0D, 1.0D, true);
    

    this._live.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Slam(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (UtilEnt.isGrounded(player))
      {

        if (this._live.containsKey(player))
        {

          if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._live.get(player)).longValue(), 1000L))
          {

            this._live.remove(player);
            

            int damage = 4;
            double range = 6.0D;
            
            HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(player.getLocation(), range);
            for (LivingEntity cur : targets.keySet())
            {
              if (!cur.equals(player))
              {

                if (!(cur instanceof Cow))
                {


                  this.Manager.GetDamage().NewDamageEvent(cur, player, null, 
                    EntityDamageEvent.DamageCause.CUSTOM, damage * ((Double)targets.get(cur)).doubleValue() + 0.5D, false, true, false, 
                    player.getName(), GetName());
                  

                  UtilAction.velocity(cur, 
                    UtilAlg.getTrajectory2d(player.getLocation().toVector(), cur.getLocation().toVector()), 
                    1.8D * ((Double)targets.get(cur)).doubleValue(), true, 0.0D, 0.4D + 1.0D * ((Double)targets.get(cur)).doubleValue(), 1.6D, true);
                  

                  this.Manager.GetCondition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
                  

                  if ((cur instanceof Player))
                    UtilPlayer.message((Player)cur, F.main("Game", F.name(player.getName()) + " hit you with " + F.skill(GetName()) + "."));
                }
              }
            }
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ZOMBIE_WOOD, 2.0F, 0.2F);
            for (Block cur : UtilBlock.getInRadius(player.getLocation(), 4.0D).keySet()) {
              if ((UtilBlock.airFoliage(cur.getRelative(org.bukkit.block.BlockFace.UP))) && (!UtilBlock.airFoliage(cur))) {
                cur.getWorld().playEffect(cur.getLocation(), org.bukkit.Effect.STEP_SOUND, cur.getTypeId());
              }
            }
          }
        }
      }
    }
  }
}
