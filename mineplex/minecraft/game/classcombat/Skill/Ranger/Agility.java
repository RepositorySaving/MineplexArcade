package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class Agility extends SkillActive
{
  private HashSet<Player> _active = new HashSet();
  










  public Agility(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Sprint with great agility, gaining", 
      "Speed I for #2#2 seconds. You are", 
      "immune to damage while sprinting.", 
      "", 
      "Agility ends if you interact." });
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
    this.Factory.Condition().Factory().Speed(GetName(), player, player, 8.0D, 0, false, true, true);
    this._active.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 0.5F, 0.5F);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void End(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (UtilEvent.isAction(event, UtilEvent.ActionType.R)) {
      return;
    }
    if (event.getAction() == Action.PHYSICAL) {
      return;
    }
    if (!this._active.contains(player)) {
      return;
    }
    
    this._active.remove(player);
    player.removePotionEffect(PotionEffectType.SPEED);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    
    if (damagee == null) {
      return;
    }
    if (!damagee.isSprinting()) {
      return;
    }
    if (!this._active.contains(damagee)) {
      return;
    }
    
    event.SetCancelled(GetName());
    

    UtilPlayer.message(event.GetDamagerPlayer(true), F.main(GetClassType().name(), 
      F.name(damagee.getName()) + " is using " + F.skill(GetName(getLevel(damagee))) + "."));
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.BLAZE_BREATH, 0.5F, 2.0F);
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Player> expired = new HashSet();
    for (Player cur : this._active) {
      if (!cur.hasPotionEffect(PotionEffectType.SPEED))
        expired.add(cur);
    }
    for (Player cur : expired) {
      this._active.remove(cur);
    }
  }
  
  @EventHandler
  public void Particle(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this._active)
    {
      if (player.isSprinting()) {
        mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.SPELL, player.getLocation(), 
          (float)(Math.random() - 0.5D), 0.2F + (float)(Math.random() * 1.0D), (float)(Math.random() - 0.5D), 0.0F, 4);
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
