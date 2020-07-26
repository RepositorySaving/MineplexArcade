package mineplex.minecraft.game.core.fire;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class Fire extends MiniPlugin
{
  private ConditionManager _conditionManager;
  private DamageManager _damageManager;
  private HashMap<Item, FireData> _fire = new HashMap();
  
  public Fire(JavaPlugin plugin, ConditionManager conditionManager, DamageManager damageManager)
  {
    super("Fire", plugin);
    
    this._conditionManager = conditionManager;
    this._damageManager = damageManager;
  }
  
  public void Add(Item item, LivingEntity owner, double expireTime, double delayTime, double burnTime, int damage, String skillName)
  {
    this._fire.put(item, new FireData(owner, expireTime, delayTime, burnTime, damage, skillName));
    item.setPickupDelay(0);
  }
  
  @EventHandler
  public void IgniteCollide(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashMap<Item, LivingEntity> collided = new HashMap();
    
    for (Item fire : this._fire.keySet())
    {
      if (((FireData)this._fire.get(fire)).IsPrimed())
      {

        if (fire.getLocation().getBlock().isLiquid())
        {
          collided.put(fire, null);
        }
        else
        {
          for (LivingEntity ent : fire.getWorld().getEntitiesByClass(LivingEntity.class))
          {
            if ((!(ent instanceof Player)) || 
              (!((CraftPlayer)ent).getHandle().spectating))
            {

              if (!ent.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
              {

                if ((ent.getLocation().getBlock().getTypeId() != 8) && (ent.getLocation().getBlock().getTypeId() != 9))
                {

                  if (!ent.equals(((FireData)this._fire.get(fire)).GetOwner()))
                  {

                    if (!this._conditionManager.HasCondition(ent, Condition.ConditionType.FIRE_ITEM_IMMUNITY, null))
                    {



                      if (UtilEnt.hitBox(fire.getLocation(), ent, 1.5D, null))
                      {

                        collided.put(fire, ent); } } } } } } }
        }
      }
    }
    for (Item fire : collided.keySet())
    {
      FireData fireData = (FireData)this._fire.remove(fire);
      fire.remove();
      Ignite((LivingEntity)collided.get(fire), fireData);
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.LOW)
  public void IgnitePickup(PlayerPickupItemEvent event)
  {
    Player player = event.getPlayer();
    Item fire = event.getItem();
    
    if (!this._fire.containsKey(fire)) {
      return;
    }
    event.setCancelled(true);
    
    if (((CraftPlayer)player).getHandle().spectating) {
      return;
    }
    if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
      return;
    }
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9)) {
      return;
    }
    if (!((FireData)this._fire.get(fire)).IsPrimed()) {
      return;
    }
    if (this._conditionManager.HasCondition(player, Condition.ConditionType.FIRE_ITEM_IMMUNITY, null))
    {
      return;
    }
    
    if (!UtilEnt.hitBox(fire.getLocation(), player, 1.5D, null)) {
      return;
    }
    
    FireData fireData = (FireData)this._fire.remove(fire);
    fire.remove();
    Ignite(player, fireData);
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event)
  {
    if (this._fire.containsKey(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  public void Ignite(LivingEntity ent, FireData fireData) {
    if (ent == null) {
      return;
    }
    this._conditionManager.Factory().Ignite(fireData.GetName(), ent, fireData.GetOwner(), fireData.GetBurnTime(), true, true);
    

    if (fireData.GetDamage() > 0)
    {

      if ((fireData.GetDamage() == 1) && 
        ((ent instanceof Player)) && 
        (!Recharge.Instance.use((Player)ent, "Fire Damage", 150L, false, false)))
      {
        ent.playEffect(EntityEffect.HURT);
        return;
      }
      


      this._damageManager.NewDamageEvent(ent, fireData.GetOwner(), null, 
        EntityDamageEvent.DamageCause.CUSTOM, fireData.GetDamage(), false, true, false, 
        UtilEnt.getName(fireData.GetOwner()), fireData.GetName());
    }
  }
  


  @EventHandler
  public void Expire(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Item> expire = new HashSet();
    
    for (Item cur : this._fire.keySet())
    {
      if ((!cur.isValid()) || (((FireData)this._fire.get(cur)).Expired()))
        expire.add(cur);
    }
    for (Item cur : expire)
    {
      this._fire.remove(cur);
      cur.remove();
    }
  }
  
  public void Remove(LivingEntity owner, String cause)
  {
    HashSet<Item> remove = new HashSet();
    
    for (Item cur : this._fire.keySet()) {
      if (((owner == null) || (((FireData)this._fire.get(cur)).GetOwner().equals(owner))) && (
        (cause == null) || (((FireData)this._fire.get(cur)).GetName().equals(cause))))
        remove.add(cur);
    }
    for (Item cur : remove)
    {
      this._fire.remove(cur);
      cur.remove();
    }
  }
  
  public void RemoveNear(Location loc, double range)
  {
    HashSet<Item> remove = new HashSet();
    
    for (Item cur : this._fire.keySet()) {
      if (UtilMath.offset(loc, cur.getLocation()) < range)
        remove.add(cur);
    }
    for (Item cur : remove)
    {
      this._fire.remove(cur);
      
      cur.getWorld().playEffect(cur.getLocation(), Effect.EXTINGUISH, 0);
      
      cur.remove();
    }
  }
}
