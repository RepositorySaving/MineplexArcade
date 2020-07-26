package nautilus.game.arcade.game.games.common.dominate_data;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.games.common.Domination;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Resupply
{
  private Domination Host;
  private Location _loc;
  private long _time;
  private Item _ent;
  
  public Resupply(Domination host, Location loc)
  {
    this.Host = host;
    
    this._time = System.currentTimeMillis();
    
    this._loc = loc;
    
    this._loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.IRON_BLOCK);
  }
  
  public void Update()
  {
    if (this._ent != null)
    {
      if (!this._ent.isValid())
      {
        this._ent.remove();
        this._ent = null;
      }
      
      return;
    }
    
    if (!mineplex.core.common.util.UtilTime.elapsed(this._time, 60000L)) {
      return;
    }
    
    this._ent = this._loc.getWorld().dropItem(this._loc.clone().add(0.0D, 1.0D, 0.0D), new ItemStack(Material.CHEST));
    this._ent.setVelocity(new Vector(0, 1, 0));
    

    this._loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.GOLD_BLOCK);
    

    UtilFirework.playFirework(this._loc.clone().add(0.0D, 1.0D, 0.0D), FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).with(FireworkEffect.Type.BURST).trail(true).build());
  }
  
  public void Pickup(Player player, Item item)
  {
    if (this._ent == null) {
      return;
    }
    if (!this._ent.equals(item)) {
      return;
    }
    if (!this.Host.IsAlive(player)) {
      return;
    }
    if (player.getGameMode() != GameMode.SURVIVAL) {
      return;
    }
    GameTeam team = this.Host.GetTeam(player);
    if (team == null) { return;
    }
    
    this._ent.remove();
    this._ent = null;
    this._time = System.currentTimeMillis();
    this._loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.IRON_BLOCK);
    
    ((ClientClass)this.Host.Manager.getClassManager().Get(player)).ResetItems();
    

    UtilPlayer.message(player, C.cYellow + C.Bold + "Your inventory was restocked!");
    

    UtilFirework.playFirework(this._loc.clone().add(0.0D, 1.0D, 0.0D), FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).trail(true).build());
  }
}
