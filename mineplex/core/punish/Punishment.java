package mineplex.core.punish;


public class Punishment
{
  private int _id;
  
  private PunishmentSentence _punishmentType;
  private Category _category;
  private String _reason;
  private String _admin;
  private double _hours;
  private int _severity;
  private long _time;
  private boolean _active;
  private boolean _removed;
  private String _removeAdmin;
  private String _removeReason;
  
  public Punishment(int id, PunishmentSentence punishmentType, Category category, String reason, String admin, double hours, int severity, long time, boolean active, boolean removed, String removeAdmin, String removeReason)
  {
    this._id = id;
    this._punishmentType = punishmentType;
    this._category = category;
    this._reason = reason;
    this._admin = admin;
    this._hours = hours;
    this._severity = severity;
    this._time = time;
    this._active = active;
    this._removed = removed;
    this._removeAdmin = removeAdmin;
    this._removeReason = removeReason;
  }
  
  public int GetPunishmentId()
  {
    return this._id;
  }
  
  public PunishmentSentence GetPunishmentType()
  {
    return this._punishmentType;
  }
  
  public Category GetCategory()
  {
    return this._category;
  }
  
  public String GetReason()
  {
    return this._reason;
  }
  
  public String GetAdmin()
  {
    return this._admin;
  }
  
  public double GetHours()
  {
    return this._hours;
  }
  
  public int GetSeverity()
  {
    return this._severity;
  }
  
  public long GetTime()
  {
    return this._time;
  }
  
  public boolean GetActive()
  {
    return this._active;
  }
  
  public boolean GetRemoved()
  {
    return this._removed;
  }
  
  public void Remove(String admin, String reason)
  {
    this._removed = true;
    this._removeAdmin = admin;
    this._removeReason = reason;
  }
  
  public String GetRemoveReason()
  {
    return this._removeReason;
  }
  
  public boolean IsBanned()
  {
    return (this._punishmentType == PunishmentSentence.Ban) && ((GetRemaining() > 0L) || (this._hours < 0.0D)) && (this._active);
  }
  
  public boolean IsMuted()
  {
    return (this._punishmentType == PunishmentSentence.Mute) && ((GetRemaining() > 0L) || (this._hours < 0.0D)) && (this._active);
  }
  
  public long GetRemaining()
  {
    return this._hours < 0.0D ? -1L : (this._time + 3600000.0D * this._hours - System.currentTimeMillis());
  }
  
  public String GetRemoveAdmin()
  {
    return this._removeAdmin;
  }
}
