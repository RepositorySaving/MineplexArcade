package mineplex.minecraft.game.classcombat.Skill;

import java.util.WeakHashMap;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import org.bukkit.entity.Player;


public class SkillCharge
  extends Skill
{
  protected WeakHashMap<Player, Float> _charge = new WeakHashMap();
  
  protected float _rateBase;
  
  protected float _rateBoost;
  

  public SkillCharge(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel, float base, float boost)
  {
    super(skills, name, classType, skillType, cost, maxLevel);
    
    this._rateBase = base;
    this._rateBoost = boost;
  }
  

  public boolean Charge(Player player)
  {
    int level = getLevel(player);
    if (level == 0) {
      return false;
    }
    
    if (!this._charge.containsKey(player)) {
      this._charge.put(player, Float.valueOf(0.0F));
    }
    float charge = ((Float)this._charge.get(player)).floatValue();
    

    charge = Math.min(1.0F, charge + this._rateBase + this._rateBoost * level);
    this._charge.put(player, Float.valueOf(charge));
    

    DisplayProgress(player, GetName(level), charge);
    
    return charge >= 1.0F;
  }
  
  public float GetCharge(Player player)
  {
    if (!this._charge.containsKey(player)) {
      return 0.0F;
    }
    return ((Float)this._charge.get(player)).floatValue();
  }
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
  }
  
  public String GetChargeString()
  {
    return "Charges #40#10 % per Second.";
  }
}
