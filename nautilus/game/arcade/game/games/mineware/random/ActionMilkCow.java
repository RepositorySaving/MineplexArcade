package nautilus.game.arcade.game.games.mineware.random;

import mineplex.core.common.util.UtilGear;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ActionMilkCow extends Order
{
  public ActionMilkCow(MineWare host)
  {
    super(host, "milk a cow");
  }
  

  public void Initialize()
  {
    for (Player player : this.Host.GetPlayers(true))
    {
      if (!player.getInventory().contains(Material.BUCKET)) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.BUCKET) });
      }
    }
  }
  



  public void Uninitialize() {}
  


  public void FailItems(Player player) {}
  


  @EventHandler
  public void Update(PlayerInteractEntityEvent event)
  {
    if (!(event.getRightClicked() instanceof Cow)) {
      return;
    }
    if (!UtilGear.isMat(event.getPlayer().getItemInHand(), Material.BUCKET)) {
      return;
    }
    SetCompleted(event.getPlayer());
  }
}
