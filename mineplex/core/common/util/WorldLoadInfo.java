package mineplex.core.common.util;

import org.bukkit.World;


public class WorldLoadInfo
{
  private World _world;
  private int _minChunkX;
  private int _minChunkZ;
  private int _maxChunkX;
  private int _maxChunkZ;
  public int CurrentChunkX;
  public int CurrentChunkZ;
  
  public WorldLoadInfo(World world, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ)
  {
    this._world = world;
    this._minChunkX = minChunkX;
    this._minChunkZ = minChunkZ;
    this._maxChunkX = maxChunkX;
    this._maxChunkZ = maxChunkZ;
    
    this.CurrentChunkX = minChunkX;
    this.CurrentChunkZ = minChunkZ;
  }
  
  public World GetWorld()
  {
    return this._world;
  }
  
  public int GetMinChunkX()
  {
    return this._minChunkX;
  }
  
  public int GetMinChunkZ()
  {
    return this._minChunkZ;
  }
  
  public int GetMaxChunkX()
  {
    return this._maxChunkX;
  }
  
  public int GetMaxChunkZ()
  {
    return this._maxChunkZ;
  }
}
