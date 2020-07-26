package mineplex.minecraft.game.core.condition;

import org.bukkit.entity.LivingEntity;

public class Condition
{
  protected ConditionManager Manager;
  protected long _time;
  protected String _reason;
  
  public static enum ConditionType {
    CLOAK, 
    SHOCK, 
    SILENCE, 
    BURNING, 
    FALLING, 
    LIGHTNING, 
    INVULNERABLE, 
    EXPLOSION, 
    FIRE_ITEM_IMMUNITY, 
    
    CUSTOM, 
    
    ABSORBTION, 
    BLINDNESS, 
    CONFUSION, 
    DAMAGE_RESISTANCE, 
    FAST_DIGGING, 
    FIRE_RESISTANCE, 
    HARM, 
    HEAL, 
    HEALTH_BOOST, 
    HUNGER, 
    INCREASE_DAMAGE, 
    INVISIBILITY, 
    JUMP, 
    NIGHT_VISION, 
    POISON, 
    REGENERATION, 
    SLOW, 
    SLOW_DIGGING, 
    SPEED, 
    WATER_BREATHING, 
    WEAKNESS, 
    WITHER;
  }
  

  protected String _informOn;
  
  protected String _informOff;
  
  protected LivingEntity _ent;
  
  protected LivingEntity _source;
  
  protected ConditionType _type;
  
  protected int _mult;
  
  protected int _ticks;
  
  protected int _ticksTotal;
  
  protected boolean _ambient;
  protected org.bukkit.Material _indicatorType;
  protected byte _indicatorData;
  protected boolean _add = false;
  protected boolean _live = false;
  
  protected boolean _showIndicator = true;
  

  public Condition(ConditionManager manager, String reason, LivingEntity ent, LivingEntity source, ConditionType type, int mult, int ticks, boolean add, org.bukkit.Material visualType, byte visualData, boolean showIndicator, boolean ambient)
  {
    this.Manager = manager;
    this._time = System.currentTimeMillis();
    
    this._reason = reason;
    
    this._ent = ent;
    this._source = source;
    
    this._type = type;
    this._mult = mult;
    this._ticks = ticks;
    this._ticksTotal = ticks;
    this._ambient = ambient;
    
    this._indicatorType = visualType;
    this._indicatorData = visualData;
    this._showIndicator = showIndicator;
    
    this._add = add;
    

    this._live = (!add);
  }
  
  public boolean Tick()
  {
    if ((this._live) && (this._ticks > 0)) {
      this._ticks -= 1;
    }
    return IsExpired();
  }
  


  public void OnConditionAdd() {}
  

  public void Apply()
  {
    this._live = true;
    
    Add();
  }
  
  public void Add()
  {
    try
    {
      org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(this._type.toString());
      

      this._ent.removePotionEffect(type);
      

      if (this._ticks == -1) {
        new org.bukkit.potion.PotionEffect(type, 72000, this._mult, this._ambient).apply(this._ent);
      } else {
        new org.bukkit.potion.PotionEffect(type, this._ticks, this._mult, this._ambient).apply(this._ent);
      }
    }
    catch (Exception localException) {}
  }
  


  public void Remove()
  {
    try
    {
      org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(this._type.toString());
      this._ent.removePotionEffect(type);
    }
    catch (Exception localException) {}
  }
  



  public org.bukkit.Material GetIndicatorMaterial()
  {
    return this._indicatorType;
  }
  
  public byte GetIndicatorData()
  {
    return this._indicatorData;
  }
  
  public LivingEntity GetEnt()
  {
    return this._ent;
  }
  
  public LivingEntity GetSource()
  {
    return this._source;
  }
  
  public boolean IsAdd()
  {
    return this._add;
  }
  
  public ConditionType GetType()
  {
    return this._type;
  }
  
  public int GetMult()
  {
    return this._mult;
  }
  
  public void SetLive(boolean live)
  {
    this._live = live;
  }
  
  public int GetTicks()
  {
    return this._ticks;
  }
  
  public int GetTicksTotal()
  {
    return this._ticksTotal;
  }
  
  public String GetReason()
  {
    return this._reason;
  }
  
  public long GetTime()
  {
    return this._time;
  }
  
  public void Expire()
  {
    this._ticks = 0;
    
    Remove();
  }
  
  public void Restart()
  {
    this._ticks = this._ticksTotal;
  }
  
  public boolean IsBetterOrEqual(Condition other, boolean additive)
  {
    if (GetMult() > other.GetMult()) {
      return true;
    }
    if (GetMult() < other.GetMult()) {
      return false;
    }
    if (additive) {
      return true;
    }
    if (GetTicks() >= other.GetTicks()) {
      return true;
    }
    return false;
  }
  
  public boolean IsVisible()
  {
    return this._showIndicator;
  }
  
  public boolean IsExpired()
  {
    if (this._ticks == -1) {
      return false;
    }
    return this._ticks <= 0;
  }
  
  public ConditionManager GetManager()
  {
    return this.Manager;
  }
  
  public String GetInformOn()
  {
    return this._informOn;
  }
  
  public String GetInformOff()
  {
    return this._informOff;
  }
  
  public void ModifyTicks(int amount)
  {
    this._ticks += amount;
    this._ticksTotal += amount;
  }
  
  public void ModifyMult(int i)
  {
    this._mult = Math.max(0, this._mult + i);
  }
}
