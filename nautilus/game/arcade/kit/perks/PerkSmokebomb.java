package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkSmokebomb extends Perk
{
  public PerkSmokebomb()
  {
    super("Smoke Bomb", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Sword/Axe to " + C.cGreen + "Smoke Bomb" });
  }
  

  @EventHandler
  public void Use(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if ((!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) && (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD"))) {
      return;
    }
    event.setCancelled(true);
    
    if (!Recharge.Instance.use(player, GetName(), GetName(), 20000L, true, true)) {
      return;
    }
    
    this.Manager.GetCondition().Factory().Cloak(GetName(), player, player, 8.0D, false, false);
    


    for (Player other : UtilPlayer.getNearby(player.getLocation(), 6.0D))
    {
      if (!other.equals(player))
      {

        this.Manager.GetCondition().Factory().Blind(GetName(), player, player, 2.0D, 0, false, false, true);
      }
    }
    
    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.FIZZ, 2.0F, 0.5F);
    mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_EXPLODE, player.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    

    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamagee(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    
    this.Manager.GetCondition().EndCondition(damagee, null, GetName());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamager(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    
    this.Manager.GetCondition().EndCondition(damager, null, GetName());
  }
  
  @EventHandler
  public void EndInteract(PlayerInteractEvent event)
  {
    if (!this.Kit.HasKit(event.getPlayer())) {
      return;
    }
    this.Manager.GetCondition().EndCondition(event.getPlayer(), null, GetName());
  }
  
  @EventHandler
  public void Smoke(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (!this.Kit.HasKit(cur)) {
        return;
      }
      Condition cond = this.Manager.GetCondition().GetActiveCondition(cur, mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK);
      if (cond != null)
      {
        if (cond.GetReason().equals(GetName()))
        {


          cur.getWorld().playEffect(cur.getLocation(), org.bukkit.Effect.SMOKE, 4);
        }
      }
    }
  }
  
  @EventHandler
  public void Reset(PlayerDeathEvent event) {
    this.Manager.GetCondition().EndCondition(event.getEntity(), null, GetName());
  }
}
