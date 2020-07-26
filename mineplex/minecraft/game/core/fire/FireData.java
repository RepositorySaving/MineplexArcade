package mineplex.minecraft.game.core.fire;

import org.bukkit.entity.LivingEntity;

public class FireData
{
  private LivingEntity _owner;
  private long _expireTime;
  private long _delayTime;
  private double _burnTime;
  private int _damage;
  private String _skillName;
  
  public FireData(LivingEntity owner, double expireTime, double delayTime, double burnTime, int damage, String skillName)
  {
    this._owner = owner;
    this._expireTime = (System.currentTimeMillis() + (1000.0D * expireTime));
    this._delayTime = (System.currentTimeMillis() + (1000.0D * delayTime));
    this._burnTime = burnTime;
    this._damage = damage;
    this._skillName = skillName;
  }
  
  public LivingEntity GetOwner()
  {
    return this._owner;
  }
  
  public double GetBurnTime()
  {
    return this._burnTime;
  }
  
  public int GetDamage()
  {
    return this._damage;
  }
  
  public String GetName()
  {
    return this._skillName;
  }
  
  public boolean IsPrimed()
  {
    return System.currentTimeMillis() > this._delayTime;
  }
  
  public boolean Expired()
  {
    return System.currentTimeMillis() > this._expireTime;
  }
}
