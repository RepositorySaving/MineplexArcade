package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.UtilBlock;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PerkSmasher extends nautilus.game.arcade.kit.Perk
{
  public PerkSmasher()
  {
    super("Smasher", new String[] {mineplex.core.common.util.C.cGray + "Hitting blocks damages all surrounding blocks" });
  }
  

  @org.bukkit.event.EventHandler
  public void BlockSmash(BlockDamageEvent event)
  {
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (!this.Manager.GetGame().IsAlive(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 250L, false, false)) {
      return;
    }
    for (Block block : UtilBlock.getSurrounding(event.getBlock(), false))
    {
      BlockDamageEvent blockDamage = new BlockDamageEvent(event.getPlayer(), block, event.getPlayer().getItemInHand(), false);
      this.Manager.GetPlugin().getServer().getPluginManager().callEvent(blockDamage);
    }
    
    BlockDamageEvent blockDamage = new BlockDamageEvent(event.getPlayer(), event.getBlock(), event.getPlayer().getItemInHand(), false);
    this.Manager.GetPlugin().getServer().getPluginManager().callEvent(blockDamage);
  }
  
  @org.bukkit.event.EventHandler
  public void BlockSmash(BlockBreakEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (!this.Manager.GetGame().IsAlive(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 50L, false, false)) {
      return;
    }
    for (Block block : UtilBlock.getSurrounding(event.getBlock(), false))
    {
      BlockBreakEvent blockDamage = new BlockBreakEvent(block, player);
      this.Manager.GetPlugin().getServer().getPluginManager().callEvent(blockDamage);
    }
  }
}
