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
import nautilus.game.arcade.kit.perks.PerkShockingStrike;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;








public class KitHiderShocker
  extends Kit
{
  public KitHiderShocker(ArcadeManager manager)
  {
    super(manager, "Shocking Hider", KitAvailability.Blue, new String[] {"Shock and stun seekers!" }, new Perk[] {new PerkShockingStrike() }, EntityType.SLIME, new ItemStack(Material.REDSTONE_LAMP_OFF));
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
    
    DisguiseBlock block = new DisguiseBlock(ent, 61, 0);
    
    ToBlock(block, slime);
  }
  
  public void ToBlock(final DisguiseBlock block, final DisguiseSlime slime)
  {
    if ((this.Manager.GetGame() == null) || (this.Manager.GetGame().InProgress())) {
      return;
    }
    this.Manager.GetDisguise().disguise(block);
    
    UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        KitHiderShocker.this.ToSlime(block, slime);
      }
    }, 60L);
  }
  
  public void ToSlime(final DisguiseBlock block, final DisguiseSlime slime)
  {
    if ((this.Manager.GetGame() == null) || (this.Manager.GetGame().InProgress())) {
      return;
    }
    this.Manager.GetDisguise().disguise(slime);
    
    UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        KitHiderShocker.this.ToBlock(block, slime);
      }
    }, 60L);
  }
}
