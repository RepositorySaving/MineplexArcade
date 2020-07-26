package nautilus.game.arcade.game.games.survivalgames;

import java.util.ArrayList;
import org.bukkit.Chunk;
import org.bukkit.Location;




public class ChunkChange
{
  public Chunk Chunk;
  public long Time;
  public ArrayList<BlockChange> Changes;
  public short[] DirtyBlocks = new short[64];
  public short DirtyCount = 0;
  
  public ChunkChange(Location loc, int id, byte data)
  {
    this.Chunk = loc.getChunk();
    
    this.Changes = new ArrayList();
    
    AddChange(loc, id, data);
    
    this.Time = System.currentTimeMillis();
  }
  
  public void AddChange(Location loc, int id, byte data)
  {
    this.Changes.add(new BlockChange(loc, id, data));
    
    if (this.DirtyCount < 63)
    {
      short short1 = (short)((loc.getBlockX() & 0xF) << 12 | (loc.getBlockZ() & 0xF) << 8 | loc.getBlockY());
      
      for (int l = 0; l < this.DirtyCount; l++)
      {
        if (this.DirtyBlocks[l] == short1)
        {
          return;
        }
      }
      
      this.DirtyBlocks[(this.DirtyCount++)] = short1;
    }
    else
    {
      this.DirtyCount = ((short)(this.DirtyCount + 1));
    }
  }
}
