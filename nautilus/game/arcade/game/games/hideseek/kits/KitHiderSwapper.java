package nautilus.game.arcade.game.games.hideseek.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilServer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBlock;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;








public class KitHiderSwapper
  extends Kit
{
  public KitHiderSwapper(ArcadeManager manager)
  {
    super(manager, "Swapper Hider", KitAvailability.Free, new String[] {"Can change form unlimited times!" }, new Perk[0], EntityType.SLIME, new ItemStack(Material.BEACON));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().setItem(3, ItemStackFactory.Instance.CreateStack(Material.SLIME_BALL, (byte)0, 1, C.cYellow + C.Bold + "Click Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Change Form"));
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ((Slime)ent).setSize(2);
    
    DisguiseSlime slime = new DisguiseSlime(ent);
    slime.SetSize(2);
    slime.SetName(GetAvailability().GetColor() + GetName());
    slime.SetCustomNameVisible(true);
    
    ToBlock(ent, slime);
  }
  
  public void ToBlock(final LivingEntity ent, final DisguiseSlime slime)
  {
    if ((this.Manager.GetGame() == null) || (this.Manager.GetGame().InProgress())) {
      return;
    }
    double r = Math.random();
    
    if (r > 0.75D) { this.Manager.GetDisguise().disguise(new DisguiseBlock(ent, 54, 0));
    } else if (r > 0.5D) { this.Manager.GetDisguise().disguise(new DisguiseBlock(ent, 145, 0));
    } else if (r > 0.25D) this.Manager.GetDisguise().disguise(new DisguiseBlock(ent, 140, 11)); else {
      this.Manager.GetDisguise().disguise(new DisguiseBlock(ent, 47, 0));
    }
    UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        KitHiderSwapper.this.ToSlime(ent, slime);
      }
    }, 60L);
  }
  
  public void ToSlime(final LivingEntity ent, final DisguiseSlime slime)
  {
    if ((this.Manager.GetGame() == null) || (this.Manager.GetGame().InProgress())) {
      return;
    }
    this.Manager.GetDisguise().disguise(slime);
    
    UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        KitHiderSwapper.this.ToBlock(ent, slime);
      }
    }, 60L);
  }
}
