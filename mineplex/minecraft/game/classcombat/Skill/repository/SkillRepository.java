package mineplex.minecraft.game.classcombat.Skill.repository;

import java.util.List;
import mineplex.core.server.remotecall.JsonWebCall;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;



public class SkillRepository
{
  private String _webAddress;
  
  public SkillRepository(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public List<SkillToken> GetSkills(List<SkillToken> skills)
  {
    (List)new JsonWebCall(this._webAddress + "Dominate/GetSkills").Execute(new TypeToken() {}.getType(), skills);
  }
}
