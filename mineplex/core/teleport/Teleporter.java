package mineplex.core.teleport;

import mineplex.core.common.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;




public class Teleporter
{
  private Player _pA;
  private Location _loc;
  private String _mA;
  private Player _pB;
  private String _mB;
  private Teleport _tp;
  
  public Teleporter(Teleport teleport, Player pA, Player pB, String mA, String mB, Location loc, boolean record, String log)
  {
    this._tp = teleport;
    this._pA = pA;
    this._pB = pB;
    this._mA = mA;
    this._mB = mB;
    this._loc = loc;
  }
  
  public void doTeleport()
  {
    if (this._loc == null) {
      return;
    }
    












    if (this._pA != null)
    {


      this._tp.TP(this._pA, this._loc);
      

      if (this._mA != null) {
        UtilPlayer.message(this._pA, this._mA);
      }
    }
    
    if ((this._pB != null) && (this._mB != null)) {
      UtilPlayer.message(this._pB, this._mB);
    }
  }
}
