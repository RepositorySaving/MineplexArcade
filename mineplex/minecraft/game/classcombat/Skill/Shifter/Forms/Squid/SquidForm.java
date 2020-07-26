package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid;

import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class SquidForm
  extends FormBase
{
  public SquidForm(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels, EntityType.SQUID, new String[] {"Propel", "Ice Construction" });
    

    SetDesc(
      new String[] {
      ChatColor.WHITE + "Passives:", 
      "* Unlimited Air", 
      "* Protection II", 
      "", 
      "", 
      ChatColor.WHITE + "Attack: " + ChatColor.GREEN + "Suffocate", 
      "* Target has 1.5 second of air removed", 
      "* Target is pushed downward slightly", 
      "", 
      "", 
      ChatColor.WHITE + "Sword Skill: " + ChatColor.GREEN + "Swim", 
      "Push Block to thrust forwards;", 
      "* Velocity of 0.6 + 0.2pL", 
      "", 
      "Hold Block to swim;", 
      "* Velocity of 0.3 + 0.1pL", 
      "", 
      "", 
      ChatColor.WHITE + "Axe Skill: " + ChatColor.GREEN + "Ice Construction", 
      "Right-Click to freeze water", 
      "Left-Click to melt ice" });
  }
  


  @EventHandler
  public void Unmorph(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (IsMorphed(cur))
      {

        if ((UtilEnt.isGrounded(cur)) && (!cur.getLocation().getBlock().isLiquid())) {
          Unmorph(cur);
        }
      }
    }
  }
  
  public void UnapplyMorph(Player player) {
    this.Factory.Condition().EndCondition(player, null, GetName());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Attack(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!IsMorphed(damager)) {
      return;
    }
    event.SetKnockback(false);
    

    event.GetDamageeEntity().setRemainingAir(Math.max(0, event.GetDamageeEntity().getRemainingAir() - 30));
    event.GetDamageeEntity().setVelocity(new Vector(0.0D, -0.5D, 0.0D));
  }
  






  @EventHandler
  public void Update(UpdateEvent paramUpdateEvent)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method Protection(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, Player, Player, double, int, boolean, boolean)\n");
  }
}
