package mineplex.core.blockrestore;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;





public class BlockRestoreData
{
  protected Block _block;
  protected int _fromID;
  protected byte _fromData;
  protected int _toID;
  protected byte _toData;
  protected long _expireDelay;
  protected long _epoch;
  protected long _meltDelay = 0L;
  protected long _meltLast = 0L;
  
  public BlockRestoreData(Block block, int toID, byte toData, long expireDelay, long meltDelay)
  {
    this._block = block;
    
    this._fromID = block.getTypeId();
    this._fromData = block.getData();
    
    this._toID = toID;
    this._toData = toData;
    
    this._expireDelay = expireDelay;
    this._epoch = System.currentTimeMillis();
    
    this._meltDelay = meltDelay;
    this._meltLast = System.currentTimeMillis();
    

    set();
  }
  
  public boolean expire()
  {
    if (System.currentTimeMillis() - this._epoch < this._expireDelay) {
      return false;
    }
    
    if (melt()) {
      return false;
    }
    
    restore();
    return true;
  }
  
  public boolean melt()
  {
    if ((this._block.getTypeId() != 78) && (this._block.getTypeId() != 80)) {
      return false;
    }
    if ((this._block.getRelative(BlockFace.UP).getTypeId() == 78) || (this._block.getRelative(BlockFace.UP).getTypeId() == 80))
    {
      this._meltLast = System.currentTimeMillis();
      return true;
    }
    
    if (System.currentTimeMillis() - this._meltLast < this._meltDelay) {
      return true;
    }
    
    if (this._block.getTypeId() == 80) {
      this._block.setTypeIdAndData(78, (byte)7, false);
    }
    byte data = this._block.getData();
    if (data <= 0) { return false;
    }
    this._block.setData((byte)(this._block.getData() - 1));
    this._meltLast = System.currentTimeMillis();
    return true;
  }
  
  public void update(int toIDIn, byte toDataIn)
  {
    this._toID = toIDIn;
    this._toData = toDataIn;
    

    set();
  }
  

  public void update(int toID, byte addData, long expireTime)
  {
    if (toID == 78)
    {
      if (this._toID == 78) this._toData = ((byte)Math.min(7, this._toData + addData)); else {
        this._toData = addData;
      }
    }
    this._toID = toID;
    

    set();
    

    this._expireDelay = expireTime;
    this._epoch = System.currentTimeMillis();
  }
  

  public void update(int toID, byte addData, long expireTime, long meltDelay)
  {
    if (toID == 78)
    {
      if (this._toID == 78) this._toData = ((byte)Math.min(7, this._toData + addData)); else {
        this._toData = addData;
      }
    }
    this._toID = toID;
    

    set();
    

    this._expireDelay = expireTime;
    this._epoch = System.currentTimeMillis();
    

    if (this._meltDelay < meltDelay) {
      this._meltDelay = ((this._meltDelay + meltDelay) / 2L);
    }
  }
  
  public void set() {
    if ((this._toID == 78) && (this._toData == 7)) {
      this._block.setTypeIdAndData(80, (byte)0, true);
    } else {
      this._block.setTypeIdAndData(this._toID, this._toData, true);
    }
  }
  
  public void restore() {
    this._block.setTypeIdAndData(this._fromID, this._fromData, true);
  }
  
  public void setFromId(int i)
  {
    this._fromID = i;
  }
  
  public void setFromData(byte i)
  {
    this._fromData = i;
  }
}
