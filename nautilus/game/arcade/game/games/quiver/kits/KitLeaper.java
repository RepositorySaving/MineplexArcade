package nautilus.game.arcade.game.games.quiver.kits;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;









public class KitLeaper
  extends Kit
{
  public KitLeaper(ArcadeManager manager)
  {
    super(manager, "Jumper", KitAvailability.Free, new String[] {"Evade and kill using your double jump!" }, new Perk[] {new PerkDoubleJump("Double Jump", 0.9D, 0.9D, true) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    
    if (this.Manager.GetGame().GetState() == Game.GameState.Live)
    {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, F.item("Super Arrow")) });
      
      final Player fPlayer = player;
      
      UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          UtilInv.Update(fPlayer);
        }
      }, 10L);
    }
  }
}
