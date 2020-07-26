package nautilus.game.arcade.game.games.christmas.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;









public class Part1
  extends Part
{
  private ArrayList<Location> _skeletons;
  private ArrayList<Location> _tnt;
  private ArrayList<Location> _blocks;
  private ArrayList<Location> _clear;
  private long _presents = -1L;
  private long _ignited = -1L;
  private long _exploded = -1L;
  private long _cleared = -1L;
  
  public Part1(Christmas host, Location sleigh, Location[] presents, ArrayList<Location> skeletons, ArrayList<Location> tnt, ArrayList<Location> blocks, ArrayList<Location> clear)
  {
    super(host, sleigh, presents);
    
    this._skeletons = skeletons;
    this._tnt = tnt;
    this._blocks = blocks;
    this._clear = clear;
    
    for (Location loc : this._blocks) {
      if (loc.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
        loc.getBlock().setType(Material.SNOW);
      } else {
        loc.getBlock().setType(Material.STONE);
      }
    }
    Iterator<Location> locIterator = this._clear.iterator();
    while (locIterator.hasNext())
    {
      Location loc = (Location)locIterator.next();
      
      if (UtilMath.offset(loc, GetSleighWaypoint()) > 50.0D) {
        locIterator.remove();
      }
      loc.getBlock().setType(Material.AIR);
    }
  }
  

  public void Activate()
  {
    this.Host.SantaSay("Follow me! Let's find those stolen presents!");
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    UpdatePresents();
    UpdateIgnite();
    UpdateExplode();
    UpdateClear();
    UpdateSkeleton();
  }
  
  private void UpdateSkeleton()
  {
    if (this._exploded < 0L) {
      return;
    }
    if (GetCreatures().size() > 40) {
      return;
    }
    
    Location loc = (Location)UtilAlg.Random(this._skeletons);
    
    this.Host.CreatureAllowOverride = true;
    Skeleton skel = (Skeleton)loc.getWorld().spawn(loc, Skeleton.class);
    skel.getEquipment().setItemInHand(new ItemStack(Material.WOOD_HOE));
    skel.setHealth(4.0D);
    this.Host.CreatureAllowOverride = false;
    
    AddCreature(skel);
  }
  
  private void UpdatePresents()
  {
    if (this._presents > 0L) {
      return;
    }
    if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 35.0D) {
      return;
    }
    this._presents = System.currentTimeMillis();
    
    this.Host.SantaSay("Theres some of the presents up ahead!");
    SetObjectivePresents();
  }
  
  private void UpdateIgnite()
  {
    if (this._ignited > 0L) {
      return;
    }
    if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 20.0D) {
      return;
    }
    this._ignited = System.currentTimeMillis();
    
    for (Location loc : this._tnt) {
      loc.getWorld().spawn(loc, TNTPrimed.class);
    }
    this.Host.SantaSay("LOOK OUT! IT'S A TRAP!!!");
  }
  
  private void UpdateExplode()
  {
    if (this._exploded > 0L) {
      return;
    }
    if (this._ignited < 0L) {
      return;
    }
    if (!UtilTime.elapsed(this._ignited, 4000L)) {
      return;
    }
    this._exploded = System.currentTimeMillis();
    
    for (Location loc : this._blocks)
    {
      Block block = loc.getBlock();
      
      block.setType(Material.AIR);
      
      loc.getWorld().spawnFallingBlock(loc, Material.COBBLESTONE, (byte)0);
    }
    
    this.Host.SantaSay("Clear the path! Watch out for the undead!");
    SetObjectiveText("Clear a path for Santas Sleigh!", 1.0D);
  }
  
  private void UpdateClear()
  {
    if (this._cleared > 0L) {
      return;
    }
    
    if ((this._exploded < 0L) || (!UtilTime.elapsed(this._exploded, 5000L))) {
      return;
    }
    
    for (Location loc : this._clear) {
      if (loc.getBlock().getType() != Material.AIR)
        return;
    }
    this._cleared = System.currentTimeMillis();
  }
  
  public boolean CanFinish()
  {
    if ((this._cleared < 0L) || (!UtilTime.elapsed(this._cleared, 2000L))) {
      return false;
    }
    return true;
  }
}
