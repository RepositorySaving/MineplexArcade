package nautilus.game.arcade.game.games.christmas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.christmas.kits.KitPlayer;
import nautilus.game.arcade.game.games.christmas.parts.Part;
import nautilus.game.arcade.game.games.christmas.parts.Part2;
import nautilus.game.arcade.game.games.christmas.parts.Part3;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

public class Christmas extends SoloGame
{
  private Sleigh _sleigh;
  private Location _sleighSpawn;
  private ArrayList<Part> _parts = new ArrayList();
  
  private Part _part;
  private ArrayList<Location> _barrier = new ArrayList();
  
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private long _gameTime = 900000L;
  











  public Christmas(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Christmas, new Kit[] {new KitPlayer(manager) }, new String[] {"Follow Santa Claus", "Find the 10 Stolen Presents", "Defeat the Thief who stole the Presents!" });
    

    this.BlockBreakAllow.add(Integer.valueOf(4));
    this.HungerSet = 20;
    this.WorldTimeSet = 2000;
  }
  


  public void ParseData()
  {
    this._sleighSpawn = ((Location)this.WorldData.GetDataLocs("RED").get(0));
    

    ArrayList<Location> _sleighWaypoints = new ArrayList();
    double dist; while (!this.WorldData.GetDataLocs("PINK").isEmpty())
    {
      Location bestLoc = null;
      double bestDist = 0.0D;
      
      for (Location loc : this.WorldData.GetDataLocs("PINK"))
      {
        dist = UtilMath.offset(loc, this._sleighSpawn);
        
        if ((bestLoc == null) || (bestDist > dist))
        {
          bestLoc = loc;
          bestDist = dist;
        }
      }
      
      _sleighWaypoints.add(bestLoc);
      this.WorldData.GetDataLocs("PINK").remove(bestLoc);
    }
    

    ArrayList<Location> _presents = new ArrayList();
    double bestDist; while (!this.WorldData.GetDataLocs("LIME").isEmpty())
    {
      Location bestLoc = null;
      bestDist = 0.0D;
      
      for (Location loc : this.WorldData.GetDataLocs("LIME"))
      {
        double dist = UtilMath.offset(loc, this._sleighSpawn);
        
        if ((bestLoc == null) || (bestDist > dist))
        {
          bestLoc = loc;
          bestDist = dist;
        }
      }
      
      _presents.add(bestLoc);
      this.WorldData.GetDataLocs("LIME").remove(bestLoc);
    }
    

    for (Location loc : this.WorldData.GetCustomLocs("129"))
    {
      this._barrier.add(loc.getBlock().getLocation());
      mineplex.core.common.util.MapUtil.QuickChangeBlockAt(loc, 65, (byte)3);
    }
    

    this._parts.add(new nautilus.game.arcade.game.games.christmas.parts.Part1(this, (Location)_sleighWaypoints.remove(0), new Location[] { (Location)_presents.remove(0), (Location)_presents.remove(0) }, 
      this.WorldData.GetDataLocs("BLACK"), 
      this.WorldData.GetDataLocs("ORANGE"), 
      this.WorldData.GetCustomLocs("19"), 
      this.WorldData.GetCustomLocs("47")));
    
    this._parts.add(new Part2(this, (Location)_sleighWaypoints.remove(0), new Location[] { (Location)_presents.remove(0), (Location)_presents.remove(0) }, 
      this.WorldData.GetDataLocs("YELLOW"), 
      this.WorldData.GetDataLocs("BROWN"), 
      this.WorldData.GetCustomLocs("48")));
    
    this._parts.add(new Part3(this, (Location)_sleighWaypoints.remove(0), new Location[] { (Location)_presents.remove(0), (Location)_presents.remove(0) }, 
      this.WorldData.GetDataLocs("GRAY"), 
      this.WorldData.GetDataLocs("SILVER"), 
      this.WorldData.GetDataLocs("WHITE"), 
      this.WorldData.GetDataLocs("PURPLE")));
    
    this._parts.add(new nautilus.game.arcade.game.games.christmas.parts.Part4(this, (Location)_sleighWaypoints.remove(0), new Location[] { (Location)_presents.remove(0), (Location)_presents.remove(0) }, 
      this.WorldData.GetCustomLocs("56"), 
      this.WorldData.GetDataLocs("MAGENTA"), 
      this.WorldData.GetCustomLocs("22"), 
      this.WorldData.GetCustomLocs("45"), 
      this.WorldData.GetCustomLocs("121"), 
      (Location)_sleighWaypoints.get(0)));
    
    this._parts.add(new nautilus.game.arcade.game.games.christmas.parts.Part5(this, (Location)_sleighWaypoints.remove(0), new Location[] { (Location)_presents.remove(0), (Location)_presents.remove(0) }, 
      this.WorldData.GetCustomLocs("14"), 
      this.WorldData.GetCustomLocs("15"), 
      this.WorldData.GetCustomLocs("16"), 
      this.WorldData.GetCustomLocs("87"), 
      this.WorldData.GetCustomLocs("88"), 
      this.WorldData.GetCustomLocs("89"), 
      this.WorldData.GetCustomLocs("153"), 
      this.WorldData.GetCustomLocs("173")));
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void TeamGen(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    GetTeamList().add(new GameTeam(this, "Christmas Thieves", ChatColor.RED, this.WorldData.GetDataLocs("RED")));
  }
  


  @EventHandler
  public void PartUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() == UpdateType.SEC)
    {
      if (((this._part == null) || (this._part.IsDone())) && (this._parts != null) && (!this._parts.isEmpty()))
      {
        if (this._part != null) {
          HandlerList.unregisterAll(this._part);
        }
        this._part = ((Part)this._parts.remove(0));
        
        this._part.Prepare();
        

        UtilServer.getServer().getPluginManager().registerEvents(this._part, this.Manager.GetPlugin());
        
        GetSleigh().SetTarget(this._part.GetSleighWaypoint());
      }
    }
  }
  
  public Sleigh GetSleigh()
  {
    if (this._sleigh == null) {
      this._sleigh = new Sleigh(this, this._sleighSpawn);
    }
    return this._sleigh;
  }
  
  @EventHandler
  public void SleighSpawn(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    final Christmas christmas = this;
    
    UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        Christmas.this.GetSleigh();
        
        Location loc = christmas.GetSleigh().GetLocation();
        
        christmas.CreatureAllowOverride = true;
        for (int i = 0; i < 20; i++)
        {
          Location elfLoc = UtilBlock.getHighest(loc.getWorld(), (int)(loc.getX() + 20.0D - Math.random() * 40.0D), (int)(loc.getZ() + 20.0D - Math.random() * 40.0D)).getLocation().add(0.5D, 0.5D, 0.5D);
          
          Villager elf = (Villager)elfLoc.getWorld().spawn(elfLoc, Villager.class);
          
          elf.setBaby();
          elf.setAgeLock(true);
          
          elf.setCustomName("Elf");
        }
        
        christmas.CreatureAllowOverride = false;
      }
    }, 20L);
  }
  
  @EventHandler
  public void SleighUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() == UpdateType.TICK) {
      GetSleigh().Update();
    }
  }
  
  @EventHandler
  public void BarrierDecay(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    Location breakAt = null;
    
    for (Location loc : this._barrier)
    {
      if (UtilMath.offset(GetSleigh().GetLocation(), loc) <= 15.0D)
      {

        breakAt = loc;
      }
    }
    if (breakAt != null) {
      BarrierBreak(breakAt);
    }
  }
  
  private void BarrierBreak(Location loc) {
    this._barrier.remove(loc);
    loc.getBlock().setType(org.bukkit.Material.AIR);
    
    for (Block block : UtilBlock.getSurrounding(loc.getBlock(), false))
    {
      if (this._barrier.remove(block.getLocation()))
      {
        BarrierBreak(block.getLocation());
      }
    }
  }
  
  public void SantaSay(String string)
  {
    Announce(C.cRed + C.Bold + "Santa: " + ChatColor.RESET + C.cYellow + string);
  }
  
  public void BossSay(String name, String string)
  {
    for (Player player : )
    {
      player.playSound(player.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
      
      mineplex.core.common.util.UtilPlayer.message(player, C.cDGreen + C.Bold + name + ": " + ChatColor.RESET + C.cGreen + string);
    }
  }
  
  @EventHandler
  public void Combust(EntityCombustEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void ItemSpawn(ItemSpawnEvent event)
  {
    event.setCancelled(true);
  }
  

  public void EndCheck()
  {
    if (!IsLive())
      return;
    Object out;
    if ((this._parts.isEmpty()) && (this._part.IsDone()))
    {
      for (Player player : GetPlayers(false))
      {
        this.Manager.GetDonation().PurchaseUnknownSalesPackage(null, player.getName(), "Snowmans Head", 0, true);
        this.Manager.GetGame().AddGems(player, 30.0D, "Slaying the Pumpkin King", false);
        this.Manager.GetGame().AddGems(player, 10.0D, "Participation", false);
      }
      
      FileWriter fstream = null;
      out = null;
      try
      {
        fstream = new FileWriter("ChristmasHelmets.dat", true);
        out = new BufferedWriter(fstream);
        
        for (Player player : GetPlayers(false))
        {
          ((BufferedWriter)out).write(UtilTime.now() + "\t\t" + player.getName());
          ((BufferedWriter)out).newLine();
        }
        
        ((BufferedWriter)out).close();
      }
      catch (Exception e)
      {
        System.err.println("Christmas Helmet Save: " + e.getMessage());
        


        if (out != null)
        {
          try
          {
            ((BufferedWriter)out).close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
        
        if (fstream != null)
        {
          try
          {
            fstream.close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      }
      finally
      {
        if (out != null)
        {
          try
          {
            ((BufferedWriter)out).close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
        
        if (fstream != null)
        {
          try
          {
            fstream.close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      }
      
      SetState(Game.GameState.End);
      SetCustomWinLine("You earned Snowmans Head!");
      AnnounceEnd((GameTeam)GetTeamList().get(0));

    }
    else if (GetPlayers(true).size() == 0)
    {
      for (out = GetPlayers(false).iterator(); ((Iterator)out).hasNext();) { Player player = (Player)((Iterator)out).next();
        
        this.Manager.GetGame().AddGems(player, 10.0D, "Participation", false);
      }
      
      SetState(Game.GameState.End);
      SetCustomWinLine("You all died...");
      AnnounceEnd((GameTeam)GetTeamList().get(1));
    }
    else if (UtilTime.elapsed(GetStateTime(), this._gameTime))
    {
      for (out = GetPlayers(false).iterator(); ((Iterator)out).hasNext();) { Player player = (Player)((Iterator)out).next();
        
        this.Manager.GetGame().AddGems(player, 10.0D, "Participation", false);
      }
      
      SetState(Game.GameState.End);
      SetCustomWinLine("You did not save Christmas in time.");
      AnnounceEnd((GameTeam)GetTeamList().get(1));
    }
  }
  

  public void End()
  {
    if (!IsLive()) {
      return;
    }
    for (Player player : GetPlayers(false))
    {
      this.Manager.GetGame().AddGems(player, 10.0D, "Participation", false);
    }
    
    SetState(Game.GameState.End);
    SetCustomWinLine("Santa Claus was killed by the Giant!");
    AnnounceEnd((GameTeam)GetTeamList().get(1));
  }
  
  @EventHandler
  public void Skip(PlayerCommandPreprocessEvent event)
  {
    if ((event.getMessage().equals("/skip")) && 
      (event.getPlayer().getName().equals("Chiss")))
    {
      event.setCancelled(true);
      
      if (this._part != null) {
        HandlerList.unregisterAll(this._part);
      }
      if ((this._parts != null) && (!this._parts.isEmpty()))
      {
        this._part = ((Part)this._parts.remove(0));
        
        this._part.Prepare();
        

        UtilServer.getServer().getPluginManager().registerEvents(this._part, this.Manager.GetPlugin());
        
        GetSleigh().SetTarget(this._part.GetSleighWaypoint());
        

        for (Player player : UtilServer.getPlayers()) {
          player.teleport(this._part.GetSleighWaypoint().clone().add(0.0D, 0.0D, 10.0D));
        }
      }
    }
    if ((event.getMessage().equals("/present")) && 
      (event.getPlayer().getName().equals("Chiss")))
    {
      event.setCancelled(true);
      
      GetSleigh().AddPresent(event.getPlayer().getLocation());
    }
  }
  

  @EventHandler(priority=EventPriority.LOWEST)
  public void DamageCancel(CustomDamageEvent event)
  {
    if (this._sleigh != null) {
      GetSleigh().Damage(event);
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FALL) {
      if (event.GetDamageeEntity().getLocation().getY() > 30.0D)
      {
        event.SetCancelled("Fall Cancel");
      }
      else
      {
        event.AddMod("Christmas", "Fall Damage", 20.0D, false);
      }
    }
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    
    int index = 15;
    

    String out = " ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cWhite + "Challenge:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cYellow + (5 - this._parts.size()) + " of " + 5;
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    

    out = "  ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cWhite + "Presents:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cYellow + GetSleigh().GetPresents().size() + " of " + 10;
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    

    out = "   ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cWhite + "Players:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cYellow + GetPlayers(true).size();
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    

    out = "    ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cWhite + "Time Left:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cYellow + UtilTime.MakeStr(this._gameTime - (System.currentTimeMillis() - GetStateTime()));
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
  }
  

  public Location GetSpectatorLocation()
  {
    if (this.SpectatorSpawn == null)
    {
      this.SpectatorSpawn = new Location(this.WorldData.World, 0.0D, 0.0D, 0.0D);
    }
    
    Vector vec = new Vector(0, 0, 0);
    double count = 0.0D;
    
    for (Player player : GetPlayers(true))
    {
      count += 1.0D;
      vec.add(player.getLocation().toVector());
    }
    
    if (count == 0.0D) {
      count += 1.0D;
    }
    vec.multiply(1.0D / count);
    
    this.SpectatorSpawn.setX(vec.getX());
    this.SpectatorSpawn.setY(vec.getY() + 10.0D);
    this.SpectatorSpawn.setZ(vec.getZ());
    
    return this.SpectatorSpawn;
  }
  
  @EventHandler
  public void ProjectileClean(ProjectileHitEvent event)
  {
    event.getEntity().remove();
  }
  
  @EventHandler
  public void DeregisterListeners(GameStateChangeEvent event)
  {
    if ((event.GetState() != Game.GameState.End) && (event.GetState() != Game.GameState.Dead)) {
      return;
    }
    if (this._part != null) {
      HandlerList.unregisterAll(this._part);
    }
  }
}
