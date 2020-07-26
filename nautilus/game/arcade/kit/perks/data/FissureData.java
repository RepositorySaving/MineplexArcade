package nautilus.game.arcade.kit.perks.data;

import java.util.ArrayList;
import java.util.HashSet;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.perks.PerkFissure;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class FissureData
{
  private PerkFissure Host;
  private Player _player;
  private Vector _vec;
  private Location _loc;
  private Location _startLoc;
  private int _height = 0;
  private int _handled = 0;
  
  private HashSet<Player> _hit = new HashSet();
  
  private ArrayList<Block> _path = new ArrayList();
  
  public FissureData(PerkFissure host, Player player, Vector vec, Location loc)
  {
    this.Host = host;
    
    vec.setY(0);
    vec.normalize();
    vec.multiply(0.1D);
    
    this._player = player;
    this._vec = vec;
    this._loc = loc;
    this._startLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    
    MakePath();
  }
  
  private void MakePath()
  {
    while (UtilMath.offset2d(this._loc, this._startLoc) < 14.0D)
    {
      this._loc.add(this._vec);
      
      Block block = this._loc.getBlock();
      
      if (!block.equals(this._startLoc.getBlock()))
      {

        if (!this._path.contains(block))
        {


          if (UtilBlock.solid(block.getRelative(BlockFace.UP)))
          {
            this._loc.add(0.0D, 1.0D, 0.0D);
            block = this._loc.getBlock();
            
            if (!UtilBlock.solid(block.getRelative(BlockFace.UP))) {}



          }
          else if (!UtilBlock.solid(block))
          {
            this._loc.add(0.0D, -1.0D, 0.0D);
            block = this._loc.getBlock();
            
            if (!UtilBlock.solid(block)) {
              return;
            }
          }
          if (UtilMath.offset(block.getLocation().add(0.5D, 0.5D, 0.5D), this._loc) <= 0.5D)
          {

            this._path.add(block);
            

            this._loc.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
            

            for (Player cur : block.getWorld().getPlayers())
              if ((!cur.equals(this._player)) && 
                (UtilMath.offset(block.getLocation().add(0.5D, 0.5D, 0.5D), cur.getLocation()) < 1.5D))
              {

                this.Host.Manager.GetCondition().Factory().Slow("Fissure", cur, this._player, 4.0D, 1, false, true, true, true); }
          }
        } }
    }
  }
  
  public boolean Update() {
    if (this._handled >= this._path.size()) {
      return true;
    }
    Block block = (Block)this._path.get(this._handled);
    

    if (block.getTypeId() == 46) {
      return false;
    }
    Block up = block.getRelative(0, this._height + 1, 0);
    

    if (!UtilBlock.airFoliage(up))
    {
      this._loc.getWorld().playEffect(up.getLocation(), Effect.STEP_SOUND, up.getTypeId());
      this._height = 0;
      this._handled += 1;
      return false;
    }
    

    if (block.getTypeId() == 1) this.Host.Manager.GetBlockRestore().Add(block, 4, block.getData(), 14000L);
    if (block.getTypeId() == 2) this.Host.Manager.GetBlockRestore().Add(block, 3, block.getData(), 14000L);
    if (block.getTypeId() == 98) { this.Host.Manager.GetBlockRestore().Add(block, 98, (byte)2, 14000L);
    }
    this.Host.Manager.GetBlockRestore().Add(up, block.getTypeId(), block.getData(), 10000 - 1000 * this._height);
    this._height += 1;
    

    up.getWorld().playEffect(up.getLocation(), Effect.STEP_SOUND, block.getTypeId());
    

    for (Player cur : up.getWorld().getPlayers()) {
      if (!cur.equals(this._player))
      {

        if (cur.getLocation().getBlock().equals(block))
        {
          cur.teleport(cur.getLocation().add(0.0D, 1.0D, 0.0D));
        }
        



        if ((!this._hit.contains(cur)) && 
          (UtilMath.offset(up.getLocation().add(0.5D, 0.5D, 0.5D), cur.getLocation()) < 1.5D))
        {
          this._hit.add(cur);
          
          int damage = 4 + this._handled;
          

          this.Host.Manager.GetDamage().NewDamageEvent(cur, this._player, null, 
            EntityDamageEvent.DamageCause.CUSTOM, damage, false, false, false, 
            this._player.getName(), "Fissure");
          

          mineplex.core.common.util.UtilPlayer.message(cur, F.main("Game", F.name(this._player.getName()) + " hit you with " + F.skill(this.Host.GetName()) + "."));
          
          final Player fPlayer = cur;
          final Location fLoc = up.getLocation().add(0.5D, 0.5D, 0.5D);
          
          this.Host.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Host.Manager.GetPlugin(), new Runnable()
          {

            public void run()
            {
              mineplex.core.common.util.UtilAction.velocity(fPlayer, UtilAlg.getTrajectory2d(fLoc, fPlayer.getLocation()), 
                1.0D + 0.1D * FissureData.this._handled, true, 0.6D + 0.05D * FissureData.this._handled, 0.0D, 10.0D, true);
            }
          }, 4L);
        }
      }
    }
    
    if (this._height >= Math.min(2, this._handled / 3 + 1))
    {
      this._height = 0;
      this._handled += 1;
    }
    
    return this._handled >= this._path.size();
  }
  
  public void Clear()
  {
    this._hit.clear();
    this._path.clear();
    this.Host = null;
    this._player = null;
    this._loc = null;
    this._startLoc = null;
  }
}
