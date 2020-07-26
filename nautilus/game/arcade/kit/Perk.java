package nautilus.game.arcade.kit;

import nautilus.game.arcade.ArcadeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;



public abstract class Perk
  implements Listener
{
  public ArcadeManager Manager;
  public Kit Kit;
  private String _perkName;
  private String[] _perkDesc;
  private boolean _display;
  
  public Perk(String name, String[] perkDesc)
  {
    this._perkName = name;
    this._perkDesc = perkDesc;
    this._display = true;
  }
  
  public Perk(String name, String[] perkDesc, boolean display)
  {
    this._perkName = name;
    this._perkDesc = perkDesc;
    this._display = display;
  }
  
  public void SetHost(Kit kit)
  {
    this.Manager = kit.Manager;
    this.Kit = kit;
  }
  
  public String GetName()
  {
    return this._perkName;
  }
  
  public String[] GetDesc()
  {
    return this._perkDesc;
  }
  
  public boolean IsVisible()
  {
    return this._display;
  }
  
  public void Apply(Player player) {}
}
