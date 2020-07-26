package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class PerkSmashStats
  extends Perk
{
  private double _damage;
  private double _knockbackTaken;
  private double _regen;
  
  public PerkSmashStats(double damage, double knockbackTaken, double regen, double armor)
  {
    super("Smash Stats", new String[] {C.cAqua + "Damage: " + C.cWhite + damage + C.cWhite + "        " + C.cAqua + "Knockback Taken: " + C.cWhite + (int)(knockbackTaken * 100.0D) + "%", C.cAqua + "Armor: " + C.cWhite + armor + C.cWhite + "          " + C.cAqua + "Health Regeneration: " + C.cWhite + regen + " per Second" });
    

    this._damage = damage;
    this._knockbackTaken = knockbackTaken;
    this._regen = regen;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this.Manager.IsAlive(damager)) {
      return;
    }
    double mod = this._damage - event.GetDamageInitial();
    
    event.AddMod(damager.getName(), "Attack", mod, true);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    if (!this.Manager.IsAlive(damagee)) {
      return;
    }
    event.AddKnockback("Knockback Multiplier", this._knockbackTaken);
  }
  
  @EventHandler
  public void Regeneration(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        UtilPlayer.health(player, this._regen);
      }
    }
  }
}
