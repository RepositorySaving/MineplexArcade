package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import java.util.Iterator;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;



public class Snake
{
  private ArrayList<Location> _path = new ArrayList();
  private byte _color = 0;
  
  private int _index = 0;
  private boolean _colorTick = false;
  
  private int _pathId = 39;
  
  public Snake(Location loc, ArrayList<Location> path)
  {
    this._path = new ArrayList();
    

    this._path.add(loc);
    MapUtil.QuickChangeBlockAt(loc, this._pathId, (byte)0);
    

    for (Block block : UtilBlock.getSurrounding(loc.getBlock(), false))
    {
      if (block.getType() == Material.WOOL)
      {
        this._path.add(block.getLocation().add(0.5D, 0.5D, 0.5D));
        this._color = block.getData();
        MapUtil.QuickChangeBlockAt(block.getLocation(), this._pathId, (byte)0);
        break;
      }
    }
    

    for (int i = 0; i < 100; i++)
    {
      Object pathIterator = path.iterator();
      
      while (((Iterator)pathIterator).hasNext())
      {
        Location pathLoc = (Location)((Iterator)pathIterator).next();
        
        if (UtilMath.offset(((Location)this._path.get(this._path.size() - 1)).getBlock().getLocation(), pathLoc.getBlock().getLocation()) <= 1.0D)
        {
          this._path.add(pathLoc);
          MapUtil.QuickChangeBlockAt(pathLoc, this._pathId, (byte)0);
          ((Iterator)pathIterator).remove();
        }
      }
    }
  }
  
  public void Update()
  {
    if (this._path.isEmpty()) {
      return;
    }
    
    MapUtil.QuickChangeBlockAt((Location)this._path.get(this._index), 35, GetColor());
    
    int back = this._index - 10;
    if (back < 0) {
      back += this._path.size();
    }
    
    MapUtil.QuickChangeBlockAt((Location)this._path.get(back), this._pathId, (byte)0);
    

    if (this._path.size() > 50)
    {
      int newIndex = (this._index + this._path.size() / 2) % this._path.size();
      

      MapUtil.QuickChangeBlockAt((Location)this._path.get(newIndex), 35, GetColor());
      
      back = newIndex - 10;
      if (back < 0) {
        back += this._path.size();
      }
      
      MapUtil.QuickChangeBlockAt((Location)this._path.get(back), this._pathId, (byte)0);
    }
    
    this._index = ((this._index + 1) % this._path.size());
    this._colorTick = (!this._colorTick);
  }
  
  public byte GetColor()
  {
    if (this._colorTick)
      return this._color;
    return 0;
  }
}
