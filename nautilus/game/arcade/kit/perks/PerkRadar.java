package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PerkRadar extends Perk
{
  private HashMap<Player, Long> _lastTick = new HashMap();
  



  public PerkRadar()
  {
    super("Radar Scanner", new String[] {C.cYellow + "Hold Compass" + C.cGray + " to use " + C.cGreen + "Radar Scanner", "Ticks get faster when you are near a Hider!" });
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (UtilGear.isMat(player.getItemInHand(), Material.COMPASS))
      {

        if ((this.Kit.HasKit(player)) && (this.Manager.IsAlive(player)))
        {

          double closest = 999.0D;
          for (Player other : UtilServer.getPlayers())
          {
            if (!other.equals(player))
            {

              if (this.Manager.IsAlive(other))
              {

                if (this.Manager.GetColor(other) == ChatColor.AQUA)
                {

                  double dist = UtilMath.offset(other, player);
                  if (dist < closest)
                    closest = dist;
                } } }
          }
          double scale = Math.max(0.0D, Math.min(1.0D, (closest - 3.0D) / 10.0D));
          

          if ((this._lastTick.containsKey(player)) && (!mineplex.core.common.util.UtilTime.elapsed(((Long)this._lastTick.get(player)).longValue(), (2000.0D * scale))) && (!Recharge.Instance.usable(player, "Radar"))) {
            return;
          }
          this._lastTick.put(player, Long.valueOf(System.currentTimeMillis()));
          Recharge.Instance.useForce(player, "Radar", (2000.0D * scale));
          
          player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.NOTE_STICKS, 1.0F, (float)(2.0D - 1.0D * scale));
          
          player.setCompassTarget(player.getLocation().add(Math.random() * 10.0D - 5.0D, 0.0D, Math.random() * 10.0D - 5.0D));
        }
      }
    }
  }
}
