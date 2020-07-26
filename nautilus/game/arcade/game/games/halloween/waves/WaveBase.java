package nautilus.game.arcade.game.games.halloween.waves;

import java.io.PrintStream;
import java.util.ArrayList;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.halloween.Halloween;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public abstract class WaveBase
{
  protected Halloween Host;
  protected String _name;
  protected long _start;
  protected long _duration;
  private int _tick = 0;
  
  protected ArrayList<Location> _spawns;
  
  public WaveBase(Halloween host, String name, long duration, ArrayList<Location> spawns)
  {
    this.Host = host;
    
    this._name = name;
    
    this._start = System.currentTimeMillis();
    this._duration = duration;
    
    this._spawns = spawns;
  }
  
  public Location GetSpawn()
  {
    return (Location)this._spawns.get(UtilMath.r(this._spawns.size()));
  }
  

  public boolean Update(int wave)
  {
    if ((this._tick > 0) && (UtilTime.elapsed(this._start, this._duration)) && (CanEnd()))
    {
      System.out.println("Wave " + wave + " has ended.");
      return true;
    }
    

    if (this._tick == 0) {
      this._start = System.currentTimeMillis();
    }
    
    if (this._tick == 0)
    {
      System.out.println("Wave " + wave + " has started.");
      this.Host.Announce(C.cRed + C.Bold + "Wave " + wave + ": " + C.cYellow + this._name);
      
      for (Player player : UtilServer.getPlayers()) {
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 2.0F, 1.0F);
      }
    }
    
    for (Player player : UtilServer.getPlayers()) {
      player.setExp(Math.min(0.999F, (float)(this._duration - (System.currentTimeMillis() - this._start)) / (float)this._duration));
    }
    
    if (this._tick == 0) {
      SpawnBeacons(this._spawns);
    }
    
    this.Host.CreatureAllowOverride = true;
    Spawn(this._tick++);
    this.Host.CreatureAllowOverride = false;
    
    return false;
  }
  

  public void SpawnBeacons(ArrayList<Location> locs)
  {
    Vector total = new Vector(0, 0, 0);
    for (Location loc : locs)
      total.add(loc.toVector());
    total.multiply(1.0D / locs.size());
    

    Block block = total.toLocation(((Location)locs.get(0)).getWorld()).getBlock().getRelative(BlockFace.DOWN);
    this.Host.Manager.GetBlockRestore().Add(block, 138, (byte)0, this._duration);
    
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        this.Host.Manager.GetBlockRestore().Add(block.getRelative(x, -1, z), 42, (byte)0, this._duration);
      }
    }
    block.getWorld().strikeLightningEffect(block.getLocation());
    

    while (block.getY() < 250)
    {
      block = block.getRelative(BlockFace.UP);
      if (block.getType() != Material.AIR) {
        this.Host.Manager.GetBlockRestore().Add(block, 0, (byte)0, this._duration);
      }
    }
  }
  
  public boolean CanEnd() {
    return true;
  }
  
  public abstract void Spawn(int paramInt);
}
