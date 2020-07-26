package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.FireflyData;
import nautilus.game.arcade.world.FireworkHandler;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkFirefly extends Perk
{
  private HashSet<FireflyData> _data = new HashSet();
  private int _tick = 0;
  


  public PerkFirefly()
  {
    super("Firefly", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Firefly" });
  }
  

  @EventHandler
  public void Skill(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
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
    this._data.add(new FireflyData(player));
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    this._tick = ((this._tick + 1) % 3);
    
    Iterator<FireflyData> dataIterator = this._data.iterator();
    
    while (dataIterator.hasNext())
    {
      FireflyData data = (FireflyData)dataIterator.next();
      

      if (!UtilTime.elapsed(data.Time, 1500L))
      {
        data.Player.setVelocity(new Vector(0, 0, 0));
        data.Player.getWorld().playSound(data.Player.getLocation(), Sound.EXPLODE, 0.2F, 0.6F);
        data.Location = data.Player.getLocation();
        
        if (this._tick == 0)
        {

          FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.ORANGE).with(FireworkEffect.Type.BURST).trail(false).build();
          
          try
          {
            this.Manager.GetFirework().playFirework(data.Player.getLocation(), effect);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
          
        }
        
      }
      else if (!UtilTime.elapsed(data.Time, 2500L))
      {
        data.Player.setVelocity(data.Player.getLocation().getDirection().multiply(0.7D).add(new Vector(0.0D, 0.1D, 0.0D)));
        
        data.Player.getWorld().playSound(data.Player.getLocation(), Sound.EXPLODE, 0.6F, 1.2F);
        
        if (this._tick == 0)
        {

          FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.RED).with(FireworkEffect.Type.BURST).trail(false).build();
          
          try
          {
            this.Manager.GetFirework().playFirework(data.Player.getLocation(), effect);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
        

        for (Player other : UtilPlayer.getNearby(data.Player.getLocation(), 3.0D))
        {
          if (!other.equals(data.Player))
          {

            if (this.Manager.GetGame().IsAlive(other))
            {

              other.playEffect(org.bukkit.EntityEffect.HURT);
              
              if ((this._tick == 0) && 
                (!data.Targets.contains(other)))
              {
                data.Targets.add(other);
                

                this.Manager.GetDamage().NewDamageEvent(other, data.Player, null, 
                  EntityDamageEvent.DamageCause.CUSTOM, 10.0D, true, true, false, 
                  data.Player.getName(), GetName());
                
                UtilPlayer.message(other, F.main("Game", F.elem(new StringBuilder().append(this.Manager.GetColor(data.Player)).append(data.Player.getName()).toString()) + " hit you with " + F.elem(GetName()) + "."));
              }
            }
          }
        }
      } else {
        dataIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void FireflyDamage(CustomDamageEvent event)
  {
    Iterator<FireflyData> dataIterator = this._data.iterator();
    
    while (dataIterator.hasNext())
    {
      FireflyData data = (FireflyData)dataIterator.next();
      
      if (data.Player.equals(event.GetDamageeEntity()))
      {

        if (!UtilTime.elapsed(data.Time, 1250L))
        {
          dataIterator.remove();
        }
        else
        {
          event.SetCancelled("Firefly Immunity");
        }
      }
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event) {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.0D);
  }
}
