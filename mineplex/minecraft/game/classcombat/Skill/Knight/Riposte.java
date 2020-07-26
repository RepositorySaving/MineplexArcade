package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Riposte extends SkillActive
{
  private HashMap<Player, Long> _prepare = new HashMap();
  private HashMap<Player, LivingEntity> _block = new HashMap();
  










  public Riposte(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Block an incoming attack to parry,", 
      "then quickly return the attack ", 
      "to riposte.", 
      "", 
      "If successful, you deal an additional", 
      "#0.5#0.5 bonus damage.", 
      "", 
      "You must block, parry, then riposte", 
      "all within 1 second of each other." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._prepare.put(player, Long.valueOf(System.currentTimeMillis() + 1000L));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared to " + F.skill(GetName(level)) + "."));
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void DoParry(CustomDamageEvent event)
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
    
    if (!damagee.isBlocking()) {
      return;
    }
    if (!this._prepare.containsKey(damagee)) {
      return;
    }
    
    LivingEntity damager = event.GetDamagerEntity(false);
    if (damager == null) { return;
    }
    
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    
    if (!this._block.containsKey(damagee))
    {

      event.SetCancelled(GetName() + " Parry");
      

      this._block.put(damagee, damager);
      this._prepare.put(damagee, Long.valueOf(System.currentTimeMillis() + 1000L));
      

      damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_METAL, 0.5F, 1.6F);
      
      this.Factory.Condition().Factory().Invulnerable(GetName(), damagee, damagee, 0.4D, false, false);
      

      UtilPlayer.message(damagee, F.main(GetClassType().name(), "You parried with " + F.skill(GetName(level)) + "."));
      UtilPlayer.message(event.GetDamagerPlayer(false), F.main(GetClassType().name(), F.name(damagee.getName()) + " parried with " + F.skill(GetName(level)) + "."));
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DoRiposte(CustomDamageEvent event)
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
    if (!this._prepare.containsKey(damager)) {
      return;
    }
    
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    
    if (this._block.containsKey(damager))
    {

      LivingEntity target = (LivingEntity)this._block.remove(damager);
      this._prepare.remove(damager);
      

      event.AddMod(damager.getName(), GetName(), 0.5D + 0.5D * level, true);
      

      damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 1.2F);
      






      UtilPlayer.message(damager, F.main(GetClassType().name(), "You countered with " + F.skill(GetName(level)) + "."));
      UtilPlayer.message(target, F.main(GetClassType().name(), F.name(damager.getName()) + " countered with " + F.skill(GetName(level)) + "."));
    }
  }
  
  @EventHandler
  public void End(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    HashSet<Player> expired = new HashSet();
    
    for (Player cur : this._prepare.keySet()) {
      if (System.currentTimeMillis() > ((Long)this._prepare.get(cur)).longValue())
        expired.add(cur);
    }
    for (Player cur : expired)
    {

      this._prepare.remove(cur);
      this._block.remove(cur);
      

      UtilPlayer.message(cur, F.main(GetClassType().name(), "You failed to " + F.skill(GetName()) + "."));
    }
  }
  

  public void Reset(Player player)
  {
    this._prepare.remove(player);
    this._block.remove(player);
  }
}
