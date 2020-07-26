package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;

public class PowerChop extends SkillActive
{
  private WeakHashMap<Player, Long> _charge = new WeakHashMap();
  










  public PowerChop(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Put more strength into your next", 
      "axe attack, causing it to deal", 
      "2 bonus damage.", 
      "", 
      "Attack must be made within", 
      "0.5 seconds of being used." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    if (!Recharge.Instance.use(player, GetName() + " Cooldown", 250L, false, false)) {
      return false;
    }
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._charge.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!UtilGear.isAxe(damager.getItemInHand())) {
      return;
    }
    if (!this._charge.containsKey(damager)) {
      return;
    }
    if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._charge.remove(damager)).longValue(), 500L)) {
      return;
    }
    
    event.AddMod(damager.getName(), GetName(), 2.0D, true);
    

    damager.getWorld().playSound(damager.getLocation(), org.bukkit.Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
  }
  

  public void Reset(Player player)
  {
    this._charge.remove(player);
  }
}
