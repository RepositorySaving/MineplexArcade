package mineplex.core.account.repository;

import java.util.UUID;
import mineplex.core.account.repository.token.LoginToken;
import mineplex.core.account.repository.token.RankUpdateToken;
import mineplex.core.common.Rank;
import mineplex.core.common.util.Callback;
import mineplex.core.server.remotecall.AsyncJsonWebCall;
import mineplex.core.server.remotecall.JsonWebCall;


public class AccountRepository
{
  private String _webAddress;
  
  public AccountRepository(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public String GetClient(String name, UUID uuid, String ipAddress)
  {
    LoginToken token = new LoginToken();
    token.Name = name;
    token.Uuid = uuid.toString();
    token.IpAddress = ipAddress;
    
    return new JsonWebCall(this._webAddress + "PlayerAccount/Login").ExecuteReturnStream(token);
  }
  
  public void SaveRank(Callback<Rank> callback, String name, Rank rank, boolean perm)
  {
    RankUpdateToken token = new RankUpdateToken();
    token.Name = name;
    token.Rank = rank.toString();
    token.Perm = perm;
    
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/RankUpdate").Execute(Rank.class, callback, token);
  }
}
