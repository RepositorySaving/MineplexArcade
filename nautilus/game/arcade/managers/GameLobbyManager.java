package nautilus.game.arcade.managers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilText;
import mineplex.core.common.util.UtilText.TextAlign;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.packethandler.PacketVerifier;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameServerConfig;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.world.WorldData;
import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.WatchableObject;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameLobbyManager implements mineplex.core.packethandler.IPacketRunnable, Listener
{
  public ArcadeManager Manager;
  private Location _gameText;
  private Location _advText;
  private Location _kitText;
  private Location _teamText;
  private Location _kitDisplay;
  private Location _teamDisplay;
  private Location spawn;
  private NautHashMap<Entity, LobbyEnt> _kits = new NautHashMap();
  private NautHashMap<Block, Material> _kitBlocks = new NautHashMap();
  
  private NautHashMap<Entity, LobbyEnt> _teams = new NautHashMap();
  private NautHashMap<Block, Material> _teamBlocks = new NautHashMap();
  
  private long _fireworkStart;
  
  private Color _fireworkColor;
  private int _advertiseStage = 0;
  

  private NautHashMap<Player, Scoreboard> _scoreboardMap = new NautHashMap();
  private NautHashMap<Player, Integer> _gemMap = new NautHashMap();
  private NautHashMap<Player, String> _kitMap = new NautHashMap();
  
  private int _oldPlayerCount = 0;
  
  public GameLobbyManager(ArcadeManager manager, PacketHandler packetHandler)
  {
    this.Manager = manager;
    packetHandler.AddPacketRunnable(this);
    
    World world = UtilWorld.getWorld("world");
    
    this.spawn = new Location(world, 0.0D, 104.0D, 0.0D);
    
    this._gameText = new Location(world, 0.0D, 130.0D, 50.0D);
    this._kitText = new Location(world, -40.0D, 120.0D, 0.0D);
    this._teamText = new Location(world, 40.0D, 120.0D, 0.0D);
    this._advText = new Location(world, 0.0D, 140.0D, -60.0D);
    
    this._kitDisplay = new Location(world, -17.0D, 101.0D, 0.0D);
    this._teamDisplay = new Location(world, 18.0D, 101.0D, 0.0D);
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  private boolean HasScoreboard(Player player)
  {
    return this._scoreboardMap.containsKey(player);
  }
  
  public void CreateScoreboards()
  {
    for (Player player : ) {
      CreateScoreboard(player);
    }
  }
  
  private void CreateScoreboard(Player player) {
    this._scoreboardMap.put(player, org.bukkit.Bukkit.getScoreboardManager().getNewScoreboard());
    
    Scoreboard scoreboard = (Scoreboard)this._scoreboardMap.get(player);
    Objective objective = scoreboard.registerNewObjective("§lLobby", "dummy");
    objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
    
    for (Rank rank : Rank.values())
    {
      if (rank == Rank.ALL)
      {
        scoreboard.registerNewTeam(rank.Name).setPrefix("");
      }
      else
      {
        scoreboard.registerNewTeam(rank.Name).setPrefix(rank.GetTag(true, true) + ChatColor.RESET + " " + ChatColor.WHITE);
      }
      
      if ((this.Manager.GetGame() != null) && (!this.Manager.GetGame().GetTeamList().isEmpty()))
      {
        for (GameTeam team : this.Manager.GetGame().GetTeamList())
        {
          if (rank == Rank.ALL)
          {
            scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(team.GetColor());
          }
          else
          {
            scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(rank.GetTag(true, true) + ChatColor.RESET + " " + team.GetColor());
          }
        }
      }
    }
    
    for (Player otherPlayer : UtilServer.getPlayers())
    {
      AddPlayerToScoreboards(otherPlayer, null);
    }
  }
  
  public Collection<Scoreboard> GetScoreboards()
  {
    return this._scoreboardMap.values();
  }
  
  public void WriteLine(Player player, int x, int y, int z, BlockFace face, int line, String text)
  {
    Location loc = player.getLocation();
    loc.setX(x);
    loc.setY(y);
    loc.setZ(z);
    
    int id = 159;
    byte data = 15;
    
    if ((player.getItemInHand() != null) && (player.getItemInHand().getType().isBlock()) && (player.getItemInHand().getType() != Material.AIR))
    {
      id = player.getItemInHand().getTypeId();
      data = UtilInv.GetData(player.getItemInHand());
    }
    
    if (line > 0) {
      loc.add(0.0D, line * -6, 0.0D);
    }
    UtilText.MakeText(text, loc, face, id, data, UtilText.TextAlign.CENTER);
    
    player.sendMessage("Writing: " + text + " @ " + UtilWorld.locToStrClean(loc));
  }
  
  public void WriteGameLine(String text, int line, int id, byte data)
  {
    Location loc = this._gameText.clone();
    
    if (line > 0) {
      loc.add(0.0D, line * -6, 0.0D);
    }
    BlockFace face = BlockFace.WEST;
    
    UtilText.MakeText(text, loc, face, id, data, UtilText.TextAlign.CENTER);
  }
  
  public void WriteAdvertiseLine(String text, int line, int id, byte data)
  {
    Location loc = this._advText.clone();
    
    if (line > 0) {
      loc.add(0.0D, line * -6, 0.0D);
    }
    BlockFace face = BlockFace.EAST;
    
    UtilText.MakeText(text, loc, face, id, data, UtilText.TextAlign.CENTER);
  }
  
  public void WriteKitLine(String text, int line, int id, byte data)
  {
    Location loc = this._kitText.clone();
    
    if (line > 0) {
      loc.add(0.0D, line * -6, 0.0D);
    }
    BlockFace face = BlockFace.NORTH;
    
    UtilText.MakeText(text, loc, face, id, data, UtilText.TextAlign.CENTER);
  }
  
  public void WriteTeamLine(String text, int line, int id, byte data)
  {
    Location loc = this._teamText.clone();
    
    if (line > 0) {
      loc.add(0.0D, line * -6, 0.0D);
    }
    BlockFace face = BlockFace.SOUTH;
    
    UtilText.MakeText(text, loc, face, id, data, UtilText.TextAlign.CENTER);
  }
  
  public Location GetSpawn()
  {
    return this.spawn.clone().add(4.0D - Math.random() * 8.0D, 0.0D, 4.0D - Math.random() * 8.0D);
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void TeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    if (event.GetGame().GetMode() == null) {
      WriteGameLine(event.GetGame().WorldData.MapName, 1, 159, (byte)4);
    } else {
      WriteGameLine(event.GetGame().WorldData.MapName, 2, 159, (byte)4);
    }
    CreateTeams(event.GetGame());
  }
  

  public void CreateTeams(Game game)
  {
    WriteTeamLine("Select", 0, 159, (byte)15);
    WriteTeamLine("Team", 1, 159, (byte)4);
    

    for (Entity ent : this._teams.keySet())
      ent.remove();
    this._teams.clear();
    

    for (Block block : this._teamBlocks.keySet())
      block.setType((Material)this._teamBlocks.get(block));
    this._teamBlocks.clear();
    

    if ((game.GetType() == GameType.Smash) || (game.GetType() == GameType.SurvivalGames))
    {

      WriteTeamLine("Ultra", 0, 159, (byte)15);
      WriteTeamLine("Kits", 1, 159, (byte)4);
      
      CreateScoreboards();
      return;
    }
    

    if ((game.GetTeamList().size() > 10) && (game.GetType() == GameType.UHC))
    {
      WriteTeamLine("Season", 0, 159, (byte)15);
      WriteTeamLine("5", 1, 159, (byte)4);
      
      WriteKitLine("Season", 0, 159, (byte)15);
      WriteKitLine("5", 1, 159, (byte)4);
      
      CreateScoreboards();
      return;
    }
    

    if ((game.GetKits().length > 1) || (game.GetType() != GameType.UHC))
    {

      ArrayList<GameTeam> teams = new ArrayList();
      
      for (GameTeam team : game.GetTeamList()) {
        if (team.GetVisible()) {
          teams.add(team);
        }
      }
      double space = 6.0D;
      double offset = (teams.size() - 1) * space / 2.0D;
      
      for (int i = 0; i < teams.size(); i++)
      {
        Location entLoc = this._teamDisplay.clone().subtract(0.0D, 0.0D, i * space - offset);
        
        SetKitTeamBlocks(entLoc.clone(), 35, ((GameTeam)teams.get(i)).GetColorData(), this._teamBlocks);
        
        entLoc.add(0.0D, 1.5D, 0.0D);
        
        entLoc.getChunk().load();
        
        Sheep ent = (Sheep)this.Manager.GetCreature().SpawnEntity(entLoc, EntityType.SHEEP);
        ent.setRemoveWhenFarAway(false);
        ent.setCustomNameVisible(true);
        
        ent.setColor(DyeColor.getByWoolData(((GameTeam)teams.get(i)).GetColorData()));
        
        UtilEnt.Vegetate(ent);
        
        ((GameTeam)teams.get(i)).SetTeamEntity(ent);
        
        this._teams.put(ent, new LobbyEnt(ent, entLoc, (GameTeam)teams.get(i)));
      }
      

    }
    else
    {
      WriteKitLine("Select", 0, 159, (byte)15);
      WriteKitLine("Team", 1, 159, (byte)4);
      

      ArrayList<GameTeam> teamsA = new ArrayList();
      Object teamsB = new ArrayList();
      
      for (int i = 0; i < game.GetTeamList().size(); i++)
      {
        if (i < game.GetTeamList().size() / 2) {
          teamsA.add((GameTeam)game.GetTeamList().get(i));
        } else {
          ((ArrayList)teamsB).add((GameTeam)game.GetTeamList().get(i));
        }
      }
      


      double space = 6.0D;
      double offset = (teamsA.size() - 1) * space / 2.0D;
      
      for (int i = 0; i < teamsA.size(); i++)
      {
        Location entLoc = this._teamDisplay.clone().subtract(0.0D, 0.0D, i * space - offset);
        
        SetKitTeamBlocks(entLoc.clone(), 35, ((GameTeam)teamsA.get(i)).GetColorData(), this._teamBlocks);
        
        entLoc.add(0.0D, 1.5D, 0.0D);
        
        entLoc.getChunk().load();
        
        Sheep ent = (Sheep)this.Manager.GetCreature().SpawnEntity(entLoc, EntityType.SHEEP);
        ent.setRemoveWhenFarAway(false);
        ent.setCustomNameVisible(true);
        
        ent.setColor(DyeColor.getByWoolData(((GameTeam)teamsA.get(i)).GetColorData()));
        
        UtilEnt.Vegetate(ent);
        
        ((GameTeam)teamsA.get(i)).SetTeamEntity(ent);
        
        this._teams.put(ent, new LobbyEnt(ent, entLoc, (GameTeam)teamsA.get(i)));
      }
      



      double space = 6.0D;
      double offset = (((ArrayList)teamsB).size() - 1) * space / 2.0D;
      
      for (int i = 0; i < ((ArrayList)teamsB).size(); i++)
      {
        Location entLoc = this._kitDisplay.clone().subtract(0.0D, 0.0D, i * space - offset);
        
        SetKitTeamBlocks(entLoc.clone(), 35, ((GameTeam)((ArrayList)teamsB).get(i)).GetColorData(), this._teamBlocks);
        
        entLoc.add(0.0D, 1.5D, 0.0D);
        
        entLoc.getChunk().load();
        
        Sheep ent = (Sheep)this.Manager.GetCreature().SpawnEntity(entLoc, EntityType.SHEEP);
        ent.setRemoveWhenFarAway(false);
        ent.setCustomNameVisible(true);
        
        ent.setColor(DyeColor.getByWoolData(((GameTeam)((ArrayList)teamsB).get(i)).GetColorData()));
        
        UtilEnt.Vegetate(ent);
        
        ((GameTeam)((ArrayList)teamsB).get(i)).SetTeamEntity(ent);
        
        this._teams.put(ent, new LobbyEnt(ent, entLoc, (GameTeam)((ArrayList)teamsB).get(i)));
      }
    }
    

    CreateScoreboards();
  }
  

  public void CreateKits(Game game)
  {
    WriteKitLine("Select", 0, 159, (byte)15);
    WriteKitLine("Kit", 1, 159, (byte)4);
    

    for (Entity ent : this._kits.keySet())
      ent.remove();
    this._kits.clear();
    

    for (Block block : this._kitBlocks.keySet())
      block.setType((Material)this._kitBlocks.get(block));
    this._kitBlocks.clear();
    
    if ((game.GetKits().length <= 1) && (game.GetType() == GameType.UHC))
    {
      WriteKitLine("      ", 0, 159, (byte)15);
      WriteKitLine("      ", 1, 159, (byte)4);
      return;
    }
    

    ArrayList<Kit> kits = new ArrayList();
    for (Kit kit : game.GetKits())
    {
      if (kit.GetAvailability() != KitAvailability.Hide) {
        kits.add(kit);
      }
    }
    
    if ((game.GetType() == GameType.Smash) || (game.GetType() == GameType.SurvivalGames))
    {
      WriteKitLine("Free", 0, 159, (byte)15);
      WriteKitLine("Kits", 1, 159, (byte)4);
      
      Object kitsA = new ArrayList();
      Object kitsB = new ArrayList();
      
      for (int i = 0; i < kits.size(); i++)
      {
        if (((Kit)kits.get(i)).GetAvailability() != KitAvailability.Blue) ((ArrayList)kitsA).add((Kit)kits.get(i)); else {
          ((ArrayList)kitsB).add((Kit)kits.get(i));
        }
      }
      

      double space = 4.0D;
      double offset = (((ArrayList)kitsA).size() - 1) * space / 2.0D;
      
      for (int i = 0; i < ((ArrayList)kitsA).size(); i++)
      {
        Kit kit = (Kit)((ArrayList)kitsA).get(i);
        
        if (kit.GetAvailability() != KitAvailability.Null)
        {

          Location entLoc = this._kitDisplay.clone().subtract(0.0D, 0.0D, i * space - offset);
          
          byte data = 4;
          if (kit.GetAvailability() == KitAvailability.Green) { data = 5;
          } else if (kit.GetAvailability() == KitAvailability.Blue) data = 3;
          SetKitTeamBlocks(entLoc.clone(), 35, data, this._kitBlocks);
          
          entLoc.add(0.0D, 1.5D, 0.0D);
          
          entLoc.getChunk().load();
          
          Entity ent = kit.SpawnEntity(entLoc);
          
          if (ent != null)
          {

            this._kits.put(ent, new LobbyEnt(ent, entLoc, kit));
          }
        }
      }
      
      double space = 4.0D;
      double offset = (((ArrayList)kitsB).size() - 1) * space / 2.0D;
      
      for (int i = 0; i < ((ArrayList)kitsB).size(); i++)
      {
        Kit kit = (Kit)((ArrayList)kitsB).get(i);
        
        if (kit.GetAvailability() != KitAvailability.Null)
        {

          Location entLoc = this._teamDisplay.clone().subtract(0.0D, 0.0D, i * space - offset);
          
          byte data = 4;
          if (kit.GetAvailability() == KitAvailability.Green) { data = 5;
          } else if (kit.GetAvailability() == KitAvailability.Blue) data = 3;
          SetKitTeamBlocks(entLoc.clone(), 35, data, this._kitBlocks);
          
          entLoc.add(0.0D, 1.5D, 0.0D);
          
          entLoc.getChunk().load();
          
          Entity ent = kit.SpawnEntity(entLoc);
          
          if (ent != null)
          {

            this._kits.put(ent, new LobbyEnt(ent, entLoc, kit));
          }
        }
      }
      return;
    }
    

    double space = 4.0D;
    double offset = (kits.size() - 1) * space / 2.0D;
    
    for (int i = 0; i < kits.size(); i++)
    {
      Kit kit = (Kit)kits.get(i);
      
      if (kit.GetAvailability() != KitAvailability.Null)
      {

        Location entLoc = this._kitDisplay.clone().subtract(0.0D, 0.0D, i * space - offset);
        
        byte data = 4;
        if (kit.GetAvailability() == KitAvailability.Green) { data = 5;
        } else if (kit.GetAvailability() == KitAvailability.Blue) data = 3;
        SetKitTeamBlocks(entLoc.clone(), 35, data, this._kitBlocks);
        
        entLoc.add(0.0D, 1.5D, 0.0D);
        
        entLoc.getChunk().load();
        
        Entity ent = kit.SpawnEntity(entLoc);
        
        if (ent != null)
        {

          this._kits.put(ent, new LobbyEnt(ent, entLoc, kit));
        }
      }
    }
  }
  
  public void SetKitTeamBlocks(Location loc, int id, byte data, NautHashMap<Block, Material> blockMap) {
    Block block = loc.clone().add(0.5D, 0.0D, 0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);
    
    block = loc.clone().add(-0.5D, 0.0D, 0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);
    
    block = loc.clone().add(0.5D, 0.0D, -0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);
    
    block = loc.clone().add(-0.5D, 0.0D, -0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);
    

    block = loc.clone().add(0.5D, 1.0D, 0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);
    
    block = loc.clone().add(-0.5D, 1.0D, 0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);
    
    block = loc.clone().add(0.5D, 1.0D, -0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);
    
    block = loc.clone().add(-0.5D, 1.0D, -0.5D).getBlock();
    blockMap.put(block, block.getType());
    MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);
    

    for (int x = -2; x < 2; x++)
    {
      for (int z = -2; z < 2; z++)
      {
        block = loc.clone().add(x + 0.5D, -1.0D, z + 0.5D).getBlock();
        
        blockMap.put(block, block.getType());
        MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);
      }
    }
    

    for (int x = -3; x < 3; x++)
    {
      for (int z = -3; z < 3; z++)
      {
        block = loc.clone().add(x + 0.5D, -1.0D, z + 0.5D).getBlock();
        
        if (!blockMap.containsKey(block))
        {

          blockMap.put(block, block.getType());
          MapUtil.QuickChangeBlockAt(block.getLocation(), 35, (byte)15);
        }
      }
    }
  }
  
  public void AddKitLocation(Entity ent, Kit kit, Location loc) {
    this._kits.put(ent, new LobbyEnt(ent, loc, kit));
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    this._scoreboardMap.remove(event.getPlayer());
    this._gemMap.remove(event.getPlayer());
    this._kitMap.remove(event.getPlayer());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DamageCancel(CustomDamageEvent event)
  {
    if (this._kits.containsKey(event.GetDamageeEntity())) {
      event.SetCancelled("Kit Cancel");
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() == UpdateType.FAST)
    {
      this.spawn.getWorld().setTime(6000L);
      this.spawn.getWorld().setStorm(false);
      this.spawn.getWorld().setThundering(false);
    }
    

    if (event.getType() == UpdateType.TICK) {
      UpdateEnts();
    }
    if (event.getType() == UpdateType.FASTEST) {
      UpdateFirework();
    }
    if (event.getType() == UpdateType.SEC) {
      RemoveInvalidEnts();
    }
    if (event.getType() == UpdateType.SLOW) {
      UpdateAdvertise();
    }
    ScoreboardDisplay(event);
    ScoreboardSet(event);
  }
  
  private void RemoveInvalidEnts()
  {
    for (Entity ent : UtilWorld.getWorld("world").getEntities())
    {
      if (((ent instanceof org.bukkit.entity.Creature)) || ((ent instanceof Slime)))
      {
        if (!this._kits.containsKey(ent))
        {

          if (!this._teams.containsKey(ent))
          {

            if (ent.getPassenger() == null)
            {

              ent.remove(); } }
        }
      }
    }
  }
  
  private void UpdateAdvertise() {
    if (this.Manager.GetGame() == null) {
      return;
    }
    this._advertiseStage = ((this._advertiseStage + 1) % 2);
    
    if (this.Manager.GetGame().AdvertiseText(this, this._advertiseStage))
    {
      return;
    }
    
    if (this._advertiseStage == 0)
    {
      WriteAdvertiseLine("MINEPLEX ULTRA RANK", 0, 159, (byte)4);
      WriteAdvertiseLine("UNLOCKS EVERYTHING", 1, 159, (byte)15);
      WriteAdvertiseLine("IN EVERY GAME", 2, 159, (byte)15);
      
      WriteAdvertiseLine("www.mineplex.com", 4, 159, (byte)15);
    }
    else if (this._advertiseStage == 1)
    {
      WriteAdvertiseLine("KEEP CALM", 0, 159, (byte)4);
      WriteAdvertiseLine("AND", 1, 159, (byte)15);
      WriteAdvertiseLine("PLAY MINEPLEX", 2, 159, (byte)4);
      
      WriteAdvertiseLine("www.mineplex.com", 4, 159, (byte)15);
    }
  }
  



  public void UpdateEnts()
  {
    for (Entity ent : this._kits.keySet()) {
      ent.teleport(((LobbyEnt)this._kits.get(ent)).GetLocation());
    }
    for (Entity ent : this._teams.keySet()) {
      ent.teleport(((LobbyEnt)this._teams.get(ent)).GetLocation());
    }
  }
  
  public Kit GetClickedKit(Entity clicked) {
    for (LobbyEnt ent : this._kits.values()) {
      if (clicked.equals(ent.GetEnt()))
        return ent.GetKit();
    }
    return null;
  }
  
  public GameTeam GetClickedTeam(Entity clicked)
  {
    for (LobbyEnt ent : this._teams.values()) {
      if (clicked.equals(ent.GetEnt()))
        return ent.GetTeam();
    }
    return null;
  }
  
  public void RegisterFireworks(GameTeam winnerTeam)
  {
    if (winnerTeam != null)
    {
      this._fireworkColor = Color.GREEN;
      if (winnerTeam.GetColor() == ChatColor.RED) this._fireworkColor = Color.RED;
      if (winnerTeam.GetColor() == ChatColor.AQUA) this._fireworkColor = Color.BLUE;
      if (winnerTeam.GetColor() == ChatColor.YELLOW) { this._fireworkColor = Color.YELLOW;
      }
      this._fireworkStart = System.currentTimeMillis();
    }
  }
  
  public void UpdateFirework()
  {
    if (mineplex.core.common.util.UtilTime.elapsed(this._fireworkStart, 10000L)) {
      return;
    }
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(this._fireworkColor).with(FireworkEffect.Type.BALL_LARGE).trail(false).build();
    
    try
    {
      this.Manager.GetFirework().playFirework(this.spawn.clone().add(
        Math.random() * 160.0D - 80.0D, 30.0D + Math.random() * 10.0D, Math.random() * 160.0D - 80.0D), effect);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @EventHandler
  public void Combust(EntityCombustEvent event)
  {
    for (LobbyEnt ent : this._kits.values()) {
      if (event.getEntity().equals(ent.GetEnt()))
      {
        event.setCancelled(true);
        return;
      }
    }
  }
  
  public void DisplayLast(Game game)
  {
    RegisterFireworks(game.WinnerTeam);
  }
  
  public void DisplayNext(Game game, HashMap<String, ChatColor> pastTeams)
  {
    WriteGameLine(game.GetType().GetLobbyName(), 0, 159, (byte)14);
    
    if (game.GetMode() == null) {
      WriteGameLine("      ", 1, 159, (byte)14);
    } else {
      WriteGameLine(game.GetMode(), 1, 159, (byte)14);
    }
    
    DisplayWaiting();
    
    CreateKits(game);
    CreateTeams(game);
  }
  
  public void DisplayWaiting()
  {
    WriteGameLine("waiting for players", 3, 159, (byte)13);
  }
  
  @EventHandler
  public void ScoreboardDisplay(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if ((this.Manager.GetGame() != null) && 
      (this.Manager.GetGame().GetState() != Game.GameState.Loading) && 
      (this.Manager.GetGame().GetState() != Game.GameState.Recruit))
    {
      for (Player player : UtilServer.getPlayers()) {
        player.setScoreboard(this.Manager.GetGame().GetScoreboard());
      }
      
    }
    else {
      for (Player player : UtilServer.getPlayers())
      {
        if (!HasScoreboard(player))
        {
          CreateScoreboard(player);
        }
        else
        {
          player.setScoreboard((Scoreboard)this._scoreboardMap.get(player));
        }
      }
    }
  }
  
  @EventHandler
  public void ScoreboardSet(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if ((this.Manager.GetGame() != null) && (!this.Manager.GetGame().DisplayLobbySide))
    {
      return;
    }
    
    for (Map.Entry<Player, Scoreboard> entry : this._scoreboardMap.entrySet())
    {
      Objective objective = ((Scoreboard)entry.getValue()).getObjective("§lLobby");
      
      if ((this.Manager.GetGame() != null) && (this.Manager.GetGame().GetCountdown() >= 0))
      {
        if (this.Manager.GetGame().GetCountdown() > 0) {
          objective.setDisplayName(C.Bold + "§lStarting in " + C.cGreen + "§l" + this.Manager.GetGame().GetCountdown() + (this.Manager.GetGame().GetCountdown() == 1 ? " Second" : " Seconds"));
        } else if (this.Manager.GetGame().GetCountdown() == 0) {
          objective.setDisplayName(ChatColor.WHITE + "§lIn Progress...");
        }
      }
      else {
        objective.setDisplayName(ChatColor.GREEN + "§l" + "Waiting for Players");
      }
      
      int line = 14;
      
      objective.getScore(C.cYellow + "Max Players").setScore(line--);
      objective.getScore(this.Manager.GetPlayerFull() + " ").setScore(line--);
      objective.getScore(" ").setScore(line--);
      objective.getScore(C.cYellow + "Min Players").setScore(line--);
      objective.getScore(this.Manager.GetPlayerMin() + "  ").setScore(line--);
      objective.getScore("   ").setScore(line--);
      objective.getScore(C.cYellow + "Players").setScore(line--);
      

      ((Scoreboard)entry.getValue()).resetScores(this._oldPlayerCount + "   ");
      
      objective.getScore(UtilServer.getPlayers().length + "   ").setScore(line--);
      
      if (this.Manager.GetGame() != null)
      {
        ChatColor teamColor = ChatColor.GRAY;
        String kitName = "None";
        
        if (this.Manager.GetGame().GetTeam((Player)entry.getKey()) != null)
        {
          teamColor = this.Manager.GetGame().GetTeam((Player)entry.getKey()).GetColor();
        }
        
        if (this.Manager.GetGame().GetKit((Player)entry.getKey()) != null)
        {
          kitName = this.Manager.GetGame().GetKit((Player)entry.getKey()).GetName();
        }
        
        if (teamColor == null)
        {

          if (kitName.length() > 16) {
            kitName = kitName.substring(0, 16);
          }
        }
        ((Scoreboard)entry.getValue()).resetScores(C.cGray + C.Bold + "Kit");
        ((Scoreboard)entry.getValue()).resetScores((String)this._kitMap.get((Player)entry.getKey()));
        

        objective.getScore("    ").setScore(line--);
        objective.getScore(teamColor + C.Bold + "Kit").setScore(line--);
        objective.getScore(kitName).setScore(line--);
        
        this._kitMap.put((Player)entry.getKey(), kitName);
      }
      
      objective.getScore("     ").setScore(line--);
      objective.getScore(C.cGreen + C.Bold + "Gems").setScore(line--);
      

      ((Scoreboard)entry.getValue()).resetScores(this._gemMap.get((Player)entry.getKey()) + "     ");
      
      objective.getScore(this.Manager.GetDonation().Get(((Player)entry.getKey()).getName()).GetGems() + "     ").setScore(line--);
      
      this._gemMap.put((Player)entry.getKey(), Integer.valueOf(this.Manager.GetDonation().Get(((Player)entry.getKey()).getName()).GetGems()));
    }
    
    this._oldPlayerCount = UtilServer.getPlayers().length;
  }
  
  private String GetKitCustomName(Player player, Game game, LobbyEnt ent)
  {
    CoreClient client = this.Manager.GetClients().Get(player);
    Donor donor = this.Manager.GetDonation().Get(player.getName());
    
    String entityName = ent.GetKit().GetName();
    
    if ((!player.isOnline()) || (client == null) || (donor == null)) {
      return entityName;
    }
    if (client.GetRank() == null)
    {
      System.out.println("client rank is null");
    }
    
    if (game == null)
    {
      System.out.println("game is null");
    }
    
    if (this.Manager == null)
    {
      System.out.println("Manager is null");
    }
    
    if (this.Manager.GetServerConfig() == null)
    {
      System.out.println("Manager.GetServerConfig() is null");
    }
    
    if ((client.GetRank().Has(Rank.ULTRA)) || (donor.OwnsUnknownPackage(game.GetName() + " " + ent.GetKit().GetName())) || (donor.OwnsUnknownPackage(this.Manager.GetServerConfig().ServerType + " ULTRA")) || (ent.GetKit().GetAvailability() == KitAvailability.Free))
    {
      entityName = ent.GetKit().GetAvailability().GetColor() + entityName;
    }
    else
    {
      entityName = ChatColor.RED + C.Bold + entityName;
      
      if (ent.GetKit().GetAvailability() != KitAvailability.Blue) {
        entityName = entityName + ChatColor.RESET + " " + ChatColor.WHITE + C.Line + ent.GetKit().GetCost() + " Gems";
      } else {
        entityName = entityName + ChatColor.RESET + " " + ChatColor.WHITE + C.Line + "Ultra";
      }
    }
    return entityName;
  }
  


  public boolean run(Packet packet, Player owner, PacketVerifier packetList)
  {
    int entityId = -1;
    
    if ((packet instanceof PacketPlayOutEntityMetadata))
    {
      entityId = ((PacketPlayOutEntityMetadata)packet).a;
    } else {
      (packet instanceof PacketPlayOutSpawnEntityLiving);
    }
    


    if (entityId != -1)
    {
      String customName = null;
      

      for (LobbyEnt ent : this._kits.values())
      {
        if ((ent.GetEnt().getEntityId() == entityId) && (this.Manager.GetGame() != null))
        {
          customName = GetKitCustomName(owner, this.Manager.GetGame(), ent);
          break;
        }
      }
      
      if (customName != null)
      {
        try
        {
          if ((packet instanceof PacketPlayOutEntityMetadata))
          {
            List<WatchableObject> watchables = new ArrayList();
            
            for (WatchableObject watchableObject : ((PacketPlayOutEntityMetadata)packet).b)
            {
              WatchableObject newWatch = new WatchableObject(watchableObject.c(), watchableObject.a(), watchableObject.b());
              
              if (newWatch.a() == 10)
              {
                newWatch.a(customName);
              }
              
              watchables.add(newWatch);
            }
            
            PacketPlayOutEntityMetadata newPacket = new PacketPlayOutEntityMetadata();
            newPacket.a = entityId;
            newPacket.b = watchables;
            
            packetList.forceProcess(newPacket);
            
            return false;
          }
          if ((packet instanceof PacketPlayOutSpawnEntityLiving))
          {
            DataWatcher watcher = ((PacketPlayOutSpawnEntityLiving)packet).l;
            watcher.watch(10, customName);
            watcher.watch(11, Byte.valueOf((byte)1));
          }
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    return true;
  }
  
  public void AddPlayerToScoreboards(Player player, String teamName) {
    Iterator localIterator2;
    for (Iterator localIterator1 = GetScoreboards().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Scoreboard scoreboard = (Scoreboard)localIterator1.next();
      
      localIterator2 = scoreboard.getTeams().iterator(); continue;Team team = (Team)localIterator2.next();
      team.removePlayer(player);
    }
    
    if (teamName == null) {
      teamName = "";
    }
    for (Scoreboard scoreboard : GetScoreboards())
    {
      String rankName = this.Manager.GetClients().Get(player).GetRank().Name;
      
      if ((!this.Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA)) && (this.Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(this.Manager.GetServerConfig().ServerType + " ULTRA")))
      {
        rankName = Rank.ULTRA.Name;
      }
      
      try
      {
        scoreboard.getTeam(rankName + teamName).addPlayer(player);
      }
      catch (Exception e)
      {
        System.out.println("GameLobbyManager AddPlayerToScoreboard Error");
        System.out.println("[" + rankName + teamName + "] adding [" + player.getName() + "]");
        System.out.println("Team is Null [" + (scoreboard.getTeam(rankName + teamName) == null) + "]");
      }
    }
  }
}
