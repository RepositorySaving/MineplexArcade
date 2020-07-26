package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class PerkCharge extends Perk
{
  public PerkCharge()
  {
    super("Cow Charge", new String[] {C.cYellow + "Sprint" + C.cGray + " to use " + C.cGreen + "Cow Charge" });
  }
  

  @EventHandler
  public void Damage(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (player.isSprinting())
      {

        if (this.Kit.HasKit(player))
        {

          player.getWorld().playSound(player.getLocation(), Sound.COW_WALK, 0.8F, 1.0F);
          
          this.Manager.GetCondition().Factory().Speed(GetName(), player, player, 0.9D, 2, false, false, false);
          
          HashMap<LivingEntity, Double> targets = mineplex.core.common.util.UtilEnt.getInRadius(player.getLocation().add(player.getLocation().getDirection().setY(0).normalize()), 2.0D);
          for (LivingEntity cur : targets.keySet())
          {
            if (!cur.equals(player))
            {

              if (!(cur instanceof org.bukkit.entity.Cow))
              {

                Vector dir = mineplex.core.common.util.UtilAlg.getTrajectory2d(player.getLocation().toVector(), cur.getLocation().toVector());
                dir.add(player.getLocation().getDirection().setY(0).normalize());
                dir.setY(0);
                dir.normalize();
                

                this.Manager.GetDamage().NewDamageEvent(cur, player, null, 
                  EntityDamageEvent.DamageCause.CUSTOM, 3.0D, false, false, false, 
                  player.getName(), GetName());
                

                mineplex.core.common.util.UtilAction.velocity(cur, dir, 1.0D + 0.8D * ((Double)targets.get(cur)).doubleValue(), true, 0.0D, 0.8D + 0.4D * ((Double)targets.get(cur)).doubleValue(), 1.6D, true);
                

                this.Manager.GetCondition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
                

                player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOOD, 0.75F, 1.0F);
              } } }
        } }
    }
  }
  
  @EventHandler
  public void Charge(UpdateEvent event) {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        if (player.isSprinting())
        {
          UtilPlayer.hunger(player, -1);
          
          if (player.getFoodLevel() <= 0)
          {
            player.setSprinting(false);
          }
        }
        else
        {
          UtilPlayer.hunger(player, 1);
        }
      }
    }
  }
}
