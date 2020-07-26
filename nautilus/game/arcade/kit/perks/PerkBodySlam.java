package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkBodySlam extends Perk
{
  private HashMap<LivingEntity, Long> _live = new HashMap();
  
  private int _damage;
  
  private double _knockback;
  

  public PerkBodySlam(int damage, double knockback)
  {
    super("Body Slam", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Body Slam" });
    

    this._damage = damage;
    this._knockback = knockback;
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
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    UtilAction.velocity(player, player.getLocation().getDirection(), 1.2D, false, 0.0D, 0.2D, 0.8D, true);
    

    this._live.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void End(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    
    for (Player player : this.Manager.GetGame().GetPlayers(true)) {
      if (this._live.containsKey(player)) {
        for (Player other : this.Manager.GetGame().GetPlayers(true))
          if ((other.getGameMode() == GameMode.SURVIVAL) && 
            (!other.equals(player)) && 
            (UtilMath.offset(player, other) < 2.0D))
          {
            DoSlam(player, other);
            this._live.remove(player);
            return;
          }
      }
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (UtilEnt.isGrounded(player))
      {

        if (this._live.containsKey(player))
        {

          if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._live.get(player)).longValue(), 1000L))
          {

            this._live.remove(player); }
        }
      }
    }
  }
  
  public void DoSlam(Player damager, LivingEntity damagee) {
    this.Manager.GetDamage().NewDamageEvent(damager, damagee, null, 
      EntityDamageEvent.DamageCause.CUSTOM, this._damage / 3.0D, true, true, false, 
      damager.getName(), GetName() + " Recoil");
    

    this.Manager.GetDamage().NewDamageEvent(damagee, damager, null, 
      EntityDamageEvent.DamageCause.CUSTOM, this._damage, true, true, false, 
      damager.getName(), GetName());
    

    UtilPlayer.message(damager, F.main("Game", "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName()) + "."));
    UtilPlayer.message(damagee, F.main("Game", F.name(damager.getName()) + " hit you with " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), this._knockback);
  }
}
