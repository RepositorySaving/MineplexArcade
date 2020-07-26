package mineplex.minecraft.game.classcombat.Skill.Shifter;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Polysmash extends SkillActive
{
  private HashSet<Player> _used = new HashSet();
  










  public Polysmash(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Turn target enemy into a sheep", 
      "for 6 seconds. While in sheep form,", 
      "players have Slow and Silence." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if (this._used.contains(player)) {
      return false;
    }
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    UtilPlayer.message(player, F.main(GetClassType().name(), "You missed " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Miss(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    this._used.clear();
  }
  
  public boolean CanUse(Player player)
  {
    int level = getLevel(player);
    if (level == 0) {
      return false;
    }
    
    if (!this._itemSet.contains(player.getItemInHand().getType())) {
      return false;
    }
    
    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    mineplex.core.common.util.UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return false;
    }
    
    if (!EnergyRechargeCheck(player, level)) {
      return false;
    }
    
    return true;
  }
  
  @EventHandler
  public void Hit(PlayerInteractEntityEvent event)
  {
    Player player = event.getPlayer();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    if (!CanUse(player)) {
      return;
    }
    if (event.getRightClicked() == null) {
      return;
    }
    if (!(event.getRightClicked() instanceof LivingEntity)) {
      return;
    }
    LivingEntity ent = (LivingEntity)event.getRightClicked();
    
    if (mineplex.core.common.util.UtilMath.offset(player, ent) > 3.0D)
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You missed " + F.skill(GetName()) + "."));
      return;
    }
    

    this._used.add(player);
    

    this.Factory.Condition().Factory().Slow(GetName(), ent, player, 5.0D, 0, false, true, false, false);
    this.Factory.Condition().Factory().Silence(GetName(), ent, player, 5.0D, false, true);
    

    ent.getWorld().playSound(ent.getLocation(), Sound.SHEEP_IDLE, 2.0F, 1.0F);
    ent.getWorld().playSound(ent.getLocation(), Sound.SHEEP_IDLE, 2.0F, 1.0F);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName()) + "."));
    UtilPlayer.message(ent, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
  }
  

  public void Reset(Player player)
  {
    this._used.remove(player);
  }
}
