package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public abstract class FormBase
  extends Skill
{
  private EntityType _type;
  private ArrayList<ISkill> _formSkills = new ArrayList();
  
  private String[] _formSkillNames;
  private HashMap<Player, HashMap<ISkill, Integer>> _savedSkills = new HashMap();
  

  public FormBase(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, EntityType type, String[] formSkillNames)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    this._type = type;
    this._formSkillNames = formSkillNames;
  }
  
  @EventHandler
  public void Use(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    int level = getLevel(player);
    if (level == 0) { return;
    }
    if (!UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    
    if (!IsMorphed(player))
    {
      if ((GetSkillType() == ISkill.SkillType.PassiveA) && 
        (player.isSneaking())) {
        return;
      }
      if ((GetSkillType() == ISkill.SkillType.PassiveB) && 
        (!player.isSneaking())) {
        return;
      }
    }
    event.setCancelled(true);
    
    if (this._savedSkills.containsKey(player)) {
      Unmorph(player);
    } else {
      Morph(player);
    }
  }
  
  public EntityType GetType() {
    return this._type;
  }
  



  public void Morph(Player paramPlayer)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method ClassManager() is undefined for the type SkillFactory\n");
  }
  








  public void Unmorph(Player player)
  {
    for (ISkill skill : GetFormSkills()) {
      skill.Reset(player);
    }
    UnapplyMorph(player);
    RestoreHumanSkills(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You returned to " + F.skill("Human Form")));
  }
  
  public boolean IsMorphed(Player player)
  {
    return this._savedSkills.containsKey(player);
  }
  
  public Collection<Player> GetMorphedUsers()
  {
    return this._savedSkills.keySet();
  }
  
  public ArrayList<ISkill> GetFormSkills()
  {
    if (this._formSkills.isEmpty()) {
      for (String name : this._formSkillNames)
      {
        ISkill skill = this.Factory.GetSkill(name);
        
        if (skill != null) {
          this._formSkills.add(skill);
        }
        else {
          System.out.println("Invalid Skill [" + name + "] for [" + GetName() + "].");
        }
      }
    }
    return this._formSkills;
  }
  
  public abstract void UnapplyMorph(Player paramPlayer);
  
  private void SaveHumanSkills(Player paramPlayer)
  {
    throw new Error("Unresolved compilation problems: \n\tThe method ClassManager() is undefined for the type SkillFactory\n\tType mismatch: cannot convert from element type ISkill to Map.Entry<ISkill,Integer>\n");
  }
  

























  private void RestoreHumanSkills(Player paramPlayer)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method ClassManager() is undefined for the type SkillFactory\n");
  }
  














  public void Reset(Player player)
  {
    this._savedSkills.remove(player);
  }
}
