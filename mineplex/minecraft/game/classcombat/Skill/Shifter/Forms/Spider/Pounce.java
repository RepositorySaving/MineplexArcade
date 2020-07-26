package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.movement.ClientMovement;
import mineplex.core.movement.Movement;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;





public class Pounce
  extends SkillActive
{
  public Pounce(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    return true;
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void EndDamager(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    event.SetCancelled(GetName());
    
    Skill(damager, level);
  }
  

  public void Skill(Player player, int level)
  {
    if (player.getLocation().getBlock().isLiquid())
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in liquids."));
      return;
    }
    
    if (UtilTime.elapsed(((ClientMovement)this.Factory.Movement().Get(player)).LastGrounded, 1000L))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while airborne."));
    }
    

    UtilAction.velocity(player, 0.7D + 0.1D * level, 0.2D, 0.8D, true);
    

    player.getWorld().playSound(player.getLocation(), Sound.SPIDER_DEATH, 0.5F, 2.0F);
  }
  
  public void Reset(Player player) {}
}
