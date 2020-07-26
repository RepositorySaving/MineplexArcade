package mineplex.minecraft.game.classcombat.Class.repository;

import java.util.List;
import mineplex.core.server.remotecall.AsyncJsonWebCall;
import mineplex.core.server.remotecall.JsonWebCall;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClassToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;



public class ClassRepository
{
  private String _webAddress;
  
  public ClassRepository(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public List<ClassToken> GetClasses(List<ClassToken> pvpClasses)
  {
    (List)new JsonWebCall(this._webAddress + "Dominate/GetClasses").Execute(new TypeToken() {}.getType(), pvpClasses);
  }
  
  public void SaveCustomBuild(CustomBuildToken token)
  {
    new AsyncJsonWebCall(this._webAddress + "PlayerAccount/SaveCustomBuild").Execute(token);
  }
}
