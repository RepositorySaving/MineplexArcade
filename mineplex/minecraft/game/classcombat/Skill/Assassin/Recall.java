package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashMap;
import java.util.LinkedList;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class Recall extends Skill
{
  private HashMap<Player, LinkedList<Location>> _locMap = new HashMap();
  
  public Recall(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Use.", 
      "", 
      "Instantly teleport back to where", 
      "you were #2#2 seconds ago." });
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
    if (level == 0) {
      return;
    }
    if (!UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    

    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    org.bukkit.Bukkit.getServer().getPluginManager().callEvent(trigger);
    
    if (trigger.IsCancelled()) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), GetName(level), 90000 - level * 15000, true, false)) {
      return;
    }
    LinkedList<Location> locs = (LinkedList)this._locMap.remove(player);
    if (locs == null) {
      return;
    }
    
    player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
    
    Location current = player.getLocation();
    Location target = (Location)locs.getLast();
    
    this.Factory.Teleport().TP(player, target);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
    
    while (UtilMath.offset(current, target) > 0.5D)
    {
      UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.WITCH_MAGIC, current, 0.0F, 1.0F, 0.0F, 0.0F, 1);
      current = current.add(UtilAlg.getTrajectory(current, target).multiply(0.1D));
    }
  }
  
  @EventHandler
  public void StoreLocation(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (!this._locMap.containsKey(cur)) {
        this._locMap.put(cur, new LinkedList());
      }
      ((LinkedList)this._locMap.get(cur)).addFirst(cur.getLocation());
      
      int level = getLevel(cur);
      if (((LinkedList)this._locMap.get(cur)).size() > (2 + 2 * level) * 20) {
        ((LinkedList)this._locMap.get(cur)).removeLast();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._locMap.remove(player);
  }
}
