package nautilus.game.arcade.game.games.christmas.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import nautilus.game.arcade.game.games.christmas.content.CaveGiant;
import nautilus.game.arcade.game.games.christmas.content.Snake;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;



public class Part4
  extends Part
{
  private ArrayList<Location> _roofIce;
  private ArrayList<Location> _mobSpawns;
  private ArrayList<Location> _gate;
  private ArrayList<Snake> _snakes;
  private Location _giantSpawn;
  private CaveGiant _giant;
  private boolean _a = false;
  

  public Part4(Christmas host, Location sleigh, Location[] presents, ArrayList<Location> roofIce, ArrayList<Location> mobs, ArrayList<Location> snakeHead, ArrayList<Location> snakeTrail, ArrayList<Location> gate, Location giant)
  {
    super(host, sleigh, presents);
    
    this._roofIce = roofIce;
    this._mobSpawns = mobs;
    this._giantSpawn = giant;
    this._gate = gate;
    
    for (Location loc : roofIce) {
      loc.getBlock().setType(Material.AIR);
    }
    for (Location loc : gate) {
      loc.getBlock().setType(Material.FENCE);
    }
    
    this._snakes = new ArrayList();
    
    for (Location loc : snakeHead) {
      this._snakes.add(new Snake(loc, snakeTrail));
    }
  }
  


  public void Activate() {}
  

  @EventHandler
  public void GateUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (this._gate.isEmpty()) {
      return;
    }
    int lowest = 255;
    
    for (Location loc : this._gate) {
      if (loc.getBlockY() < lowest)
        lowest = loc.getBlockY();
    }
    Iterator<Location> gateIterator = this._gate.iterator();
    
    boolean sound = true;
    
    while (gateIterator.hasNext())
    {
      Location loc = (Location)gateIterator.next();
      
      if (loc.getBlockY() == lowest)
      {
        loc.getBlock().setType(Material.AIR);
        gateIterator.remove();
        
        if (sound)
        {
          loc.getWorld().playSound(loc, Sound.PISTON_RETRACT, 3.0F, 1.0F);
          sound = false;
        }
      }
    }
  }
  
  @EventHandler
  public void SnakeUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Snake snake : this._snakes) {
      snake.Update();
    }
  }
  
  @EventHandler
  public void IceUpdate(UpdateEvent event) {
    if (event.getType() != UpdateType.FASTER) {}
  }
  









  @EventHandler
  public void IceLand(EntityChangeBlockEvent event)
  {
    if ((event.getEntity() instanceof FallingBlock))
    {
      event.setCancelled(true);
      event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.STEP_SOUND, 79);
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void MonstersUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (HasPresents()) {
      return;
    }
    if (GetCreatures().size() > 40) {
      return;
    }
    
    Location loc = (Location)UtilAlg.Random(this._mobSpawns);
    
    this.Host.CreatureAllowOverride = true;
    Zombie ent = (Zombie)loc.getWorld().spawn(loc, Zombie.class);
    this.Host.CreatureAllowOverride = false;
    
    ent.getEquipment().setItemInHand(new ItemStack(Material.WOOD_PICKAXE));
    ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
    ent.setVillager(true);
    
    ent.setHealth(9.0D);
    
    if (Math.random() > 0.8D) {
      ent.setBaby(true);
    }
    AddCreature(ent);
  }
  
  @EventHandler
  public void GiantUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if ((this._giant == null) && (HasPresents()))
    {
      this._giant = new CaveGiant(this, this._giantSpawn);
      
      this.Host.SantaSay("OH NO! WHAT WAS THAT?");
      SetObjectiveText("Kill the Giant before it reaches Santa", 1.0D);
    }
    else if (this._giant != null)
    {
      this._giant.MoveUpdate();
      SetObjectiveText("Kill the Giant before it reaches Santa", this._giant.GetEntity().getHealth() / this._giant.GetEntity().getMaxHealth());
    }
  }
  
  @EventHandler
  public void UpdateIntro(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!this._a)
    {
      if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 10.0D) {
        return;
      }
      this._a = true;
      
      this.Host.SantaSay("What is that wall of ice?!");
      this.Host.SantaSay("Get those presents while I think of a plan!");
      SetObjectivePresents();
    }
  }
  

  public boolean CanFinish()
  {
    return (this._giant != null) && (this._giant.IsDead());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void GiantKnockback(CustomDamageEvent event)
  {
    if ((event.GetDamageeEntity() instanceof Giant)) {
      event.SetKnockback(false);
    }
  }
}
