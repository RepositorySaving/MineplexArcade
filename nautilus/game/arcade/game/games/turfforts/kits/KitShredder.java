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
import nautilus.game.arcade.kit.perks.PerkBarrage;
import nautilus.game.arcade.kit.perks.PerkConstructor;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;








public class KitShredder
  extends Kit
{
  public KitShredder(ArcadeManager manager)
  {
    super(manager, "Shredder", KitAvailability.Blue, new String[] {"Arrows are weaker, but shred through forts." }, new Perk[] {new PerkConstructor("Constructor", 4.0D, 6, Material.WOOL, "Wool", false), new PerkFletcher(4, 2, false), new PerkBarrage(5, 250L, false, false) }, EntityType.ZOMBIE, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
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
