package nautilus.game.arcade.game.games.turfforts.kits;

import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkConstructor;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;








public class KitInfiltrator
  extends Kit
{
  public KitInfiltrator(ArcadeManager manager)
  {
    super(manager, "Infiltrator", KitAvailability.Green, new String[] {"Able to travel into the enemies turf, but you", "must return to your turf fast, or receive Slow." }, new Perk[] {new PerkConstructor("Constructor", 4.0D, 4, Material.WOOL, "Wool", false), new PerkFletcher(8, 1, false) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    
    int amount = 4;
    if (!this.Manager.GetGame().IsLive()) {
      amount = 48;
    }
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WOOL, this.Manager.GetGame().GetTeam(player).GetColorData(), amount) });
    


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
