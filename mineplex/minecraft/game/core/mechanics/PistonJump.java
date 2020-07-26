package mineplex.minecraft.game.core.mechanics;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PistonJump extends MiniPlugin
{
  private HashMap<Block, Long> _pistonExtend = new HashMap();
  
  public PistonJump(JavaPlugin plugin)
  {
    super("Piston Jump", plugin);
  }
  
  @EventHandler
  public void PistonLaunch(PlayerMoveEvent event)
  {
    Block below = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
    
    if (below.getTypeId() != 33) {
      return;
    }
    if (below.getData() != 1) {
      return;
    }
    if (below.getRelative(BlockFace.UP).getType() != Material.AIR) {
      return;
    }
    if (this._pistonExtend.containsKey(below)) {
      return;
    }
    for (Player player : below.getWorld().getPlayers())
    {
      if (below.equals(player.getLocation().getBlock().getRelative(BlockFace.DOWN)))
      {


        Vector vec = new Vector(0.0D, 1.2D, 0.0D);
        
        player.setVelocity(vec);
        player.setFallDistance(0.0F);
      }
    }
    final Block block = below;
    this._plugin.getServer().getScheduler().scheduleSyncDelayedTask(this._plugin, new Runnable()
    {

      public void run()
      {
        BlockState state = block.getState();
        PistonBaseMaterial pbm = (PistonBaseMaterial)state.getData();
        pbm.setPowered(true);
        state.setData(pbm);
        state.update(true);
        
        block.getRelative(BlockFace.UP).setTypeIdAndData(34, (byte)1, false);
        
        PistonJump.this._pistonExtend.put(block, Long.valueOf(System.currentTimeMillis()));
        

        block.getWorld().playSound(block.getLocation(), Sound.PISTON_EXTEND, 1.0F, 1.0F);
      }
    }, 10L);
  }
  

  @EventHandler
  public void PistonExtendUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Block> retract = new HashSet();
    
    for (Block cur : this._pistonExtend.keySet())
    {
      if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._pistonExtend.get(cur)).longValue(), 600L)) {
        retract.add(cur);
      }
    }
    for (Block cur : retract)
    {
      this._pistonExtend.remove(cur);
      

      if (cur.getTypeId() == 33)
      {

        BlockState state = cur.getState();
        PistonBaseMaterial pbm = (PistonBaseMaterial)state.getData();
        pbm.setPowered(false);
        state.setData(pbm);
        state.update(true);
      }
      
      if (cur.getRelative(BlockFace.UP).getTypeId() == 34) {
        cur.getRelative(BlockFace.UP).setTypeIdAndData(0, (byte)0, true);
      }
      
      cur.getWorld().playSound(cur.getLocation(), Sound.PISTON_RETRACT, 1.0F, 1.0F);
    }
  }
}
