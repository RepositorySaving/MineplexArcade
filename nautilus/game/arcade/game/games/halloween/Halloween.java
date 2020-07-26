package nautilus.game.arcade.game.games.halloween;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.donation.DonationManager;
import mineplex.core.explosion.Explosion;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.halloween.creatures.CreatureBase;
import nautilus.game.arcade.game.games.halloween.creatures.InterfaceMove;
import nautilus.game.arcade.game.games.halloween.kits.KitThor;
import nautilus.game.arcade.game.games.halloween.waves.Wave2;
import nautilus.game.arcade.game.games.halloween.waves.Wave4;
import nautilus.game.arcade.game.games.halloween.waves.Wave5;
import nautilus.game.arcade.game.games.halloween.waves.WaveBase;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class Halloween extends SoloGame
{
  private ArrayList<ArrayList<Location>> _spawns;
  private ArrayList<WaveBase> _waves;
  private int _wave = 0;
  
  private int _maxMobs = 80;
  private ArrayList<CreatureBase> _mobs = new ArrayList();
  
  public long total = 0L;
  public long move = 0L;
  public long wave = 0L;
  public long sound = 0L;
  public long update = 0L;
  public long damage = 0L;
  public long target = 0L;
  














  public Halloween(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Halloween, new nautilus.game.arcade.kit.Kit[] {new nautilus.game.arcade.game.games.halloween.kits.KitFinn(manager), new nautilus.game.arcade.game.games.halloween.kits.KitRobinHood(manager), new KitThor(manager) }, new String[] {"Do not die.", "Work as a team!", "Defeat the waves of monsters", "Kill the Pumpkin King" });
    

    this.DamagePvP = false;
    
    this.WorldTimeSet = 16000;
    
    this.ItemDrop = false;
    this.ItemPickup = false;
    
    this.PrepareFreeze = false;
    
    this.HungerSet = 20;
    
    this.WorldBoundaryKill = false;
  }
  

  public void ParseData()
  {
    this._spawns = new ArrayList();
    this._spawns.add(this.WorldData.GetDataLocs("RED"));
    this._spawns.add(this.WorldData.GetDataLocs("YELLOW"));
    this._spawns.add(this.WorldData.GetDataLocs("GREEN"));
    this._spawns.add(this.WorldData.GetDataLocs("BLUE"));
    
    this._waves = new ArrayList();
    this._waves.add(new nautilus.game.arcade.game.games.halloween.waves.Wave1(this));
    this._waves.add(new Wave2(this));
    this._waves.add(new nautilus.game.arcade.game.games.halloween.waves.Wave3(this));
    this._waves.add(new Wave4(this));
    this._waves.add(new Wave5(this));
    this._waves.add(new nautilus.game.arcade.game.games.halloween.waves.WaveBoss(this));
    this._waves.add(new nautilus.game.arcade.game.games.halloween.waves.WaveVictory(this));
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void Clean(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.End) {
      return;
    }
    for (CreatureBase ent : this._mobs) {
      ent.GetEntity().remove();
    }
    this._mobs.clear();
    this._spawns.clear();
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void TeamGen(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    GetTeamList().add(new GameTeam(this, "Pumpkin King", ChatColor.RED, this.WorldData.GetDataLocs("RED")));
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void TimeReport(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    System.out.println("Game State: " + GetState());
    System.out.println("Wave #: " + this._wave);
    System.out.println("Mobs #: " + this._mobs.size());
    System.out.println(" ");
    System.out.println("Total Time: " + UtilTime.convertString(this.total, 4, UtilTime.TimeUnit.MILLISECONDS));
    System.out.println("Move Time: " + UtilTime.convertString(this.move, 4, UtilTime.TimeUnit.MILLISECONDS));
    System.out.println("Wave Time: " + UtilTime.convertString(this.wave, 4, UtilTime.TimeUnit.MILLISECONDS));
    System.out.println("Sound Time: " + UtilTime.convertString(this.sound, 4, UtilTime.TimeUnit.MILLISECONDS));
    System.out.println("Update Time: " + UtilTime.convertString(this.update, 4, UtilTime.TimeUnit.MILLISECONDS));
    System.out.println("Damage Time: " + UtilTime.convertString(this.damage, 4, UtilTime.TimeUnit.MILLISECONDS));
    System.out.println("Target Time: " + UtilTime.convertString(this.target, 4, UtilTime.TimeUnit.MILLISECONDS));
    
    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    
    this.total = 0L;
    this.move = 0L;
    this.wave = 0L;
    this.sound = 0L;
    this.update = 0L;
    this.damage = 0L;
    this.target = 0L;
  }
  
  @EventHandler
  public void SoundUpdate(UpdateEvent event)
  {
    long start = System.currentTimeMillis();
    
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (Math.random() > 0.85D) {
      return;
    }
    for (Player player : mineplex.core.common.util.UtilServer.getPlayers()) {
      player.playSound(player.getLocation(), org.bukkit.Sound.AMBIENCE_CAVE, 3.0F, 1.0F);
    }
    this.total += System.currentTimeMillis() - start;
    this.sound += System.currentTimeMillis() - start;
  }
  
  @EventHandler
  public void WaveUpdate(UpdateEvent event)
  {
    long start = System.currentTimeMillis();
    
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (((WaveBase)this._waves.get(this._wave)).Update(this._wave + 1))
    {
      this._wave += 1;
      
      EndCheck();
    }
    
    this.total += System.currentTimeMillis() - start;
    this.wave += System.currentTimeMillis() - start;
  }
  
  public ArrayList<Location> GetSpawnSet(int i)
  {
    return (ArrayList)this._spawns.get(i);
  }
  
  public Location GetRandomSpawn()
  {
    ArrayList<Location> locSet = GetSpawnSet(UtilMath.r(this._spawns.size()));
    return (Location)locSet.get(UtilMath.r(locSet.size()));
  }
  
  public void AddCreature(CreatureBase mob)
  {
    this._mobs.add(0, mob);
  }
  
  public ArrayList<CreatureBase> GetCreatures()
  {
    return this._mobs;
  }
  
  @EventHandler
  public void CreatureMoveUpdate(UpdateEvent event)
  {
    long start = System.currentTimeMillis();
    
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._mobs.isEmpty()) {
      return;
    }
    CreatureBase base = (CreatureBase)this._mobs.remove(0);
    
    if ((base instanceof InterfaceMove))
    {
      InterfaceMove move = (InterfaceMove)base;
      
      move.Move();
    }
    
    this._mobs.add(base);
    
    this.total += System.currentTimeMillis() - start;
    this.move += System.currentTimeMillis() - start;
  }
  
  @EventHandler
  public void CreatureUpdate(UpdateEvent event)
  {
    long start = System.currentTimeMillis();
    
    if (!IsLive()) {
      return;
    }
    
    Iterator<CreatureBase> mobIterator = this._mobs.iterator();
    while (mobIterator.hasNext())
    {
      CreatureBase base = (CreatureBase)mobIterator.next();
      
      if (base.Updater(event)) {
        mobIterator.remove();
      }
    }
    this.total += System.currentTimeMillis() - start;
    this.update += System.currentTimeMillis() - start;
  }
  
  @EventHandler
  public void CreatureDamage(CustomDamageEvent event)
  {
    long start = System.currentTimeMillis();
    
    for (CreatureBase base : this._mobs) {
      base.Damage(event);
    }
    this.total += System.currentTimeMillis() - start;
    this.damage += System.currentTimeMillis() - start;
  }
  
  @EventHandler
  public void CreatureTarget(EntityTargetEvent event)
  {
    long start = System.currentTimeMillis();
    
    for (CreatureBase base : this._mobs) {
      base.Target(event);
    }
    this.total += System.currentTimeMillis() - start;
    this.target += System.currentTimeMillis() - start;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void EntityDeath(EntityDeathEvent event)
  {
    event.getDrops().clear();
  }
  

  public void EndCheck()
  {
    if (!IsLive())
      return;
    Object out;
    if (this._wave >= this._waves.size())
    {
      for (Player player : GetPlayers(false))
      {
        this.Manager.GetDonation().PurchaseUnknownSalesPackage(null, player.getName(), "Pumpkin Kings Head", 0, true);
        this.Manager.GetGame().AddGems(player, 30.0D, "Killing the Pumpkin King", false);
        this.Manager.GetGame().AddGems(player, 10.0D, "Participation", false);
      }
      
      FileWriter fstream = null;
      out = null;
      try
      {
        fstream = new FileWriter("HalloweenHelmets.dat", true);
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
        System.err.println("Halloween Helmet Save: " + e.getMessage());
        


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
      SetCustomWinLine("You earned Pumpkin Kings Head!");
      AnnounceEnd((GameTeam)GetTeamList().get(0));

    }
    else if (GetPlayers(true).size() == 0)
    {
      for (out = GetPlayers(false).iterator(); ((Iterator)out).hasNext();) { Player player = (Player)((Iterator)out).next();
        
        this.Manager.GetGame().AddGems(player, 10.0D, "Participation", false);
      }
      
      SetState(Game.GameState.End);
      SetCustomWinLine("You lost...");
      AnnounceEnd((GameTeam)GetTeamList().get(1));
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void Explosion(EntityExplodeEvent event)
  {
    if ((event.getEntity() instanceof org.bukkit.entity.Fireball))
    {
      event.blockList().clear();
      
      Collection<Block> blocks = mineplex.core.common.util.UtilBlock.getInRadius(event.getLocation(), 3.5D).keySet();
      
      Iterator<Block> blockIterator = blocks.iterator();
      
      while (blockIterator.hasNext())
      {
        Block block = (Block)blockIterator.next();
        
        if (block.getY() < 4) {
          blockIterator.remove();
        }
      }
      this.Manager.GetExplosion().BlockExplosion(blocks, event.getLocation(), false);
    }
  }
  
  @EventHandler
  public void ItemSpawn(ItemSpawnEvent event)
  {
    Material type = event.getEntity().getItemStack().getType();
    
    if ((type == Material.DIAMOND_AXE) || (type == Material.FIRE) || (type == Material.SNOW_BALL)) {
      return;
    }
    event.setCancelled(true);
  }
  
  public int GetMaxMobs()
  {
    return this._maxMobs;
  }
  
  private long _helpTimer = 0L;
  private int _helpIndex = 0;
  
  private String[] _help = {
    C.cGreen + "Giants one hit kill you! Stay away!!!", 
    C.cAqua + "Work together with your team mates.", 
    C.cGreen + "Each kit gives a buff to nearby allies.", 
    C.cAqua + "Kill monsters to keep their numbers down.", 
    C.cGreen + "Kill giants quickly.", 
    C.cAqua + "Defend your team mates from monsters.", 
    C.cGreen + "Zombies, Giants and Spiders get faster over time.", 
    C.cAqua + "Stick together to survive.", 
    C.cGreen + "The Pumpkin King gets harder over time!" };
  

  @EventHandler
  public void StateUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (GetState() != Game.GameState.Recruit) {
      return;
    }
    if (!UtilTime.elapsed(this._helpTimer, 8000L)) {
      return;
    }
    this._helpTimer = System.currentTimeMillis();
    
    Announce(C.cWhite + C.Bold + "TIP " + ChatColor.RESET + this._help[this._helpIndex]);
    
    this._helpIndex = ((this._helpIndex + 1) % this._help.length);
  }
}
