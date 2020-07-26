package mineplex.minecraft.game.core.condition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ConditionManager extends MiniPlugin
{
  private ConditionFactory _factory;
  private ConditionApplicator _applicator;
  protected ConditionEffect Effect;
  private WeakHashMap<LivingEntity, LinkedList<Condition>> _conditions = new WeakHashMap();
  private WeakHashMap<LivingEntity, LinkedList<ConditionActive>> _activeConditions = new WeakHashMap();
  
  private HashSet<Entity> _items = new HashSet();
  
  public ConditionManager(JavaPlugin plugin)
  {
    super("Condition Manager", plugin);
    
    Factory();
    Applicator();
    Effect();
  }
  
  public ConditionFactory Factory()
  {
    if (this._factory == null) {
      this._factory = new ConditionFactory(this);
    }
    return this._factory;
  }
  
  public ConditionApplicator Applicator()
  {
    if (this._applicator == null) {
      this._applicator = new ConditionApplicator();
    }
    return this._applicator;
  }
  
  public ConditionEffect Effect()
  {
    if (this.Effect == null) {
      this.Effect = new ConditionEffect(this);
    }
    return this.Effect;
  }
  

  public Condition AddCondition(Condition newCon)
  {
    ConditionApplyEvent condEvent = new ConditionApplyEvent(newCon);
    GetPlugin().getServer().getPluginManager().callEvent(condEvent);
    
    if (condEvent.isCancelled()) {
      return null;
    }
    
    if (!this._conditions.containsKey(newCon.GetEnt())) {
      this._conditions.put(newCon.GetEnt(), new LinkedList());
    }
    ((LinkedList)this._conditions.get(newCon.GetEnt())).add(newCon);
    

    newCon.OnConditionAdd();
    

    HandleIndicator(newCon);
    
    return newCon;
  }
  
  public void HandleIndicator(Condition newCon)
  {
    ConditionActive ind = GetIndicatorType(newCon);
    

    if (ind == null)
    {
      AddIndicator(newCon);

    }
    else
    {
      UpdateActive(ind, newCon);
    }
  }
  
  public ConditionActive GetIndicatorType(Condition newCon)
  {
    if (!this._activeConditions.containsKey(newCon.GetEnt())) {
      this._activeConditions.put(newCon.GetEnt(), new LinkedList());
    }
    for (ConditionActive ind : (LinkedList)this._activeConditions.get(newCon.GetEnt())) {
      if (ind.GetCondition().GetType() == newCon.GetType())
        return ind;
    }
    return null;
  }
  

  public void AddIndicator(Condition newCon)
  {
    ConditionActive newInd = new ConditionActive(newCon);
    

    if (!this._activeConditions.containsKey(newCon.GetEnt())) {
      this._activeConditions.put(newCon.GetEnt(), new LinkedList());
    }
    LinkedList<ConditionActive> entInds = (LinkedList)this._activeConditions.get(newCon.GetEnt());
    

    entInds.addFirst(newInd);
    

    if (newCon.GetInformOn() != null) {
      UtilPlayer.message(newCon.GetEnt(), F.main("Condition", newCon.GetInformOn()));
    }
  }
  

  public void UpdateActive(ConditionActive active, Condition newCon)
  {
    if ((!active.GetCondition().IsExpired()) && 
      (active.GetCondition().IsBetterOrEqual(newCon, newCon.IsAdd()))) {
      return;
    }
    active.SetCondition(newCon);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void ExpireConditions(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Condition> conditionIterator;
    for (Iterator localIterator = this._conditions.keySet().iterator(); localIterator.hasNext(); 
        


        conditionIterator.hasNext())
    {
      LivingEntity ent = (LivingEntity)localIterator.next();
      
      conditionIterator = ((LinkedList)this._conditions.get(ent)).iterator();
      
      continue;
      
      Condition cond = (Condition)conditionIterator.next();
      
      if (cond.Tick()) {
        conditionIterator.remove();
      }
    }
    
    Iterator<ConditionActive> conditionIndicatorIterator;
    for (localIterator = this._activeConditions.keySet().iterator(); localIterator.hasNext(); 
        


        conditionIndicatorIterator.hasNext())
    {
      LivingEntity ent = (LivingEntity)localIterator.next();
      
      conditionIndicatorIterator = ((LinkedList)this._activeConditions.get(ent)).iterator();
      
      continue;
      
      ConditionActive conditionIndicator = (ConditionActive)conditionIndicatorIterator.next();
      
      if (conditionIndicator.GetCondition().IsExpired())
      {
        Condition replacement = GetBestCondition(ent, conditionIndicator.GetCondition().GetType());
        
        if (replacement == null)
        {
          conditionIndicatorIterator.remove();
          

          if (conditionIndicator.GetCondition().GetInformOff() != null) {
            UtilPlayer.message(conditionIndicator.GetCondition().GetEnt(), F.main("Condition", conditionIndicator.GetCondition().GetInformOff()));
          }
        } else {
          UpdateActive(conditionIndicator, replacement);
        }
      }
    }
  }
  
  public Condition GetBestCondition(LivingEntity ent, Condition.ConditionType type)
  {
    if (!this._conditions.containsKey(ent)) {
      return null;
    }
    Condition best = null;
    
    for (Condition con : (LinkedList)this._conditions.get(ent))
    {
      if (con.GetType() == type)
      {

        if (!con.IsExpired())
        {

          if (best == null)
          {
            best = con;


          }
          else if (con.IsBetterOrEqual(best, false))
            best = con; }
      }
    }
    return best;
  }
  
  public Condition GetActiveCondition(LivingEntity ent, Condition.ConditionType type)
  {
    if (!this._activeConditions.containsKey(ent)) {
      return null;
    }
    for (ConditionActive ind : (LinkedList)this._activeConditions.get(ent))
    {
      if (ind.GetCondition().GetType() == type)
      {

        if (!ind.GetCondition().IsExpired())
        {

          return ind.GetCondition(); }
      }
    }
    return null;
  }
  
  @EventHandler
  public void Remove(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Entity> expired = new HashSet();
    
    for (Entity cur : this._items) {
      if ((UtilEnt.isGrounded(cur)) || (cur.isDead()) || (!cur.isValid()))
        expired.add(cur);
    }
    for (Entity cur : expired)
    {
      this._items.remove(cur);
      cur.remove();
    }
  }
  
  @EventHandler
  public void Respawn(PlayerRespawnEvent event)
  {
    Clean(event.getPlayer());
  }
  
  @EventHandler
  public void Quit(PlayerQuitEvent event)
  {
    Clean(event.getPlayer());
  }
  

  @EventHandler(priority=EventPriority.MONITOR)
  public void Death(EntityDeathEvent event)
  {
    if (((event.getEntity() instanceof Player)) && 
      (event.getEntity().getHealth() > 0.0D)) {
      return;
    }
    Clean(event.getEntity());
  }
  

  public void Clean(LivingEntity ent)
  {
    this._conditions.remove(ent);
    this._activeConditions.remove(ent);
  }
  
  @EventHandler
  public void Debug(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (LivingEntity ent : this._activeConditions.keySet())
    {
      if ((ent instanceof Player))
      {

        Player player = (Player)ent;
        if (player.getItemInHand() != null)
        {

          if (player.getItemInHand().getType() == Material.PAPER)
          {

            if (player.isOp())
            {

              UtilPlayer.message(player, C.cGray + ((LinkedList)this._activeConditions.get(ent)).size() + " Indicators ----------- " + ((LinkedList)this._conditions.get(ent)).size() + " Conditions");
              for (ConditionActive ind : (LinkedList)this._activeConditions.get(ent))
                UtilPlayer.message(player, 
                  F.elem(new StringBuilder().append(ind.GetCondition().GetType()).append(" ").append(ind.GetCondition().GetMult() + 1).toString()) + " for " + 
                  F.time(UtilTime.convertString(ind.GetCondition().GetTicks() * 50L, 1, UtilTime.TimeUnit.FIT)) + " via " + 
                  F.skill(ind.GetCondition().GetReason()) + " from " + 
                  F.name(UtilEnt.getName(ind.GetCondition().GetSource())) + ".");
            } } }
      } }
  }
  
  @EventHandler
  public void Pickup(PlayerPickupItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (this._items.contains(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (this._items.contains(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  public void EndCondition(LivingEntity target, Condition.ConditionType type, String reason) {
    if (!this._conditions.containsKey(target)) {
      return;
    }
    for (Condition cond : (LinkedList)this._conditions.get(target)) {
      if (((reason == null) || (cond.GetReason().equals(reason))) && (
        (type == null) || (cond.GetType() == type)))
      {
        cond.Expire();
        
        Condition best = GetBestCondition(target, cond.GetType());
        if (best != null) best.Apply();
      }
    }
  }
  
  public boolean HasCondition(LivingEntity target, Condition.ConditionType type, String reason) {
    if (!this._conditions.containsKey(target)) {
      return false;
    }
    for (Condition cond : (LinkedList)this._conditions.get(target)) {
      if (((reason == null) || (cond.GetReason().equals(reason))) && (
        (type == null) || (cond.GetType() == type)))
        return true;
    }
    return false;
  }
  
  public WeakHashMap<LivingEntity, LinkedList<ConditionActive>> GetActiveConditions()
  {
    return this._activeConditions;
  }
  
  public boolean IsSilenced(LivingEntity ent, String ability)
  {
    if (!this._activeConditions.containsKey(ent)) {
      return false;
    }
    for (ConditionActive ind : (LinkedList)this._activeConditions.get(ent)) {
      if (ind.GetCondition().GetType() == Condition.ConditionType.SILENCE)
      {
        if (ability != null)
        {
          if ((ent instanceof Player))
          {
            if (Recharge.Instance.use((Player)ent, "Silence Feedback", 200L, false, false))
            {

              UtilPlayer.message(ent, F.main("Condition", "Cannot use " + F.skill(ability) + " while silenced."));
              

              ((Player)ent).playSound(ent.getLocation(), Sound.BAT_HURT, 0.8F, 0.8F);
            }
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean IsInvulnerable(LivingEntity ent)
  {
    if (!this._activeConditions.containsKey(ent)) {
      return false;
    }
    for (ConditionActive ind : (LinkedList)this._activeConditions.get(ent)) {
      if (ind.GetCondition().GetType() == Condition.ConditionType.INVULNERABLE)
        return true;
    }
    return false;
  }
  
  public boolean IsCloaked(LivingEntity ent)
  {
    if (!this._activeConditions.containsKey(ent)) {
      return false;
    }
    for (ConditionActive ind : (LinkedList)this._activeConditions.get(ent)) {
      if (ind.GetCondition().GetType() == Condition.ConditionType.CLOAK)
        return true;
    }
    return false;
  }
  
  @EventHandler
  public void CleanUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    Iterator<Map.Entry<LivingEntity, LinkedList<ConditionActive>>> conditionIndIterator = this._activeConditions.entrySet().iterator();
    
    while (conditionIndIterator.hasNext())
    {
      Map.Entry<LivingEntity, LinkedList<ConditionActive>> entry = (Map.Entry)conditionIndIterator.next();
      LivingEntity ent = (LivingEntity)entry.getKey();
      
      if ((ent.isDead()) || (!ent.isValid()) || (((ent instanceof Player)) && (!((Player)ent).isOnline())))
      {
        ent.remove();
        conditionIndIterator.remove();
      }
    }
    
    Iterator<Map.Entry<LivingEntity, LinkedList<Condition>>> conditionIterator = this._conditions.entrySet().iterator();
    
    while (conditionIterator.hasNext())
    {
      Map.Entry<LivingEntity, LinkedList<Condition>> entry = (Map.Entry)conditionIterator.next();
      LivingEntity ent = (LivingEntity)entry.getKey();
      
      if ((ent.isDead()) || (!ent.isValid()) || (((ent instanceof Player)) && (!((Player)ent).isOnline())))
      {
        ent.remove();
        conditionIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void Debug(PlayerCommandPreprocessEvent event)
  {
    if (event.getPlayer().getName().equals("Chiss"))
    {
      if (event.getMessage().equals("/debugcond1"))
      {
        this._factory.Regen("Debug", event.getPlayer(), event.getPlayer(), 30.0D, 0, false, false, false);
        event.getPlayer().sendMessage("Regen 1 for 30s");
        event.setCancelled(true);
      }
      else if (event.getMessage().equals("/debugcond2"))
      {
        this._factory.Regen("Debug", event.getPlayer(), event.getPlayer(), 15.0D, 1, false, false, false);
        event.getPlayer().sendMessage("Regen 2 for 15s");
        event.setCancelled(true);
      }
      else if (event.getMessage().equals("/debugcond3"))
      {
        this._factory.Regen("Debug", event.getPlayer(), event.getPlayer(), 5.0D, 2, false, false, false);
        event.getPlayer().sendMessage("Regen 3 for 5s");
        event.setCancelled(true);
      }
      else if (event.getMessage().equals("/debugcond4"))
      {
        this._factory.Slow("Debug", event.getPlayer(), event.getPlayer(), 5.0D, 0, true, false, false, false);
        event.setCancelled(true);
      }
      else if (event.getMessage().equals("/debugcond5"))
      {
        this._factory.Ignite("Debug", event.getPlayer(), event.getPlayer(), 5.0D, true, false);
        event.getPlayer().sendMessage("Regen 1 for 30s");
        event.setCancelled(true);
      }
    }
  }
}
