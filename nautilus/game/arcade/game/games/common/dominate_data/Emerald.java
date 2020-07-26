package nautilus.game.arcade.game.games.common.dominate_data;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.games.common.Domination;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Emerald
{
  private Domination Host;
  private Location _loc;
  private long _time;
  private Item _ent;
  
  public Emerald(Domination host, Location loc)
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
    
    if (!UtilTime.elapsed(this._time, 60000L)) {
      return;
    }
    
    this._ent = this._loc.getWorld().dropItem(this._loc.clone().add(0.0D, 1.0D, 0.0D), new ItemStack(Material.EMERALD));
    this._ent.setVelocity(new Vector(0, 1, 0));
    

    this._loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.EMERALD_BLOCK);
    

    UtilFirework.playFirework(this._loc.clone().add(0.0D, 1.0D, 0.0D), FireworkEffect.builder().flicker(false).withColor(Color.GREEN).with(FireworkEffect.Type.BURST).trail(true).build());
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
    

    this.Host.AddScore(team, 300);
    

    UtilPlayer.message(player, C.cGreen + C.Bold + "You scored 300 Points for your team!");
    

    UtilFirework.playFirework(this._loc.clone().add(0.0D, 1.0D, 0.0D), FireworkEffect.builder().flicker(false).withColor(Color.GREEN).with(FireworkEffect.Type.BALL_LARGE).trail(true).build());
    

    this.Host.AddGems(player, 3.0D, "Emerald Powerup", true);
  }
}
