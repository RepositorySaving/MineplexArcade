package mineplex.core.teleport;

import java.util.Iterator;
import java.util.LinkedList;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.spawn.Spawn;
import mineplex.core.teleport.command.TeleportCommand;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;


public class Teleport
  extends MiniPlugin
{
  private CoreClientManager _clientManager;
  private Spawn _spawn;
  private LinkedList<Teleporter> teleportList = new LinkedList();
  private NautHashMap<String, LinkedList<Location>> _tpHistory = new NautHashMap();
  
  public Teleport(JavaPlugin plugin, CoreClientManager clientManager, Spawn spawn)
  {
    super("Teleport", plugin);
    
    this._spawn = spawn;
  }
  

  public void AddCommands()
  {
    AddCommand(new TeleportCommand(this));
  }
  
  @EventHandler
  public void UnloadHistory(ClientUnloadEvent event)
  {
    this._tpHistory.remove(event.GetName());
  }
  
  @EventHandler
  public void update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this.teleportList.isEmpty()) {
      return;
    }
    ((Teleporter)this.teleportList.removeFirst()).doTeleport();
  }
  
  public void playerToPlayer(Player caller, String from, String to)
  {
    LinkedList<Player> listA = new LinkedList();
    

    if (from.equals("%ALL%")) {
      for (Player cur : UtilServer.getPlayers()) {
        listA.add(cur);
      }
    } else {
      listA = UtilPlayer.matchOnline(caller, from, true);
    }
    
    Player pB = UtilPlayer.searchOnline(caller, to, true);
    
    if ((listA.isEmpty()) || (pB == null)) {
      return;
    }
    if (listA.size() == 1)
    {
      Player pA = (Player)listA.getFirst();
      
      String mA = null;
      mB = null;
      

      if (pA.equals(caller))
      {
        mA = F.main("Teleport", "You teleported to " + F.elem(pB.getName()) + ".");
      }
      else if (pB.equals(caller))
      {
        mA = F.main("Teleport", F.elem(caller.getName()) + " teleported you to themself.");
        mB = F.main("Teleport", "You teleported " + F.elem(pA.getName()) + " to yourself.");
      }
      else
      {
        mA = F.main("Teleport", F.elem(caller.getName()) + " teleported you to " + F.elem(pB.getName()) + ".");
        mB = F.main("Teleport", "You teleported " + F.elem(pA.getName()) + " to " + F.elem(pB.getName()) + ".");
      }
      

      Add(pA, pB.getLocation(), mA, true, caller, (String)mB, 
        pA.getName() + " teleported to " + pB.getName() + " via " + caller.getName());
      return;
    }
    
    boolean first = true;
    for (Object mB = listA.iterator(); ((Iterator)mB).hasNext();) { Player pA = (Player)((Iterator)mB).next();
      
      String mA = null;
      String mB = null;
      

      if (pA.equals(caller))
      {
        mA = F.main("Teleport", "You teleported to " + F.elem(pB.getName()) + ".");
      }
      else if (pB.equals(caller))
      {
        mA = F.main("Teleport", F.elem(caller.getName()) + " teleported you to themself.");
        mB = F.main("Teleport", "You teleported " + F.elem(new StringBuilder(String.valueOf(listA.size())).append(" Players").toString()) + " to yourself.");
      }
      else
      {
        mA = F.main("Teleport", F.elem(caller.getName()) + " teleported you to " + F.elem(pB.getName()) + ".");
        mB = F.main("Teleport", "You teleported " + F.elem(new StringBuilder(String.valueOf(listA.size())).append(" Players").toString()) + " to " + F.elem(pB.getName()) + ".");
      }
      

      if (first) {
        Add(pA, pB.getLocation(), mA, true, caller, mB, pA.getName() + " teleported to " + pB.getName() + " via " + caller.getName());
      }
      else {
        Add(pA, pB.getLocation(), mA, true, caller, null, pA.getName() + " teleported to " + pB.getName() + " via " + caller.getName());
      }
      first = false;
    }
  }
  
  public void playerToLoc(Player caller, String target, String sX, String sY, String sZ)
  {
    playerToLoc(caller, target, caller.getWorld().getName(), sX, sY, sZ);
  }
  
  public void playerToLoc(Player caller, String target, String world, String sX, String sY, String sZ)
  {
    Player player = UtilPlayer.searchOnline(caller, target, true);
    
    if (player == null) {
      return;
    }
    try
    {
      int x = Integer.parseInt(sX);
      int y = Integer.parseInt(sY);
      int z = Integer.parseInt(sZ);
      
      Location loc = new Location(Bukkit.getWorld(world), x, y, z);
      

      String mA = null;
      if (caller == player) mA = F.main("Teleport", "You teleported to " + UtilWorld.locToStrClean(loc) + "."); else {
        mA = F.main("Teleport", F.elem(caller.getName()) + " teleported you to " + UtilWorld.locToStrClean(loc) + ".");
      }
      
      Add(player, loc, mA, true, caller, null, player.getName() + " teleported to " + UtilWorld.locToStrClean(loc) + " via " + caller.getName());
    }
    catch (Exception e)
    {
      UtilPlayer.message(caller, F.main("Teleport", "Invalid Location [" + sX + "," + sY + "," + sZ + "]."));
      return;
    }
  }
  
  public void playerToSpawn(Player caller, String target)
  {
    Player player = UtilPlayer.searchOnline(caller, target, true);
    
    if (player == null) {
      return;
    }
    String mA = F.main("Teleport", F.elem(caller.getName()) + " teleported you to " + F.elem("Spawn") + ".");
    String mB = F.main("Teleport", "You teleported " + F.count(player.getName()) + " to " + F.elem("Spawn") + ".");
    Add(player, this._spawn.getSpawn(), mA, true, caller, mB, player.getName() + " teleported to Spawn via " + caller.getName() + ".");
  }
  
  public void Add(Player pA, Location loc, String mA, boolean record, Player pB, String mB, String log)
  {
    this.teleportList.addLast(new Teleporter(this, pA, pB, mA, mB, loc, record, log));
  }
  
  public void TP(Player player, Location getLocation)
  {
    TP(player, getLocation, true);
  }
  
  public void TP(Player player, Location getLocation, boolean dettach)
  {
    if (dettach)
    {
      player.eject();
      player.leaveVehicle();
    }
    
    player.setFallDistance(0.0F);
    player.setVelocity(new Vector(0, 0, 0));
    
    player.teleport(getLocation);
  }
  
  public LinkedList<Location> GetTPHistory(Player player)
  {
    return (LinkedList)this._tpHistory.get(player.getName());
  }
}
