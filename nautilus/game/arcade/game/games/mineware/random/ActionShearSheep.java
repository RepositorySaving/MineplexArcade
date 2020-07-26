package nautilus.game.arcade.game.games.mineware.random;

import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.mineware.order.Order;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ActionShearSheep extends Order
{
  public ActionShearSheep(MineWare host)
  {
    super(host, "shear a sheep");
  }
  

  public void Initialize()
  {
    for (Player player : this.Host.GetPlayers(true))
    {
      if (!player.getInventory().contains(Material.SHEARS)) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.SHEARS) });
      }
    }
  }
  



  public void Uninitialize() {}
  


  public void FailItems(Player player) {}
  


  @EventHandler
  public void Update(PlayerShearEntityEvent event)
  {
    SetCompleted(event.getPlayer());
  }
}
