package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Swim extends Skill
{
  public Swim(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Tap Crouch to Swim forwards." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 5";
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGHEST)
  public void Crouch(PlayerToggleSneakEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    

    if ((player.getLocation().getBlock().getTypeId() != 8) && (player.getLocation().getBlock().getTypeId() != 9)) {
      return;
    }
    
    int level = getLevel(player);
    
    if (level == 0) {
      return;
    }
    
    if (!Recharge.Instance.use(player, GetName(), GetName(level), 800L, false, false)) {
      return;
    }
    if (!this.Factory.Energy().Use(player, GetName(level), 5.0D, true, false)) {
      return;
    }
    
    mineplex.core.common.util.UtilAction.velocity(player, 0.6D, 0.2D, 0.6D, false);
    

    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SPLASH, 0.3F, 2.0F);
  }
  
  public void Reset(Player player) {}
}
