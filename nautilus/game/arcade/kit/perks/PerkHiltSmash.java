package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PerkHiltSmash extends Perk
{
  private HashSet<Player> _used = new HashSet();
  


  public PerkHiltSmash()
  {
    super("Hilt Smash", new String[] {C.cYellow + "Block on Player" + C.cGray + " to use " + C.cGreen + "Hilt Smash" });
  }
  

  public boolean CanUse(Player player)
  {
    if (!this.Kit.HasKit(player)) {
      return false;
    }
    
    if (!mineplex.core.common.util.UtilGear.isSword(player.getItemInHand())) {
      return false;
    }
    
    if (!Recharge.Instance.use(player, GetName(), 6000L, true, true)) {
      return false;
    }
    
    return true;
  }
  
  @EventHandler
  public void Hit(PlayerInteractEntityEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!CanUse(player)) {
      return;
    }
    org.bukkit.entity.Entity ent = event.getRightClicked();
    
    if (ent == null) {
      return;
    }
    if (!(ent instanceof LivingEntity)) {
      return;
    }
    if (UtilMath.offset(player, ent) > 3.0D)
    {
      UtilPlayer.message(player, F.main("Skill", "You missed " + F.skill(GetName()) + "."));
      return;
    }
    

    this._used.add(player);
    

    this.Manager.GetDamage().NewDamageEvent((LivingEntity)ent, player, null, 
      EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5.0D, false, true, false, 
      player.getName(), GetName());
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    
    this.Manager.GetCondition().Factory().Slow(GetName(), damagee, damager, 2.0D, 1, false, false, true, true);
    this.Manager.GetCondition().Factory().Blind(GetName(), damagee, damager, 2.0D, 1, false, false, false);
    

    damagee.getWorld().playSound(damagee.getLocation(), org.bukkit.Sound.ZOMBIE_WOOD, 1.0F, 1.2F);
    damagee.getWorld().playEffect(damagee.getLocation(), Effect.STEP_SOUND, 17);
    

    UtilPlayer.message(damager, F.main("Skill", "You used " + F.skill(GetName()) + "."));
    UtilPlayer.message(damagee, F.main("Skill", F.name(damager.getName()) + " hit you with " + F.skill(GetName()) + "."));
  }
}
