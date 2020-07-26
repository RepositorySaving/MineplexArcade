package mineplex.core.arena;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;




public class Region
{
  private String _name;
  private transient Vector _pointOne;
  private transient Vector _pointTwo;
  private List<String> _owners;
  private Boolean _blockPassage = Boolean.valueOf(false);
  private Boolean _blockChange = Boolean.valueOf(true);
  
  private int _priority;
  
  private int _minX;
  
  private int _minY;
  private int _minZ;
  private int _maxX;
  private int _maxY;
  private int _maxZ;
  
  public Region(String name, Vector pointOne, Vector pointTwo)
  {
    this._name = name;
    this._pointOne = pointOne;
    this._pointTwo = pointTwo;
    this._priority = 0;
    this._owners = new ArrayList();
    
    UpdateMinMax();
  }
  
  public Vector GetMaximumPoint()
  {
    return new Vector(this._maxX, this._maxY, this._maxZ);
  }
  
  public void AdjustRegion(Vector vector)
  {
    this._minX += vector.getBlockX();
    this._minY += vector.getBlockY();
    this._minZ += vector.getBlockZ();
    
    this._maxX += vector.getBlockX();
    this._maxY += vector.getBlockY();
    this._maxZ += vector.getBlockZ();
  }
  
  public Vector GetMinimumPoint()
  {
    return new Vector(this._minX, this._minY, this._minZ);
  }
  
  public Vector GetMidPoint()
  {
    return new Vector((this._maxX - this._minX) / 2 + this._minX, (this._maxY - this._minY) / 2 + this._minY, (this._maxZ - this._minZ) / 2 + this._minZ);
  }
  
  public void SetPriority(int priority)
  {
    this._priority = priority;
  }
  
  public int GetPriority()
  {
    return this._priority;
  }
  
  public Boolean Contains(Vector v)
  {
    if ((v.getBlockX() >= this._minX) && (v.getBlockX() <= this._maxX) && 
      (v.getBlockY() >= this._minY) && (v.getBlockY() <= this._maxY) && 
      (v.getBlockZ() >= this._minZ) && (v.getBlockZ() <= this._maxZ))
      return Boolean.valueOf(true); return Boolean.valueOf(false);
  }
  


  public void AddOwner(String name)
  {
    if (!this._owners.contains(name.toLowerCase()))
    {
      this._owners.add(name.toLowerCase());
    }
  }
  
  public void RemoveOwner(String name)
  {
    this._owners.remove(name.toLowerCase());
  }
  
  public void SetOwners(List<String> owners)
  {
    this._owners = owners;
    
    for (String ownerName : this._owners)
    {
      ownerName = ownerName.toLowerCase();
    }
  }
  
  public void SetEnter(Boolean canEnter)
  {
    this._blockPassage = Boolean.valueOf(!canEnter.booleanValue());
  }
  
  public void SetChangeBlocks(Boolean canChangeBlocks)
  {
    this._blockChange = Boolean.valueOf(!canChangeBlocks.booleanValue());
  }
  
  public Boolean CanEnter(String playerName)
  {
    if (this._blockPassage.booleanValue())
    {
      if (!this._owners.contains(playerName.toLowerCase()))
      {
        return Boolean.valueOf(false);
      }
    }
    
    return Boolean.valueOf(true);
  }
  
  public Boolean CanChangeBlocks(String playerName)
  {
    if (this._blockChange.booleanValue())
    {
      if (!this._owners.contains(playerName.toLowerCase()))
      {
        return Boolean.valueOf(false);
      }
    }
    
    return Boolean.valueOf(true);
  }
  
  public String GetName()
  {
    return this._name;
  }
  
  private void UpdateMinMax()
  {
    this._minX = Math.min(this._pointOne.getBlockX(), this._pointTwo.getBlockX());
    this._minY = Math.min(this._pointOne.getBlockY(), this._pointTwo.getBlockY());
    this._minZ = Math.min(this._pointOne.getBlockZ(), this._pointTwo.getBlockZ());
    
    this._maxX = Math.max(this._pointOne.getBlockX(), this._pointTwo.getBlockX());
    this._maxY = Math.max(this._pointOne.getBlockY(), this._pointTwo.getBlockY());
    this._maxZ = Math.max(this._pointOne.getBlockZ(), this._pointTwo.getBlockZ());
  }
  

  public String toString()
  {
    return "Maximum point: " + GetMaximumPoint() + " Minimum point: " + GetMinimumPoint();
  }
}
