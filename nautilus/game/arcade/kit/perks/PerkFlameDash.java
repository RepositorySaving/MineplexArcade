package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.FireflyData;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkFlameDash extends Perk
{
  private HashSet<FireflyData> _data = new HashSet();
  


  public PerkFlameDash()
  {
    super("Flame Dash", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Spade to use " + C.cGreen + "Flame Dash" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SPADE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.usable(player, GetName()))
    {
      boolean done = false;
      for (FireflyData data : this._data)
      {
        if (data.Player.equals(player))
        {
          data.Time = 0L;
          done = true;
        }
      }
      
      if (done)
      {
        UtilPlayer.message(player, F.main("Skill", "You ended " + F.skill(GetName()) + "."));
        UpdateMovement();
      }
      else
      {
        Recharge.Instance.use(player, GetName(), 8000L, true, true);
      }
      
      return;
    }
    
    Recharge.Instance.useForce(player, GetName(), 8000L);
    
    this._data.add(new FireflyData(player));
    
    this.Manager.GetCondition().Factory().Invisible(GetName(), player, player, 2.5D, 0, false, false, true);
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    UpdateMovement();
  }
  
  public void UpdateMovement()
  {
    Iterator<FireflyData> dataIterator = this._data.iterator();
    
    while (dataIterator.hasNext())
    {
      FireflyData data = (FireflyData)dataIterator.next();
      


      if (!mineplex.core.common.util.UtilTime.elapsed(data.Time, 800L))
      {
        Vector vel = data.Location.getDirection();
        vel.setY(0);
        vel.normalize();
        vel.setY(0.05D);
        
        data.Player.setVelocity(vel);
        

        data.Player.getWorld().playSound(data.Player.getLocation(), Sound.FIZZ, 0.6F, 1.2F);
        

        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, data.Player.getLocation().add(0.0D, 0.4D, 0.0D), 0.2F, 0.2F, 0.2F, 0.0F, 3);

      }
      else
      {
        for (Player other : UtilPlayer.getNearby(data.Player.getLocation(), 3.0D))
        {
          if (!other.equals(data.Player))
          {

            if (this.Manager.GetGame().IsAlive(other))
            {

              double dist = UtilMath.offset(data.Player.getLocation(), data.Location) / 2.0D;
              

              this.Manager.GetDamage().NewDamageEvent(other, data.Player, null, 
                org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 2.0D + dist, true, true, false, 
                data.Player.getName(), GetName());
              
              UtilPlayer.message(other, F.main("Game", F.elem(new StringBuilder().append(this.Manager.GetColor(data.Player)).append(data.Player.getName()).toString()) + " hit you with " + F.elem(GetName()) + "."));
            }
          }
        }
        this.Manager.GetCondition().EndCondition(data.Player, null, GetName());
        

        data.Player.getWorld().playSound(data.Player.getLocation(), Sound.EXPLODE, 1.0F, 1.2F);
        

        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, data.Player.getLocation(), 0.1F, 0.1F, 0.1F, 0.3F, 100);
        UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, data.Player.getLocation().add(0.0D, 0.4D, 0.0D), 0.2F, 0.2F, 0.2F, 0.0F, 1);
        
        dataIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.0D);
  }
}
