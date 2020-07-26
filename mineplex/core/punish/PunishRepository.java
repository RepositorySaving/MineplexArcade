package mineplex.core.punish;

import java.util.List;
import mineplex.core.common.util.Callback;
import mineplex.core.punish.Tokens.PunishClientToken;
import mineplex.core.punish.Tokens.PunishToken;
import mineplex.core.punish.Tokens.RemovePunishToken;
import mineplex.core.server.remotecall.AsyncJsonWebCall;
import mineplex.core.server.remotecall.JsonWebCall;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;



public class PunishRepository
{
  private String _webAddress;
  
  public PunishRepository(String webServerAddress)
  {
    this._webAddress = webServerAddress;
  }
  
  public void Punish(Callback<String> callback, String target, String category, PunishmentSentence punishment, String reason, double duration, String admin, int severity, long time)
  {
    PunishToken token = new PunishToken();
    token.Target = target;
    token.Category = category;
    token.Sentence = punishment.toString();
    token.Reason = reason;
    token.Duration = duration;
    token.Admin = admin;
    token.Time = time;
    token.Severity = severity;
    
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/Punish").Execute(String.class, callback, token);
  }
  
  public void RemovePunishment(Callback<String> callback, int id, String target, String reason, String admin)
  {
    RemovePunishToken token = new RemovePunishToken();
    token.PunishmentId = id;
    token.Target = target;
    token.Reason = reason;
    token.Admin = admin;
    
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/RemovePunishment").Execute(String.class, callback, token);
  }
  
  public void LoadPunishClient(String target, Callback<PunishClientToken> callback)
  {
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/GetPunishClient").Execute(PunishClientToken.class, callback, target);
  }
  
  public void MatchPlayerName(final Callback<List<String>> callback, final String userName)
  {
    Thread asyncThread = new Thread(new Runnable()
    {
      public void run()
      {
        List<String> tokenList = (List)new JsonWebCall(PunishRepository.this._webAddress + "PlayerAccount/GetMatches").Execute(new TypeToken() {}.getType(), userName);
        callback.run(tokenList);
      }
      
    });
    asyncThread.start();
  }
  
  public void RemoveBan(String name, String reason)
  {
    RemovePunishToken token = new RemovePunishToken();
    token.Target = name;
    token.Reason = reason;
    
    new JsonWebCall(this._webAddress + "PlayerAccount/RemoveBan").Execute(String.class, token);
  }
}
