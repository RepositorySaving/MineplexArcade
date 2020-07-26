package mineplex.core.antihack.types;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;
import mineplex.core.MiniPlugin;
import mineplex.core.antihack.AntiHack;
import mineplex.core.antihack.Detector;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed
  extends MiniPlugin
  implements Detector
{
  private AntiHack Host;
  private HashMap<Player, Map.Entry<Integer, Long>> _speedTicks = new HashMap();
  
  public Speed(AntiHack host)
  {
    super("Speed Detector", host.GetPlugin());
    this.Host = host;
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void updateSpeedhack(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    

    if (this.Host.isValid(player, false)) {
      return;
    }
    UpdateSpeed(player, event);
  }
  
  private void UpdateSpeed(Player player, PlayerMoveEvent event)
  {
    int count = 0;
    
    if (this._speedTicks.containsKey(player)) {
      double offset;
      double offset;
      if (event.getFrom().getY() > event.getTo().getY())
      {
        offset = UtilMath.offset2d(event.getFrom(), event.getTo());
      }
      else
      {
        offset = UtilMath.offset(event.getFrom(), event.getTo());
      }
      

      double limit = 0.74D;
      if (UtilEnt.isGrounded(player)) {
        limit = 0.32D;
      }
      for (PotionEffect effect : player.getActivePotionEffects())
      {
        if (effect.getType().equals(PotionEffectType.SPEED))
        {
          if (UtilEnt.isGrounded(player)) {
            limit += 0.08D * (effect.getAmplifier() + 1);
          } else {
            limit += 0.04D * (effect.getAmplifier() + 1);
          }
        }
      }
      
      if ((offset > limit) && (!UtilTime.elapsed(((Long)((Map.Entry)this._speedTicks.get(player)).getValue()).longValue(), 150L)))
      {
        count = ((Integer)((Map.Entry)this._speedTicks.get(player)).getKey()).intValue() + 1;
      }
      else
      {
        count = 0;
      }
    }
    
    if (count > this.Host.SpeedHackTicks)
    {
      this.Host.addSuspicion(player, "Speed (Fly/Move)");
      count = 0;
    }
    
    this._speedTicks.put(player, new AbstractMap.SimpleEntry(Integer.valueOf(count), Long.valueOf(System.currentTimeMillis())));
  }
  

  public void Reset(Player player)
  {
    this._speedTicks.remove(player);
  }
}
