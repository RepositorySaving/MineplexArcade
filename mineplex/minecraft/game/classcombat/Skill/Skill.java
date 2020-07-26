package mineplex.minecraft.game.classcombat.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilGear;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public abstract class Skill
  implements ISkill, Listener
{
  private String _name;
  private String[] _desc;
  private HashMap<Integer, String[]> _descLevels = new HashMap();
  
  private IPvpClass.ClassType _classType;
  
  private ISkill.SkillType _skillType;
  private int _salesPackageId;
  private int _gemCost = 1000;
  private int _tokenCost = 0;
  private int _maxLevel = 1;
  
  private boolean _free;
  
  private NautHashMap<Player, Integer> _users;
  public SkillFactory Factory;
  
  public Skill(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel)
  {
    this.Factory = skills;
    this._name = name;
    this._desc = new String[] { "<Skill Description>" };
    this._classType = classType;
    this._skillType = skillType;
    this._users = new NautHashMap();
    this._maxLevel = maxLevel;
    this._tokenCost = cost;
  }
  

  public String GetName()
  {
    return this._name;
  }
  
  public String GetName(int level)
  {
    if (level <= 1) {
      return GetName();
    }
    return this._name + " " + level;
  }
  
  public String GetName(String type)
  {
    return this._name + " (" + type + ")";
  }
  

  public Integer GetSalesPackageId()
  {
    return Integer.valueOf(this._salesPackageId);
  }
  

  public IPvpClass.ClassType GetClassType()
  {
    return this._classType;
  }
  

  public ISkill.SkillType GetSkillType()
  {
    return this._skillType;
  }
  

  public int GetGemCost()
  {
    return this._gemCost;
  }
  

  public int GetTokenCost()
  {
    return this._tokenCost;
  }
  

  public int getMaxLevel()
  {
    return this._maxLevel;
  }
  
  public int getLevel(Entity ent)
  {
    if (!(ent instanceof Player)) {
      return 0;
    }
    Player player = (Player)ent;
    
    if (!this._users.containsKey(player)) {
      return 0;
    }
    int level = ((Integer)this._users.get(player)).intValue();
    
    if ((GetSkillType() == ISkill.SkillType.Sword) && 
      (UtilGear.isMat(player.getItemInHand(), Material.GOLD_SWORD))) {
      level++;
    }
    if ((GetSkillType() == ISkill.SkillType.Axe) && 
      (UtilGear.isMat(player.getItemInHand(), Material.GOLD_AXE))) {
      level++;
    }
    return level;
  }
  


  public String[] GetDesc(int curLevel)
  {
    if (this._descLevels.containsKey(Integer.valueOf(curLevel)))
    {
      return (String[])this._descLevels.get(Integer.valueOf(curLevel));
    }
    



    ArrayList<String> descOut = new ArrayList();
    

    for (String line : this._desc) {
      descOut.add(ModifyLineToLevel(line, curLevel));
    }
    
    if ((GetEnergyString() != null) || (GetRechargeString() != null)) {
      descOut.add("");
    }
    if (GetEnergyString() != null) {
      descOut.add(ModifyLineToLevel(GetEnergyString(), curLevel));
    }
    if (GetRechargeString() != null) {
      descOut.add(ModifyLineToLevel(GetRechargeString(), curLevel));
    }
    
    String[] out = new String[descOut.size()];
    
    for (int i = 0; i < descOut.size(); i++) {
      out[i] = ((String)descOut.get(i));
    }
    
    this._descLevels.put(Integer.valueOf(curLevel), out);
    
    return out;
  }
  
  public String ModifyLineToLevel(String line, int level)
  {
    String newLine = "";
    

    for (String token : line.split(" "))
    {
      if (token.length() > 0)
      {


        if (token.charAt(0) == '#')
        {
          token = token.substring(1, token.length());
          String[] numberToks = token.split("\\#");
          
          try
          {
            float base = Float.parseFloat(numberToks[0]);
            float bonus = Float.parseFloat(numberToks[1]);
            
            float levelValue = base + level * bonus;
            
            String plusMinus = "+";
            if (bonus < 0.0F) {
              plusMinus = "";
            }
            
            String bonusString = bonus;
            if (bonus % 1.0F == 0.0F) {
              bonusString = (int)bonus;
            }
            String totalString = levelValue;
            if (levelValue % 1.0F == 0.0F) {
              totalString = (int)levelValue;
            }
            
            if (level == 0)
            {
              levelValue = base + 1.0F * bonus;
              
              totalString = levelValue;
              if (levelValue % 1.0F == 0.0F) {
                totalString = (int)levelValue;
              }
              token = C.cGreen + totalString + C.cGray;

            }
            else if (level == getMaxLevel())
            {
              token = C.cYellow + totalString + C.cGray;
            }
            else
            {
              token = C.cYellow + totalString + C.cGray + " (" + C.cGreen + plusMinus + bonusString + C.cGray + ")";
            }
          }
          catch (Exception e)
          {
            token = C.cRed + token + C.cGray;
          }
        }
        
        newLine = newLine + token + " ";
      }
    }
    
    if (newLine.length() > 0) {
      newLine = newLine.substring(0, newLine.length() - 1);
    }
    return newLine;
  }
  
  public String GetEnergyString()
  {
    return null;
  }
  
  public String GetRechargeString()
  {
    return null;
  }
  

  public Set<Player> GetUsers()
  {
    this._users.remove(null);
    return this._users.keySet();
  }
  
  public void AddUser(Player player, int level)
  {
    this._users.put(player, Integer.valueOf(level));
    OnPlayerAdd(player);
  }
  


  public void OnPlayerAdd(Player player) {}
  


  public void RemoveUser(Player player)
  {
    this._users.remove(player);
    Reset(player);
  }
  
  public void SetDesc(String[] desc)
  {
    this._desc = desc;
  }
  
  @EventHandler
  public final void Death(PlayerDeathEvent event)
  {
    Reset(event.getEntity());
  }
  
  @EventHandler
  public final void Quit(PlayerQuitEvent event)
  {
    Reset(event.getPlayer());
    this._users.remove(event.getPlayer());
  }
  

  public boolean IsFree()
  {
    return this._free;
  }
  

  public void setFree(boolean free)
  {
    this._free = free;
  }
  
  public void Update(SkillToken skillToken)
  {
    this._salesPackageId = skillToken.SalesPackage.GameSalesPackageId.intValue();
  }
  

  public void DisplayProgress(Player player, String ability, float amount)
  {
    UtilDisplay.displayTextBar(this.Factory.GetPlugin(), player, amount, C.cYellow + C.Bold + ability + ChatColor.RESET + " - " + C.cGreen + C.Bold + (int)(amount * 100.0F) + "%");
    
    if (amount < 1.0F) {
      player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 0.5F + amount * 1.5F);
    }
  }
  
  public void ResetProgress(Player player) {}
}
