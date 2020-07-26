package nautilus.game.arcade.game.games.christmas.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import net.minecraft.server.v1_7_R3.ControllerMove;
import net.minecraft.server.v1_7_R3.EntityCreature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Part implements Listener
{
  public Christmas Host;
  protected Location _sleigh;
  protected Location[] _presents;
  protected boolean _presentsAnnounce = false;
  
  protected String _objective = "Follow Santa";
  protected double _objectiveHealth = 1.0D;
  
  protected HashMap<Creature, Player> _creatures = new HashMap();
  
  public Part(Christmas host, Location sleigh, Location[] presents)
  {
    this.Host = host;
    this._sleigh = sleigh;
    this._presents = presents;
    
    for (Location loc : this._presents) {
      loc.getBlock().setType(Material.AIR);
    }
  }
  
  public void Prepare() {
    Activate();
    
    for (Location loc : this._presents)
    {
      Block present = loc.getBlock();
      present.setTypeIdAndData(Material.SKULL.getId(), (byte)1, true);
      
      Skull skull = (Skull)present.getState();
      skull.setSkullType(SkullType.PLAYER);
      

      double r = Math.random();
      if (r > 0.75D) { skull.setOwner("CruXXx");
      } else if (r > 0.5D) { skull.setOwner("CruXXx");
      } else if (r > 0.25D) skull.setOwner("CruXXx"); else {
        skull.setOwner("CruXXx");
      }
      
      BlockFace face = BlockFace.UP;
      while ((face == BlockFace.UP) || (face == BlockFace.DOWN) || (face == BlockFace.SELF))
        face = BlockFace.values()[mineplex.core.common.util.UtilMath.r(BlockFace.values().length)];
      skull.setRotation(face);
      
      skull.update();
      

      present.getRelative(BlockFace.DOWN).setType(Material.BEACON);
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          present.getRelative(x, -2, z).setType(Material.IRON_BLOCK);
        }
      }
    }
  }
  
  public abstract void Activate();
  
  public boolean IsDone() {
    if (CanFinish())
    {
      if (HasPresents())
      {

        HandlerList.unregisterAll(this);
        

        KillCreatures();
        
        SetObjectiveText("Follow Santa", 1.0D);
        this.Host.SantaSay("Let's move!");
        
        return true;
      }
      if (!this._presentsAnnounce)
      {
        SetObjectivePresents();
        this.Host.SantaSay("Collect the presents and we can get out of here!");
        
        this._presentsAnnounce = true;
      }
    }
    
    return false;
  }
  
  public void SetObjectivePresents()
  {
    SetObjectiveText("Collect the two Presents", 1.0D);
  }
  
  public abstract boolean CanFinish();
  
  public Location GetSleighWaypoint()
  {
    return this._sleigh;
  }
  
  public Location[] GetPresents()
  {
    return this._presents;
  }
  
  public boolean HasPresents()
  {
    for (Location loc : this._presents) {
      if (!this.Host.GetSleigh().HasPresent(loc.getBlock().getLocation()))
        return false;
    }
    return true;
  }
  
  @EventHandler
  public void PresentCollect(PlayerInteractEvent event)
  {
    if (event.getClickedBlock() == null) {
      return;
    }
    boolean contains = false;
    for (Location loc : this._presents) {
      if (loc.getBlock().equals(event.getClickedBlock()))
        contains = true;
    }
    if (!contains) {
      return;
    }
    event.setCancelled(true);
    
    if (!this.Host.IsLive()) {
      return;
    }
    if (!this.Host.IsAlive(event.getPlayer())) {
      return;
    }
    if (this.Host.GetSleigh().HasPresent(event.getClickedBlock().getLocation())) {
      return;
    }
    this.Host.GetSleigh().AddPresent(event.getClickedBlock().getLocation());
    
    this.Host.SantaSay("Well done " + event.getPlayer().getName() + "! You collected a present!");
  }
  
  public void SetObjectiveText(String text, double percent)
  {
    this._objective = text;
    this._objectiveHealth = percent;
  }
  
  @EventHandler
  public void ObjectiveDisplay(UpdateEvent event)
  {
    if (!this.Host.InProgress()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers()) {
      UtilDisplay.displayTextBar(this.Host.Manager.GetPlugin(), player, this._objectiveHealth, C.cYellow + C.Bold + this._objective);
    }
  }
  
  public void AddCreature(Creature ent) {
    this._creatures.put(ent, null);
  }
  
  public HashMap<Creature, Player> GetCreatures()
  {
    return this._creatures;
  }
  
  @EventHandler
  public void UpdateCreatures(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Creature> entIterator = this._creatures.keySet().iterator();
    

    while (entIterator.hasNext())
    {
      Creature ent = (Creature)entIterator.next();
      

      Player target = (Player)this._creatures.get(ent);
      if ((target == null) || (!target.isValid()) || (!this.Host.IsAlive(target)))
      {
        if (this.Host.GetPlayers(true).size() > 0)
        {
          target = (Player)UtilAlg.Random(this.Host.GetPlayers(true));
          this._creatures.put(ent, target);

        }
        

      }
      else
      {

        float speed = 1.0F;
        if ((ent instanceof Ageable))
        {
          if (!((Ageable)ent).isAdult()) {
            speed = 0.6F;
          }
        }
        
        EntityCreature ec = ((CraftCreature)ent).getHandle();
        
        ec.getControllerMove().a(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), speed);
        

        if (!ent.isValid())
        {
          ent.remove();
          entIterator.remove();
        }
      }
    }
  }
  


  public void KillCreatures()
  {
    this._creatures.clear();
  }
}
