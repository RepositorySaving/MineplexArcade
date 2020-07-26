package nautilus.game.arcade.ore;

import java.util.Iterator;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.explosion.ExplosionEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class OreHider
{
  private NautHashMap<Location, Material> _hidden = new NautHashMap();
  private boolean _visible = false;
  
  public void AddOre(Location loc, Material type)
  {
    boolean visible = false;
    
    for (Block block : UtilBlock.getSurrounding(loc.getBlock(), false))
    {
      if (!block.getType().isOccluding())
      {
        visible = true;
        break;
      }
    }
    
    if (visible)
    {
      loc.getBlock().setType(type);
    }
    else
    {
      this._hidden.put(loc.getBlock().getLocation(), type);
    }
  }
  
  public void BlockBreak(BlockBreakEvent event)
  {
    for (Block block : UtilBlock.getSurrounding(event.getBlock(), false))
    {
      if (this._hidden.containsKey(block.getLocation()))
      {
        block.setType((Material)this._hidden.remove(block.getLocation()));
      }
    }
  }
  
  public void Explosion(ExplosionEvent event) {
    Iterator localIterator2;
    for (Iterator localIterator1 = event.GetBlocks().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Block cur = (Block)localIterator1.next();
      
      localIterator2 = UtilBlock.getSurrounding(cur, false).iterator(); continue;Block block = (Block)localIterator2.next();
      
      if (this._hidden.containsKey(block.getLocation()))
      {
        block.setType((Material)this._hidden.remove(block.getLocation()));
      }
    }
  }
  

  public void ToggleVisibility()
  {
    if (!this._visible)
    {
      for (Location loc : this._hidden.keySet())
      {
        loc.getBlock().setType((Material)this._hidden.get(loc));
      }
      
    }
    else {
      for (Location loc : this._hidden.keySet())
      {
        loc.getBlock().setType(Material.STONE);
      }
    }
    
    this._visible = (!this._visible);
  }
  
  public NautHashMap<Location, Material> GetHiddenOre()
  {
    return this._hidden;
  }
}
