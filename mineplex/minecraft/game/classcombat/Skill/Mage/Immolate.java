package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.PluginManager;

public class Immolate extends Skill
{
  private HashSet<org.bukkit.entity.Entity> _active = new HashSet();
  
  public Immolate(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Toggle.", 
      "", 
      "Ignite yourself in flaming fury.", 
      "You receive Strength #1#1 , Speed 1,", 
      "Fire Resistance and take #0#1 more", 
      "damage from attacks.", 
      "", 
      "You leave a trail of fire, which", 
      "ignites players for #0.25#0.25 seconds." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #15#-1 per Second";
  }
  
  @EventHandler
  public void Toggle(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    if (getLevel(player) == 0) {
      return;
    }
    if (!mineplex.core.common.util.UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
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
      Remove(player);
    }
    else
    {
      if (!this.Factory.Energy().Use(player, "Enable " + GetName(), 10.0D, true, true)) {
        return;
      }
      Add(player);
    }
  }
  
  public void Add(Player player)
  {
    this._active.add(player);
    Conditions();
    UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Enabled", true)));
  }
  
  public void Remove(Player player)
  {
    this._active.remove(player);
    UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Disabled", false)));
    
    this.Factory.Condition().EndCondition(player, null, GetName());
    this.Factory.Condition().Factory().FireResist(GetName(), player, player, 1.9D, 0, false, true, true);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
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
    
    event.AddMod(GetName(), GetName() + " Weakness", level * 1, true);
  }
  
  @EventHandler
  public void Aura(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {


        int level = getLevel(cur);
        if (level == 0)
        {
          Remove(cur);

        }
        else
        {
          SkillTriggerEvent trigger = new SkillTriggerEvent(cur, GetName(), GetClassType());
          UtilServer.getServer().getPluginManager().callEvent(trigger);
          if (trigger.IsCancelled())
          {
            Remove(cur);



          }
          else if (!this.Factory.Energy().Use(cur, GetName(), 0.65D - level * 0.05D, true, true))
          {
            Remove(cur);

          }
          else
          {
            cur.setFireTicks(0); }
        }
      } }
  }
  
  @EventHandler
  public void Combust(EntityCombustEvent event) {
    if (this._active.contains(event.getEntity())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() == UpdateType.FAST) {
      Conditions();
    }
    if (event.getType() == UpdateType.TICK) {
      Flames();
    }
  }
  
  public void Conditions() {
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        int level = getLevel(cur);
        
        this.Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9D, 0, false, true, true);
        this.Factory.Condition().Factory().Strength(GetName(), cur, cur, 1.9D, level, false, true, true);
        this.Factory.Condition().Factory().FireResist(GetName(), cur, cur, 1.9D, 0, false, true, true);
      }
    }
  }
  
  public void Flames() {
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        int level = getLevel(cur);
        

        Item fire = cur.getWorld().dropItem(cur.getLocation().add(0.0D, 0.5D, 0.0D), ItemStackFactory.Instance.CreateStack(org.bukkit.Material.FIRE));
        fire.setVelocity(new org.bukkit.util.Vector((Math.random() - 0.5D) / 3.0D, Math.random() / 3.0D, (Math.random() - 0.5D) / 3.0D));
        this.Factory.Fire().Add(fire, cur, 2.0D, 0.0D, 0.25D + level * 0.25D, 0, GetName());
        

        cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.FIZZ, 0.2F, 1.0F);
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
