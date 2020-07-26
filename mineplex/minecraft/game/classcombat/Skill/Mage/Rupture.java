package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActiveCharge;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

public class Rupture extends SkillActiveCharge
{
  private HashSet<Item> _items = new HashSet();
  
  private WeakHashMap<Player, Location> _target = new WeakHashMap();
  











  public Rupture(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray, 0.01F, 0.005F);
    
    SetDesc(
      new String[] {
      "Hold Block to create a rupture", 
      "at your feet. It will snake through", 
      "the ground with #1#0.1 velocity, moving", 
      "towards where you are looking,", 
      "giving Slow 2 to nearby opponents.", 
      "", 
      GetChargeString(), 
      "", 
      "Release Block to release the rupture,", 
      "causing earth and players to fly upward,", 
      "dealing up to #3#1 inital damage." });
  }
  



  public String GetEnergyString()
  {
    return "Energy: #20#-1 per Second";
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    if (!UtilEnt.isGrounded(player))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " while airborne."));
      return false;
    }
    
    return true;
  }
  

  public void Skill(Player player, int level)
  {
    this._target.put(player, player.getLocation().subtract(0.0D, 1.0D, 0.0D));
    this._charge.put(player, Float.valueOf(0.0F));
  }
  
  public void Clean(Player player)
  {
    this._target.remove(player);
    this._charge.remove(player);
  }
  
  @EventHandler
  public void UpdateSlow(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = this._target.keySet().iterator(); localIterator1.hasNext(); 
        


        localIterator2.hasNext())
    {
      Player cur = (Player)localIterator1.next();
      
      Location loc = (Location)this._target.get(cur);
      
      localIterator2 = cur.getWorld().getPlayers().iterator(); continue;Player other = (Player)localIterator2.next();
      if ((!other.equals(cur)) && 
        (UtilMath.offset(loc, other.getLocation()) < 2.0D) && 
        (this.Factory.Relation().CanHurt(cur, other))) {
        this.Factory.Condition().Factory().Slow(GetName(), other, cur, 1.9D, 1, false, true, false, true);
      }
    }
  }
  
  @EventHandler
  public void UpdateMove(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : mineplex.core.common.util.UtilServer.getPlayers())
    {

      if (this._target.containsKey(cur))
      {


        int level = getLevel(cur);
        if (level == 0) { return;
        }
        
        if (!cur.isBlocking()) {
          DoRupture(cur);



        }
        else if (!this.Factory.Energy().Use(cur, GetName(), 1.0D - 0.05D * level, true, true))
        {
          DoRupture(cur);
        }
        else
        {
          Charge(cur);
          
          MoveRupture(cur);
        }
      }
    }
  }
  
  public void MoveRupture(Player cur) {
    Block targetBlock = cur.getTargetBlock(null, 0);
    if (targetBlock == null) { return;
    }
    
    if (targetBlock.getY() == 0)
    {

      DisplayRupture(cur);
      
      return;
    }
    
    Location target = targetBlock.getLocation().add(0.5D, 0.5D, 0.5D);
    
    Location loc = (Location)this._target.get(cur);
    if (loc == null) { return;
    }
    int level = getLevel(cur);
    loc.add(UtilAlg.getTrajectory(loc, target).normalize().multiply(0.3D + 0.03D * level));
    

    RelocateRupture(cur, loc, target);
    

    DisplayRupture(cur);
  }
  
  public void RelocateRupture(Player cur, Location loc, Location target)
  {
    if (CanTravel(loc.getBlock())) {
      return;
    }
    Location bestLoc = null;
    double bestDist = 9999.0D;
    
    for (Block block : UtilBlock.getInRadius(loc, 1.5D).keySet())
    {
      if (CanTravel(block))
      {

        if (UtilMath.offset(block.getLocation(), target) < bestDist)
        {
          bestLoc = block.getLocation();
          bestDist = UtilMath.offset(block.getLocation(), target);
        }
      }
    }
    if (bestLoc == null)
    {
      UtilPlayer.message(cur, F.main("Skill", "Your " + F.skill(GetName()) + " has failed."));
      Clean(cur);
    }
    else
    {
      this._target.put(cur, bestLoc);
    }
  }
  
  public boolean CanTravel(Block block) {
    int id = block.getTypeId();
    
    return (id == 1) || 
      (id == 2) || 
      (id == 3) || 
      (id == 12) || 
      (id == 13);
  }
  
  public void DisplayRupture(Player cur)
  {
    if (this._target.get(cur) == null) {
      return;
    }
    for (Block block : UtilBlock.getInRadius((Location)this._target.get(cur), 1.0D).keySet()) {
      if ((block.getRelative(BlockFace.UP).getTypeId() == 0) || 
        (block.getRelative(BlockFace.DOWN).getTypeId() == 0) || 
        (block.getRelative(BlockFace.NORTH).getTypeId() == 0) || 
        (block.getRelative(BlockFace.SOUTH).getTypeId() == 0) || 
        (block.getRelative(BlockFace.EAST).getTypeId() == 0) || 
        (block.getRelative(BlockFace.WEST).getTypeId() == 0))
      {
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
      }
    }
  }
  
  public void DoRupture(Player player)
  {
    Location loc = (Location)this._target.get(player);
    float charge = ((Float)this._charge.get(player)).floatValue();
    
    Clean(player);
    
    double range = 2.0F + 2.0F * charge;
    double mult = 0.5D + 0.5D * charge;
    
    int level = getLevel(player);
    

    HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(loc, range);
    for (LivingEntity cur : targets.keySet())
    {

      mineplex.core.common.util.UtilAction.velocity(cur, 
        UtilAlg.getTrajectory2d(loc.toVector().add(new Vector(0.5D, 0.0D, 0.5D)), cur.getLocation().toVector()), 
        0.8D + 0.8D * ((Double)targets.get(cur)).doubleValue() * mult, true, 0.0D, 0.4D + 1.0D * ((Double)targets.get(cur)).doubleValue() * mult, 0.4D + 1.0D * mult, true);
      

      this.Factory.Condition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
      

      if ((cur instanceof Player)) {
        UtilPlayer.message((Player)cur, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
      }
      
      this.Factory.Damage().NewDamageEvent(cur, player, null, 
        org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 1.0F + charge * (3 + level), false, true, false, 
        player.getName(), GetName());
    }
    

    int attempts = 0;
    int done = 0;
    
    Block locBlock = loc.getBlock();
    
    while ((done < charge * 40.0F) && (attempts < charge * 200.0F))
    {
      attempts++;
      
      Vector vec = new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D).normalize();
      Location side = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ());
      
      if (UtilBlock.airFoliage(side.getBlock()))
      {


        vec.add(UtilAlg.getTrajectory(loc.getBlock().getLocation(), side.getBlock().getLocation()));
        

        vec.add(new Vector(0.0D, 1.6D, 0.0D));
        
        vec.normalize();
        

        vec.multiply(0.1D + 0.3D * Math.random() + 0.6D * charge);
        

        Item item = loc.getWorld().dropItem(side, ItemStackFactory.Instance.CreateStack(locBlock.getTypeId(), locBlock.getData()));
        item.setVelocity(vec);
        item.setPickupDelay(50000);
        this._items.add(item);
        

        side.getWorld().playEffect(side, Effect.STEP_SOUND, locBlock.getTypeId());
        
        done++;
      }
    }
  }
  
  @EventHandler
  public void ItemPickup(PlayerPickupItemEvent event) {
    if (this._items.contains(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event) {
    if (this._items.contains(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void ItemDestroy(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._items.isEmpty()) {
      return;
    }
    Iterator<Item> itemIterator = this._items.iterator();
    
    while (itemIterator.hasNext())
    {
      Item item = (Item)itemIterator.next();
      
      if ((item.isDead()) || (!item.isValid()))
      {
        item.remove();
        itemIterator.remove();
      }
      else if ((UtilEnt.isGrounded(item)) || (item.getTicksLived() > 60))
      {
        item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, item.getItemStack().getTypeId());
        item.remove();
        itemIterator.remove();
      }
    }
  }
  

  public void Reset(Player player)
  {
    Clean(player);
  }
}
