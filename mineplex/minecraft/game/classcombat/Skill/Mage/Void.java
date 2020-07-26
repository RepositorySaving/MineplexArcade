package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.energy.Energy;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.PluginManager;

public class Void extends Skill
{
  private HashSet<Player> _active = new HashSet();
  
  public Void(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Toggle.", 
      "", 
      "While in void form, you receive", 
      "Slow 3, take no knockback and", 
      "use no energy to swing weapons.", 
      "", 
      "Reduces incoming damage by #1#1 , but", 
      "burns #11#-1 Energy per 1 damage reduced." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 6 per Second";
  }
  
  @EventHandler
  public void Toggle(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    if (!mineplex.core.common.util.UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    if (getLevel(player) == 0) {
      return;
    }
    event.setCancelled(true);
    

    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return;
    }
    if (this._active.contains(player))
    {
      this._active.remove(player);
      UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Disabled", false)));
      

      this.Factory.Condition().EndCondition(event.getPlayer(), null, GetName());
    }
    else
    {
      if (!this.Factory.Energy().Use(player, "Enable " + GetName(), 10.0D, true, true)) {
        return;
      }
      this._active.add(player);
      UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Enabled", true)));
      

      this.Factory.Condition().EndCondition(event.getPlayer(), null, GetName());
    }
  }
  
  @EventHandler
  public void Silence(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {


        SkillTriggerEvent trigger = new SkillTriggerEvent(cur, GetName(), GetClassType());
        UtilServer.getServer().getPluginManager().callEvent(trigger);
        if (trigger.IsCancelled())
          this._active.remove(cur);
      }
    }
  }
  
  @EventHandler
  public void Audio(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : this._active) {
      cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.BLAZE_BREATH, 0.5F, 0.5F);
    }
  }
  
  @EventHandler
  public void Aura(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : this._active)
    {
      if (!cur.isDead())
      {


        this.Factory.Energy().ModifyEnergy(cur, -0.3D);
      }
    }
  }
  
  @EventHandler
  public void Conditions(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : this._active)
    {
      if (!cur.isDead())
      {


        this.Factory.Condition().Factory().Invisible(GetName(), cur, cur, 1.9D, 0, false, true, true);
        this.Factory.Condition().Factory().Slow(GetName(), cur, cur, 1.9D, 1, false, true, false, true);
      }
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Shield(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this._active.contains(damagee)) {
      return;
    }
    int level = getLevel(damagee);
    if (level == 0) { return;
    }
    double dmgToEnergy = 11 - level;
    int dmgLower = 1 + level;
    
    int currentEnergy = (int)this.Factory.Energy().GetCurrent(damagee);
    double requiredEnergy = Math.min(dmgLower, event.GetDamage()) * dmgToEnergy;
    

    if (currentEnergy < requiredEnergy) {
      dmgLower = (int)(currentEnergy / dmgToEnergy);
    }
    
    this.Factory.Energy().ModifyEnergy(damagee, -requiredEnergy);
    

    event.AddMod(damagee.getName(), GetName(), -dmgLower, false);
    event.SetKnockback(false);
    

    damagee.getWorld().playSound(damagee.getLocation(), org.bukkit.Sound.BLAZE_BREATH, 2.0F, 1.0F);
  }
  


  public void Reset(Player player)
  {
    this._active.remove(player);
    

    this.Factory.Condition().EndCondition(player, null, GetName());
  }
}
