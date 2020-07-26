package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;





public class GolemForm
  extends FormBase
{
  public GolemForm(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels, EntityType.IRON_GOLEM, new String[] {"Magnetic Pull", "Magnetic Repel" });
    

    SetDesc(
      new String[] {
      ChatColor.WHITE + "Passives:", 
      "* Slow II", 
      "* Protection III", 
      "* -4 Damage Dealt", 
      "", 
      ChatColor.WHITE + "Sword Attack: " + ChatColor.GREEN + "Iron Crush", 
      "* No Knockback", 
      "* Slow V for 0.5 seconds", 
      "", 
      "", 
      ChatColor.WHITE + "Axe Attack: " + ChatColor.GREEN + "Iron Smash", 
      "* Strong Knockback", 
      "", 
      "", 
      ChatColor.WHITE + "Sword Skill: " + ChatColor.GREEN + "Magnetic Pull", 
      "Pull in enemies infront of you;", 
      "* Range of 4 + 2pL", 
      "* Radius of 2 + 0.5pL", 
      "", 
      "", 
      ChatColor.WHITE + "Axe Skill: " + ChatColor.GREEN + "Magnetic Repel", 
      "Repel all nearby enemies;", 
      "* Range of 4 + 2pL", 
      "* Velocity of 1.2 + 0.2pL" });
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
  


  @EventHandler(priority=EventPriority.HIGH)
  public void KnockbackTaken(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!IsMorphed(damagee)) {
      return;
    }
    event.SetKnockback(false);
    damagee.getWorld().playSound(damagee.getLocation(), Sound.ZOMBIE_METAL, 0.8F, 1.8F);
  }
  

















  @EventHandler(priority=EventPriority.HIGH)
  public void KnockbackGiven(CustomDamageEvent paramCustomDamageEvent)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method Slow(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, LivingEntity, Player, double, int, boolean, boolean, boolean)\n");
  }
}
