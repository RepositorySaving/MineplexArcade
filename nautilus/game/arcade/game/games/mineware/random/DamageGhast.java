package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.Material;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DamageGhast extends Order
{
  public DamageGhast(MineWare host)
  {
    super(host, "shoot the ghast");
  }
  

  public void Initialize()
  {
    for (Player player : this.Host.GetPlayers(true))
    {
      if (!player.getInventory().contains(Material.BOW)) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.BOW) });
      }
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : this.Host.GetPlayers(true)) {
      if (!player.getInventory().contains(Material.ARROW)) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ARROW) });
      }
    }
  }
  


  public void Uninitialize() {}
  


  public void FailItems(Player player) {}
  


  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Player player = event.GetDamagerPlayer(true);
    if (player == null) { return;
    }
    LivingEntity ent = event.GetDamageeEntity();
    if (ent == null) { return;
    }
    if (!(ent instanceof Ghast)) {
      return;
    }
    SetCompleted(player);
  }
}
