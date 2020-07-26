package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;






public class SpiderForm
  extends FormBase
{
  public SpiderForm(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels, EntityType.SPIDER, new String[] {"Venomous Spines", "Spin Web", "Pounce" });
    

    SetDesc(
      new String[] {
      ChatColor.WHITE + "Passives:", 
      "* Slow II", 
      "", 
      "", 
      ChatColor.WHITE + "Attack: " + ChatColor.GREEN + "Pounce", 
      "Pounce with 0.7 + 0.1pL Velocity", 
      "", 
      "", 
      ChatColor.WHITE + "Sword Skill: " + ChatColor.GREEN + "Needler", 
      "Spit out a flurry of needles;", 
      "* Capacity of 3 + 1pL", 
      "", 
      "", 
      ChatColor.WHITE + "Axe Skill: " + ChatColor.GREEN + "Spin Web", 
      "Spin a temporary web;", 
      "* Lasts 5 + 1pL seconds" });
  }
  


  public void UnapplyMorph(Player player)
  {
    this.Factory.Condition().EndCondition(player, null, GetName());
  }
  





  @EventHandler
  public void Update(UpdateEvent paramUpdateEvent)
  {
    throw new Error("Unresolved compilation problems: \n\tThe method Protection(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, Player, Player, double, int, boolean, boolean)\n\tThe method Slow(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, Player, Player, double, int, boolean, boolean, boolean)\n");
  }
}
