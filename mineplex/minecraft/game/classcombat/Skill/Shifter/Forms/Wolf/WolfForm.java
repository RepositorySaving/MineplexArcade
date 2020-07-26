package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Wolf;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;



public class WolfForm
  extends FormBase
{
  public WolfForm(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels, EntityType.WOLF, new String[0]);
    



    SetDesc(
      new String[] {
      "Dire Wolf Form (Harass / Ganking);", 
      "* Speed I", 
      "* Protection I", 
      "* Regeneration I", 
      "", 
      "", 
      "Axe Skill: Howl", 
      "Nearby allies receive;", 
      "* Speed III for 3 + 1pL seconds", 
      "", 
      "", 
      "Sword Skill: Bite", 
      "Hold Block to bite target;", 
      "* Slow IV for 2 seconds", 
      "* You are pulled along with target" });
  }
  


  public void UnapplyMorph(Player player)
  {
    this.Factory.Condition().EndCondition(player, null, GetName());
  }
  





  @EventHandler
  public void Update(UpdateEvent paramUpdateEvent)
  {
    throw new Error("Unresolved compilation problems: \n\tThe method Protection(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, Player, Player, double, int, boolean, boolean)\n\tThe method Speed(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, Player, Player, double, int, boolean, boolean)\n\tThe method Regen(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, Player, Player, double, int, boolean, boolean)\n");
  }
}
