package nautilus.game.arcade.kit.perks.data;

import java.util.ArrayList;
import mineplex.core.common.util.UtilMath;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;



public class IcePathData
{
  private ArrayList<Block> _blocks;
  
  public IcePathData(Player player)
  {
    this._blocks = new ArrayList();
    

    if (Math.abs(player.getLocation().getDirection().getX()) > Math.abs(player.getLocation().getDirection().getZ()))
    {
      GetBlocks(player.getLocation().add(0.0D, 0.0D, 1.0D), 16);
      GetBlocks(player.getLocation().add(0.0D, 0.0D, -1.0D), 16);
    }
    else
    {
      GetBlocks(player.getLocation().add(1.0D, 0.0D, 0.0D), 16);
      GetBlocks(player.getLocation().add(-1.0D, 0.0D, 0.0D), 16);
    }
    
    GetBlocks(player.getLocation(), 16);
    

    for (int i = 0; i < this._blocks.size(); i++)
    {
      for (int j = 0; j + 1 < this._blocks.size(); j++)
      {

        if (UtilMath.offset(player.getLocation(), ((Block)this._blocks.get(j)).getLocation().add(0.5D, 0.5D, 0.5D)) > UtilMath.offset(player.getLocation(), ((Block)this._blocks.get(j + 1)).getLocation().add(0.5D, 0.5D, 0.5D)))
        {
          Block temp = (Block)this._blocks.get(j);
          this._blocks.set(j, (Block)this._blocks.get(j + 1));
          this._blocks.set(j + 1, temp);
        }
      }
    }
  }
  

  public void GetBlocks(Location loc, int length)
  {
    loc.subtract(0.0D, 1.0D, 0.0D);
    
    Vector dir = loc.getDirection();
    
    double hLength = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
    
    if (Math.abs(dir.getY()) > hLength)
    {
      if (dir.getY() > 0.0D) {
        dir.setY(hLength);
      } else {
        dir.setY(-hLength);
      }
      dir.normalize();
    }
    

    loc.subtract(dir.clone().multiply(2));
    
    double dist = 0.0D;
    while (dist < length)
    {
      dist += 0.2D;
      
      loc.add(dir.clone().multiply(0.2D));
      
      if (loc.getBlock().getTypeId() != 79)
      {

        if ((loc.getBlock().getTypeId() == 0) || (loc.getBlock().getTypeId() == 78))
        {
          if (!this._blocks.contains(loc.getBlock()))
          {
            this._blocks.add(loc.getBlock());
          }
        }
      }
    }
  }
  
  public Block GetNextBlock() {
    if (this._blocks.isEmpty()) {
      return null;
    }
    return (Block)this._blocks.remove(0);
  }
}
