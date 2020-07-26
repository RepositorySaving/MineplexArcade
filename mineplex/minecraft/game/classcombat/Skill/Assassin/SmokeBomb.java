package mineplex.minecraft.game.classcombat.Skill.Assassin;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SmokeBomb extends Skill
{
  public SmokeBomb(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Use.", 
      "", 
      "Create a smokey explosion, giving", 
      "Blindness to players within #2#1 Blocks", 
      "for #2#2 seconds.", 
      "", 
      "You go invisible for #0#2 seconds." });
  }
  


  public String GetRechargeString()
  {
    return "Recharge: #90#-15 Seconds";
  }
  
  @EventHandler
  public void Use(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    int level = getLevel(player);
    if (level == 0) { return;
    }
    if (!mineplex.core.common.util.UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    

    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    org.bukkit.Bukkit.getServer().getPluginManager().callEvent(trigger);
    
    if (trigger.IsCancelled()) {
      return;
    }
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return;
    }
    
    if (!Recharge.Instance.use(player, GetName(), GetName(level), 90000 - level * 15000, true, false)) {
      return;
    }
    
    this.Factory.Condition().Factory().Cloak(GetName(), player, player, 2 * level, false, true);
    

    UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.HUGE_EXPLOSION, player.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    
    for (Player other : UtilPlayer.getNearby(player.getLocation(), 2 + level))
    {
      this.Factory.Condition().Factory().Blind(GetName(), other, player, 2 * level, 0, false, false, false);
    }
    
    for (int i = 0; i < 3; i++) {
      player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.FIZZ, 2.0F, 0.5F);
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamagee(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (getLevel(damagee) == 0) {
      return;
    }
    
    this.Factory.Condition().EndCondition(damagee, null, GetName());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamager(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (getLevel(damager) == 0) {
      return;
    }
    
    this.Factory.Condition().EndCondition(damager, null, GetName());
  }
  
  @EventHandler
  public void EndInteract(PlayerInteractEvent event)
  {
    if (getLevel(event.getPlayer()) == 0) {
      return;
    }
    this.Factory.Condition().EndCondition(event.getPlayer(), null, GetName());
  }
  
  @EventHandler
  public void Smoke(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      Condition cond = this.Factory.Condition().GetActiveCondition(cur, Condition.ConditionType.CLOAK);
      if (cond != null)
      {
        if (cond.GetReason().equals(GetName()))
        {


          cur.getWorld().playEffect(cur.getLocation(), org.bukkit.Effect.SMOKE, 4);
        }
      }
    }
  }
  
  public void Reset(Player player)
  {
    this.Factory.Condition().EndCondition(player, null, GetName());
  }
}
