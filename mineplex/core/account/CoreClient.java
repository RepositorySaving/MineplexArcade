package mineplex.core.account;

import mineplex.core.common.Rank;
import org.bukkit.entity.Player;


public class CoreClient
{
  private int _accountId;
  private String _name;
  private Player _player;
  private Rank _rank;
  
  public CoreClient(Player player)
  {
    this._player = player;
    this._name = player.getName();
  }
  
  public CoreClient(String name)
  {
    this._name = name;
  }
  
  public String GetPlayerName()
  {
    return this._name;
  }
  
  public Player GetPlayer()
  {
    return this._player;
  }
  
  public void SetPlayer(Player player)
  {
    this._player = player;
  }
  
  public int GetAccountId()
  {
    return this._accountId;
  }
  
  public void Delete()
  {
    this._name = null;
    this._player = null;
  }
  
  public void SetAccountId(int accountId)
  {
    this._accountId = accountId;
  }
  
  public Rank GetRank()
  {
    return this._rank;
  }
  
  public void SetRank(Rank rank)
  {
    this._rank = rank;
  }
}
