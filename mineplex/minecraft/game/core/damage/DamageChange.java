package mineplex.minecraft.game.core.damage;

public class DamageChange
{
  private String _source;
  private String _reason;
  private double _modifier;
  private boolean _useReason;
  
  public DamageChange(String source, String reason, double modifier, boolean useReason)
  {
    this._source = source;
    this._reason = reason;
    this._modifier = modifier;
    this._useReason = useReason;
  }
  
  public String GetSource()
  {
    return this._source;
  }
  
  public String GetReason()
  {
    return this._reason;
  }
  
  public double GetDamage()
  {
    return this._modifier;
  }
  
  public boolean UseReason()
  {
    return this._useReason;
  }
}
