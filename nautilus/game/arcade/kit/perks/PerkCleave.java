package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PerkCleave extends Perk
{
  public PerkCleave(double splash)
  {
    super("Cleave", new String[] {C.cGray + "Attacks deal " + (int)(100.0D * splash) + "% damage to nearby enemies" });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Skill(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    if (event.GetReason() != null) {
      return;
    }
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!UtilGear.isAxe(damager.getItemInHand())) {
      return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    event.AddMod(damager.getName(), GetName(), 0.0D, false);
    

    for (Player other : UtilPlayer.getNearby(damagee.getLocation(), 2.4D))
    {
      if (!other.equals(damagee))
      {

        if (this.Manager.CanHurt(damager, other))
        {


          this.Manager.GetDamage().NewDamageEvent(other, damager, null, 
            EntityDamageEvent.DamageCause.CUSTOM, event.GetDamageInitial(), true, true, false, 
            damager.getName(), GetName());
        }
      }
    }
  }
}
