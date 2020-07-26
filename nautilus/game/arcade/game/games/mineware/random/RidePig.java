package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.Material;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RidePig extends Order
{
  public RidePig(MineWare host)
  {
    super(host, "ride a pig");
  }
  

  public void Initialize()
  {
    for (Player player : this.Host.GetPlayers(true))
    {
      if (!player.getInventory().contains(Material.SADDLE)) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.SADDLE) });
      }
    }
  }
  



  public void Uninitialize() {}
  


  public void FailItems(Player player) {}
  


  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : this.Host.GetPlayers(true)) {
      if ((player.isInsideVehicle()) && 
        ((player.getVehicle() instanceof Pig))) {
        SetCompleted(player);
      }
    }
  }
}
