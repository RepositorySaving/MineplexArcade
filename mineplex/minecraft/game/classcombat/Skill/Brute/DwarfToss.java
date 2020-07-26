package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class DwarfToss extends SkillActive
{
  private HashSet<Player> _used = new HashSet();
  private NautHashMap<Player, LivingEntity> _holding = new NautHashMap();
  private NautHashMap<Player, Long> _time = new NautHashMap();
  










  public DwarfToss(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Hold Block to pick up target player.", 
      "Release Block to throw with #1.2#0.2 velocity.", 
      "", 
      "Players you are holding cannot harm", 
      "you, or be harmed by others." });
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
    UtilPlayer.message(player, F.main(GetClassType().name(), "You failed " + F.skill(GetName()) + "."));
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
    if (level == 0) { return false;
    }
    
    if ((player.getItemInHand() != null) && 
      (!this._itemSet.contains(player.getItemInHand().getType()))) {
      return false;
    }
    
    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return false;
    }
    
    if (!EnergyRechargeCheck(player, level)) {
      return false;
    }
    
    return true;
  }
  
  @EventHandler
  public void PreventDismount(VehicleExitEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (!(event.getExited() instanceof Player)) {
      return;
    }
    if (!(event.getVehicle() instanceof Player)) {
      return;
    }
    if ((this._holding.containsKey((Player)event.getVehicle())) && (this._holding.get((Player)event.getVehicle()) == event.getExited())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void Grab(PlayerInteractEntityEvent event) {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    this._used.add(player);
    
    if (!CanUse(player)) {
      return;
    }
    if (!(event.getRightClicked() instanceof LivingEntity)) {
      return;
    }
    LivingEntity target = (LivingEntity)event.getRightClicked();
    
    if ((target instanceof Player))
    {
      if (((Player)target).getGameMode() != GameMode.SURVIVAL)
      {
        UtilPlayer.message(player, F.main(GetClassType().name(), F.name(((Player)target).getName()) + " is not attackable."));
        return;
      }
    }
    

    if (UtilMath.offset(player.getLocation(), target.getLocation()) > 4.0D)
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), F.name(UtilEnt.getName(target)) + " is too far away."));
      return;
    }
    

    if (((target instanceof Player)) && (this._holding.containsKey((Player)target)) && 
      (((LivingEntity)this._holding.get((Player)target)).equals(player)) && 
      ((target instanceof Player)))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), F.name(((Player)target).getName()) + " is already holding you."));
      return;
    }
    
    if (this._holding.containsValue(target))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), F.name(UtilEnt.getName(target)) + " is already being held."));
      return;
    }
    

















    target.leaveVehicle();
    player.eject();
    player.setPassenger(target);
    this._holding.put(player, target);
    this._time.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You picked up " + F.name(UtilEnt.getName(target)) + " with " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(target, F.main(GetClassType().name(), F.name(player.getName()) + " grabbed you with " + F.skill(GetName(level)) + "."));
    

    UtilServer.getServer().getPluginManager().callEvent(new mineplex.minecraft.game.classcombat.Skill.event.SkillEvent(player, GetName(), IPvpClass.ClassType.Brute, target));
    

    target.playEffect(EntityEffect.HURT);
  }
  

  @EventHandler(priority=EventPriority.LOWEST)
  public void DamageePassenger(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    if (!this._holding.containsValue(damagee)) {
      return;
    }
    event.SetCancelled(GetName());
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void DamagerPassenger(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    LivingEntity damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this._holding.containsKey(damagee)) {
      return;
    }
    if (!((LivingEntity)this._holding.get(damagee)).equals(damager)) {
      return;
    }
    
    UtilPlayer.message(damager, F.main(GetClassType().name(), "You cannot attack " + F.name(damagee.getName()) + "."));
    
    event.SetCancelled(GetName());
  }
  
  @EventHandler
  public void ThrowExpire(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> voidSet = new HashSet();
    HashSet<Player> throwSet = new HashSet();
    
    for (Player cur : this._holding.keySet())
    {
      if (cur.getPassenger() == null)
      {
        voidSet.add(cur);


      }
      else if (((LivingEntity)this._holding.get(cur)).getVehicle() == null)
      {
        voidSet.add(cur);


      }
      else if (!((LivingEntity)this._holding.get(cur)).getVehicle().equals(cur))
      {
        voidSet.add(cur);



      }
      else if ((!cur.isBlocking()) || (System.currentTimeMillis() - ((Long)this._time.get(cur)).longValue() > 5000L))
      {
        throwSet.add(cur);
      }
    }
    
    for (Player cur : voidSet)
    {
      LivingEntity target = (LivingEntity)this._holding.remove(cur);
      this._time.remove(cur);
      int level = getLevel(cur);
      
      UtilPlayer.message(cur, F.main(GetClassType().name(), F.name(UtilEnt.getName(target)) + " escaped your " + F.skill(GetName(level)) + "."));
    }
    
    for (Player cur : throwSet)
    {
      LivingEntity target = (LivingEntity)this._holding.remove(cur);
      this._time.remove(cur);
      int level = getLevel(cur);
      

      cur.eject();
      double mult = 1.2D + 0.2D * level;
      
      mineplex.core.common.util.UtilAction.velocity(target, cur.getLocation().getDirection(), mult, false, 0.0D, 0.2D, 1.2D, true);
      

      this.Factory.Condition().Factory().Falling(GetName(), target, cur, 10.0D, false, true);
      

      UtilPlayer.message(cur, F.main(GetClassType().name(), "You threw " + F.name(UtilEnt.getName(target)) + " with " + F.skill(GetName(level)) + "."));
      UtilPlayer.message(target, F.main(GetClassType().name(), F.name(cur.getName()) + " threw you with " + F.skill(GetName(level)) + "."));
      

      target.playEffect(EntityEffect.HURT);
    }
  }
  

  public void Reset(Player player)
  {
    player.eject();
    player.leaveVehicle();
    
    for (Player cur : this._holding.keySet())
    {
      if (((LivingEntity)this._holding.get(cur)).equals(player))
      {
        cur.eject();
        this._holding.remove(cur);
        this._time.remove(cur);
      }
    }
    
    this._holding.remove(player);
    this._time.remove(player);
  }
}
