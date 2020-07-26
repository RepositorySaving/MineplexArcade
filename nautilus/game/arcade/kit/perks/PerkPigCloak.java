package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkPigCloak extends Perk
{
  public PerkPigCloak()
  {
    super("Cloak", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Cloak" });
  }
  

  @EventHandler
  public void Use(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    if (!Recharge.Instance.use(player, GetName(), GetName(), 10000L, true, true)) {
      return;
    }
    
    this.Manager.GetCondition().Factory().Cloak(GetName(), player, player, 5.0D, false, false);
    
    for (int i = 0; i < 3; i++)
    {
      player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SHEEP_SHEAR, 2.0F, 0.5F);
      player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.STEP_SOUND, 80);
    }
    

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
    if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    this.Manager.GetCondition().EndCondition(event.getPlayer(), null, GetName());
  }
  

  @EventHandler
  public void Reset(PlayerDeathEvent event)
  {
    this.Manager.GetCondition().EndCondition(event.getEntity(), null, GetName());
  }
}
