package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

public class RecallOld extends Skill
{
  private HashMap<Player, Item> _items = new HashMap();
  private HashMap<Player, Long> _time = new HashMap();
  private HashMap<Player, Long> _informed = new HashMap();
  
  public RecallOld(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Place a recall marker on the ground.", 
      "Use recall again within #15#5 seconds", 
      "to return to original location." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #60#-15";
  }
  

  public String GetRechargeString()
  {
    return "Recharge: #90#-15 Seconds";
  }
  
  @EventHandler
  public void Activate(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    int level = getLevel(player);
    if (level == 0) { return;
    }
    if (!UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    

    if (!this._items.containsKey(player))
    {

      if (!this.Factory.Energy().Use(player, GetName(level), 60 - 15 * level, false, true)) {
        return;
      }
      
      if (!Recharge.Instance.use(player, GetName(), GetName(level), 90000 - (15000 - level), true, false)) {
        return;
      }
      
      this.Factory.Energy().Use(player, GetName(level), 60.0D, true, true);
      

      Item item = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(2261));
      item.setVelocity(new Vector(0, -1, 0));
      

      this._items.put(player, item);
      this._time.put(player, Long.valueOf(System.currentTimeMillis() + 15000L + 5000 * level));
      this._informed.put(player, Long.valueOf(20000L));
      

      UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
      

      player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 133);


    }
    else
    {

      player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 133);
      player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
      UtilParticle.PlayParticle(UtilParticle.ParticleType.WITCH_MAGIC, player.getLocation(), 
        (float)(Math.random() - 0.5D), (float)(Math.random() * 1.4D), (float)(Math.random() - 0.5D), 0.0F, 20);
      

      Item item = (Item)this._items.remove(player);
      this.Factory.Teleport().TP(player, item.getLocation());
      item.remove();
      

      UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
      

      player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
      UtilParticle.PlayParticle(UtilParticle.ParticleType.WITCH_MAGIC, player.getLocation(), 
        (float)(Math.random() - 0.5D), (float)(Math.random() * 1.4D), (float)(Math.random() - 0.5D), 0.0F, 20);
      
      Reset(player);
    }
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Player> expired = new HashSet();
    
    for (Player cur : this._time.keySet())
    {
      if (System.currentTimeMillis() > ((Long)this._time.get(cur)).longValue()) {
        expired.add(cur);
      }
      else {
        long time = ((Long)this._time.get(cur)).longValue() - System.currentTimeMillis();
        if (time < ((Long)this._informed.get(cur)).longValue())
        {
          UtilPlayer.message(cur, F.main(GetClassType().name(), 
            "Recall Time: " + F.time(mineplex.core.common.util.UtilTime.convertString(((Long)this._informed.get(cur)).longValue(), 0, UtilTime.TimeUnit.FIT))));
          this._informed.put(cur, Long.valueOf(((Long)this._informed.get(cur)).longValue() - 5000L));
        }
      }
    }
    

    for (Player cur : expired)
    {
      this._time.remove(cur);
      this._informed.remove(cur);
      
      Item item = (Item)this._items.remove(cur);
      if (item != null)
      {
        item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, 133);
        item.remove();
        

        UtilPlayer.message(cur, F.main(GetClassType().name(), 
          "Recall Time: " + mineplex.core.common.util.C.cRed + "Expired"));
      }
    }
  }
  
  @EventHandler
  public void ItemPickup(PlayerPickupItemEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (this._items.containsValue(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (this._items.containsValue(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  public void Reset(Player player)
  {
    if (this._items.containsKey(player)) {
      ((Item)this._items.get(player)).remove();
    }
    this._items.remove(player);
    this._time.remove(player);
    this._informed.remove(player);
  }
}
