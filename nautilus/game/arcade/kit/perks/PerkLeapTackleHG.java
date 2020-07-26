package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkLeapTackleHG extends Perk
{
  private HashMap<LivingEntity, Long> _live = new HashMap();
  


  public PerkLeapTackleHG()
  {
    super("Leap Attack", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Sword/Axe to " + C.cGreen + "Leap Tackle" });
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
    if ((!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) && (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD"))) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    mineplex.core.common.util.UtilAction.velocity(player, player.getLocation().getDirection(), 1.2D, false, 0.0D, 0.2D, 1.2D, true);
    

    this._live.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void End(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    for (Player player : this.Manager.GetGame().GetPlayers(true)) {
      if (this._live.containsKey(player)) {
        for (Player other : this.Manager.GetGame().GetPlayers(true))
          if ((other.getGameMode() == GameMode.SURVIVAL) && 
            (!other.equals(player)) && 
            (mineplex.core.common.util.UtilMath.offset(player, other) < 2.0D))
          {
            Hit(player, other);
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

            this._live.remove(player); } }
      }
    }
  }
  
  public void Hit(Player damager, LivingEntity damagee) {
    damagee.playEffect(EntityEffect.HURT);
    

    this.Manager.GetCondition().Factory().Slow(GetName(), damagee, damager, 1.0D, 3, false, false, true, false);
    this.Manager.GetCondition().Factory().Slow(GetName(), damager, damagee, 1.0D, 3, false, false, true, false);
    

    UtilPlayer.message(damager, F.main("Game", "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName()) + "."));
    UtilPlayer.message(damagee, F.main("Game", F.name(damager.getName()) + " hit you with " + F.skill(GetName()) + "."));
  }
}
