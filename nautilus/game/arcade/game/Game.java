package nautilus.game.arcade.game;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.chat.Chat;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.DeathMessageType;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.PlayerGameRespawnEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.managers.GameLobbyManager;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public abstract class Game implements org.bukkit.event.Listener
{
  public ArcadeManager Manager;
  private GameType _gameType;
  private String[] _gameDesc;
  private ArrayList<String> _files;
  
  public static enum GameState
  {
    Loading, 
    Recruit, 
    Prepare, 
    Live, 
    End, 
    Dead;
  }
  










  private GameState _gameState = GameState.Loading;
  private long _gameStateTime = System.currentTimeMillis();
  
  private boolean _prepareCountdown = false;
  
  private int _countdown = -1;
  private boolean _countdownForce = false;
  
  private String _customWinLine = "";
  

  private Kit[] _kits;
  

  private ArrayList<GameTeam> _teamList = new ArrayList();
  

  protected NautHashMap<Player, Kit> _playerKit = new NautHashMap();
  private NautHashMap<GameTeam, ArrayList<Player>> _teamPreference = new NautHashMap();
  private NautHashMap<Player, HashMap<String, GemData>> _gemCount = new NautHashMap();
  

  private NautHashMap<String, Location> _playerLocationStore = new NautHashMap();
  

  private Scoreboard _scoreboard;
  
  private Objective _sideObjective;
  
  public WorldData WorldData = null;
  

  private long _helpTimer = 0L;
  private int _helpIndex = 0;
  private ChatColor _helpColor = ChatColor.YELLOW;
  
  protected String[] _help;
  
  public boolean Damage = true;
  public boolean DamagePvP = true;
  public boolean DamagePvE = true;
  public boolean DamageEvP = true;
  public boolean DamageSelf = true;
  public boolean DamageTeamSelf = false;
  public boolean DamageTeamOther = true;
  
  public boolean BlockBreak = false;
  public HashSet<Integer> BlockBreakAllow = new HashSet();
  public HashSet<Integer> BlockBreakDeny = new HashSet();
  
  public boolean BlockPlace = false;
  public HashSet<Integer> BlockPlaceAllow = new HashSet();
  public HashSet<Integer> BlockPlaceDeny = new HashSet();
  
  public boolean ItemPickup = false;
  public HashSet<Integer> ItemPickupAllow = new HashSet();
  public HashSet<Integer> ItemPickupDeny = new HashSet();
  
  public boolean ItemDrop = false;
  public HashSet<Integer> ItemDropAllow = new HashSet();
  public HashSet<Integer> ItemDropDeny = new HashSet();
  
  public boolean InventoryOpen = false;
  
  public boolean PrivateBlocks = false;
  
  public boolean DeathOut = true;
  public boolean DeathDropItems = false;
  public boolean DeathMessages = true;
  public double DeathSpectateSecs = 0.0D;
  
  public boolean QuitOut = true;
  
  public boolean IdleKick = true;
  
  public boolean CreatureAllow = false;
  public boolean CreatureAllowOverride = false;
  
  public int WorldTimeSet = -1;
  public boolean WorldWeatherEnabled = false;
  public int WorldWaterDamage = 0;
  public boolean WorldBoundaryKill = true;
  
  public int HungerSet = -1;
  public int HealthSet = -1;
  
  public int SpawnDistanceRequirement = 1;
  
  public boolean PrepareFreeze = true;
  
  public boolean RepairWeapons = true;
  
  public boolean AutoBalance = true;
  
  public boolean AnnounceStay = true;
  public boolean AnnounceJoinQuit = true;
  public boolean AnnounceSilence = true;
  
  public boolean DisplayLobbySide = true;
  
  public boolean AutoStart = true;
  
  public GameState KitRegisterState = GameState.Live;
  

  public boolean CompassEnabled = false;
  public boolean SoupEnabled = true;
  
  public boolean GiveClock = true;
  
  public double GemMultiplier = 1.0D;
  

  public HashMap<Location, Player> PrivateBlockMap = new HashMap();
  public HashMap<String, Integer> PrivateBlockCount = new HashMap();
  
  public Location SpectatorSpawn = null;
  
  public boolean FirstKill = true;
  
  public String Winner = "Nobody";
  public GameTeam WinnerTeam = null;
  
  public Game(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc)
  {
    this.Manager = manager;
    

    this._gameType = gameType;
    this._gameDesc = gameDesc;
    

    this._kits = kits;
    

    this._scoreboard = org.bukkit.Bukkit.getScoreboardManager().getNewScoreboard();
    
    this._sideObjective = this._scoreboard.registerNewObjective("Obj" + mineplex.core.common.util.UtilMath.r(999999999), "dummy");
    this._sideObjective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
    this._sideObjective.setDisplayName(C.Bold + GetName());
    

    this._files = this.Manager.LoadFiles(GetName());
    this.WorldData = new WorldData(this);
    
    System.out.println("Loading " + GetName() + "...");
  }
  
  public ArrayList<String> GetFiles()
  {
    return this._files;
  }
  
  public String GetName()
  {
    return this._gameType.GetName();
  }
  
  public String GetMode()
  {
    return null;
  }
  
  public GameType GetType()
  {
    return this._gameType;
  }
  
  public String[] GetDesc()
  {
    return this._gameDesc;
  }
  
  public void SetCustomWinLine(String line)
  {
    this._customWinLine = line;
  }
  
  public Scoreboard GetScoreboard()
  {
    return this._scoreboard;
  }
  
  public Objective GetObjectiveSide()
  {
    return this._sideObjective;
  }
  
  public ArrayList<GameTeam> GetTeamList()
  {
    return this._teamList;
  }
  
  public int GetCountdown()
  {
    return this._countdown;
  }
  
  public void SetCountdown(int time)
  {
    this._countdown = time;
  }
  
  public boolean GetCountdownForce()
  {
    return this._countdownForce;
  }
  
  public void SetCountdownForce(boolean value)
  {
    this._countdownForce = value;
  }
  
  public NautHashMap<GameTeam, ArrayList<Player>> GetTeamPreferences()
  {
    return this._teamPreference;
  }
  
  public NautHashMap<Player, Kit> GetPlayerKits()
  {
    return this._playerKit;
  }
  
  public NautHashMap<Player, HashMap<String, GemData>> GetPlayerGems()
  {
    return this._gemCount;
  }
  
  public NautHashMap<String, Location> GetLocationStore()
  {
    return this._playerLocationStore;
  }
  
  public GameState GetState()
  {
    return this._gameState;
  }
  
  public void SetState(GameState state)
  {
    this._gameState = state;
    this._gameStateTime = System.currentTimeMillis();
    
    for (Player player : UtilServer.getPlayers()) {
      player.leaveVehicle();
    }
    
    nautilus.game.arcade.events.GameStateChangeEvent stateEvent = new nautilus.game.arcade.events.GameStateChangeEvent(this, state);
    UtilServer.getServer().getPluginManager().callEvent(stateEvent);
    
    System.out.println(GetName() + " state set to " + state.toString());
  }
  
  public long GetStateTime()
  {
    return this._gameStateTime;
  }
  
  public boolean InProgress()
  {
    return (GetState() == GameState.Prepare) || (GetState() == GameState.Live);
  }
  
  public boolean IsLive()
  {
    return this._gameState == GameState.Live;
  }
  

  public void AddTeam(GameTeam team)
  {
    GetTeamList().add(team);
    

    team.SetSpawnRequirement(this.SpawnDistanceRequirement);
    


    System.out.println("Created Team: " + team.GetName());
  }
  
  public boolean HasTeam(GameTeam team)
  {
    for (GameTeam cur : GetTeamList()) {
      if (cur.equals(team))
        return true;
    }
    return false;
  }
  
  public void CreateScoreboardTeams()
  {
    System.out.println("Creating Scoreboard Teams.");
    

    for (Rank rank : Rank.values())
    {

      if (rank == Rank.ALL)
      {
        this._scoreboard.registerNewTeam(rank.Name + "SPEC").setPrefix(ChatColor.GRAY);
      }
      else
      {
        this._scoreboard.registerNewTeam(rank.Name + "SPEC").setPrefix(ChatColor.GRAY);
      }
    }
    
    int m;
    int k;
    for (Iterator localIterator = GetTeamList().iterator(); localIterator.hasNext(); 
        
        k < m)
    {
      GameTeam team = (GameTeam)localIterator.next();
      Rank[] arrayOfRank2;
      m = (arrayOfRank2 = Rank.values()).length;k = 0; continue;Rank rank = arrayOfRank2[k];
      
      if (rank == Rank.ALL)
      {
        this._scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(team.GetColor());

      }
      else
      {
        this._scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(team.GetColor());
      }
      k++;
    }
  }
  







  public void RestrictKits() {}
  







  public void RegisterKits()
  {
    for (Kit kit : this._kits)
    {
      UtilServer.getServer().getPluginManager().registerEvents(kit, this.Manager.GetPlugin());
      
      for (Perk perk : kit.GetPerks()) {
        UtilServer.getServer().getPluginManager().registerEvents(perk, this.Manager.GetPlugin());
      }
    }
  }
  
  public void DeregisterKits()
  {
    for (Kit kit : this._kits)
    {
      HandlerList.unregisterAll(kit);
      
      for (Perk perk : kit.GetPerks()) {
        HandlerList.unregisterAll(perk);
      }
    }
  }
  



  public void ParseData() {}
  



  public void SetPlayerTeam(Player player, GameTeam team)
  {
    GameTeam pastTeam = GetTeam(player);
    if (pastTeam != null)
    {
      pastTeam.RemovePlayer(player);
    }
    
    team.AddPlayer(player);
    

    ValidateKit(player, team);
    

    SetPlayerScoreboardTeam(player, team.GetName().toUpperCase());
    

    this.Manager.GetLobby().AddPlayerToScoreboards(player, team.GetName().toUpperCase());
  }
  
  public void SetPlayerScoreboardTeam(Player player, String teamName)
  {
    for (Team team : GetScoreboard().getTeams()) {
      team.removePlayer(player);
    }
    if (teamName == null) {
      teamName = "";
    }
    GetScoreboard().getTeam(this.Manager.GetClients().Get(player).GetRank().Name + teamName).addPlayer(player);
  }
  
  public GameTeam ChooseTeam(Player player)
  {
    GameTeam team = null;
    

    for (int i = 0; i < this._teamList.size(); i++)
    {
      if ((team == null) || (((GameTeam)this._teamList.get(i)).GetSize() < team.GetSize()))
      {
        team = (GameTeam)this._teamList.get(i);
      }
    }
    
    return team;
  }
  
  public double GetKillsGems(Player killer, Player killed, boolean assist)
  {
    if (!this.DeathOut)
    {
      return 0.5D;
    }
    
    if (!assist)
    {
      return 4.0D;
    }
    

    return 1.0D;
  }
  

  public HashMap<String, GemData> GetGems(Player player)
  {
    if (!this._gemCount.containsKey(player)) {
      this._gemCount.put(player, new HashMap());
    }
    return (HashMap)this._gemCount.get(player);
  }
  
  public void AddGems(Player player, double gems, String reason, boolean countAmount)
  {
    if ((!countAmount) && (gems < 1.0D)) {
      gems = 1.0D;
    }
    if (GetGems(player).containsKey(reason))
    {
      ((GemData)GetGems(player).get(reason)).AddGems(gems);
    }
    else
    {
      GetGems(player).put(reason, new GemData(gems, countAmount));
    }
  }
  

  public void ValidateKit(Player player, GameTeam team)
  {
    if ((GetKit(player) == null) || (!team.KitAllowed(GetKit(player))))
    {
      for (Kit kit : this._kits)
      {
        if ((kit.GetAvailability() != KitAvailability.Hide) && 
          (kit.GetAvailability() != KitAvailability.Null))
        {

          if (team.KitAllowed(kit))
          {
            SetKit(player, kit, false);
            break;
          }
        }
      }
    }
  }
  
  public void SetKit(Player player, Kit kit, boolean announce) {
    GameTeam team = GetTeam(player);
    if (team != null)
    {
      if (!team.KitAllowed(kit))
      {
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 2.0F, 0.5F);
        UtilPlayer.message(player, F.main("Kit", F.elem(team.GetFormattedName()) + " cannot use " + F.elem(new StringBuilder(String.valueOf(kit.GetFormattedName())).append(" Kit").toString()) + "."));
        return;
      }
    }
    
    if (this._playerKit.get(player) != null)
    {
      ((Kit)this._playerKit.get(player)).Deselected(player);
    }
    
    this._playerKit.put(player, kit);
    
    kit.Selected(player);
    
    if (announce)
    {
      player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2.0F, 1.0F);
      UtilPlayer.message(player, F.main("Kit", "You equipped " + F.elem(new StringBuilder(String.valueOf(kit.GetFormattedName())).append(" Kit").toString()) + "."));
    }
    
    if (InProgress()) {
      kit.ApplyKit(player);
    }
  }
  
  public Kit GetKit(Player player) {
    return (Kit)this._playerKit.get(player);
  }
  
  public Kit[] GetKits()
  {
    return this._kits;
  }
  
  public boolean HasKit(Kit kit)
  {
    for (Kit cur : GetKits()) {
      if (cur.equals(kit))
        return true;
    }
    return false;
  }
  
  public boolean HasKit(Player player, Kit kit)
  {
    if (!IsAlive(player)) {
      return false;
    }
    if (GetKit(player) == null) {
      return false;
    }
    return GetKit(player).equals(kit);
  }
  
  public boolean SetPlayerState(Player player, GameTeam.PlayerState state)
  {
    GetScoreboard().resetScores(player.getName());
    
    GameTeam team = GetTeam(player);
    
    if (team == null) {
      return false;
    }
    team.SetPlayerState(player, state);
    

    nautilus.game.arcade.events.PlayerStateChangeEvent playerStateEvent = new nautilus.game.arcade.events.PlayerStateChangeEvent(this, player, GameTeam.PlayerState.OUT);
    UtilServer.getServer().getPluginManager().callEvent(playerStateEvent);
    
    return true;
  }
  
  public abstract void EndCheck();
  
  public void RespawnPlayer(final Player player)
  {
    player.eject();
    player.teleport(GetTeam(player).GetSpawn());
    
    this.Manager.Clear(player);
    

    PlayerGameRespawnEvent event = new PlayerGameRespawnEvent(this, player);
    UtilServer.getServer().getPluginManager().callEvent(event);
    

    this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        Game.this.GetKit(player).ApplyKit(player);
      }
    }, 0L);
  }
  
  public boolean IsPlaying(Player player)
  {
    return GetTeam(player) != null;
  }
  
  public boolean IsAlive(Player player)
  {
    GameTeam team = GetTeam(player);
    
    if (team == null) {
      return false;
    }
    return team.IsAlive(player);
  }
  
  public ArrayList<Player> GetPlayers(boolean aliveOnly)
  {
    ArrayList<Player> players = new ArrayList();
    
    for (GameTeam team : this._teamList) {
      players.addAll(team.GetPlayers(aliveOnly));
    }
    return players;
  }
  
  public GameTeam GetTeam(String player, boolean aliveOnly)
  {
    for (GameTeam team : this._teamList) {
      if (team.HasPlayer(player, aliveOnly))
        return team;
    }
    return null;
  }
  
  public GameTeam GetTeam(Player player)
  {
    for (GameTeam team : this._teamList) {
      if (team.HasPlayer(player))
        return team;
    }
    return null;
  }
  
  public GameTeam GetTeam(ChatColor color)
  {
    for (GameTeam team : this._teamList) {
      if (team.GetColor() == color)
        return team;
    }
    return null;
  }
  
  public Location GetSpectatorLocation()
  {
    if (this.SpectatorSpawn != null) {
      return this.SpectatorSpawn;
    }
    Vector vec = new Vector(0, 0, 0);
    double count = 0.0D;
    Iterator localIterator2;
    for (Iterator localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
      localIterator2 = team.GetSpawns().iterator(); continue;Location spawn = (Location)localIterator2.next();
      
      count += 1.0D;
      vec.add(spawn.toVector());
    }
    

    this.SpectatorSpawn = new Location(this.WorldData.World, 0.0D, 0.0D, 0.0D);
    
    vec.multiply(1.0D / count);
    
    this.SpectatorSpawn.setX(vec.getX());
    this.SpectatorSpawn.setY(vec.getY());
    this.SpectatorSpawn.setZ(vec.getZ());
    

    while ((!UtilBlock.airFoliage(this.SpectatorSpawn.getBlock())) || (!UtilBlock.airFoliage(this.SpectatorSpawn.getBlock().getRelative(BlockFace.UP))))
    {
      this.SpectatorSpawn.add(0.0D, 1.0D, 0.0D);
    }
    
    int Up = 0;
    

    for (int i = 0; i < 15; i++)
    {
      if (!UtilBlock.airFoliage(this.SpectatorSpawn.getBlock().getRelative(BlockFace.UP)))
        break;
      this.SpectatorSpawn.add(0.0D, 1.0D, 0.0D);
      Up++;
    }
    






    while (((Up > 0) && (!UtilBlock.airFoliage(this.SpectatorSpawn.getBlock()))) || (!UtilBlock.airFoliage(this.SpectatorSpawn.getBlock().getRelative(BlockFace.UP))))
    {
      this.SpectatorSpawn.subtract(0.0D, 1.0D, 0.0D);
      Up--;
    }
    
    this.SpectatorSpawn = this.SpectatorSpawn.getBlock().getLocation().add(0.5D, 0.1D, 0.5D);
    
    while ((this.SpectatorSpawn.getBlock().getTypeId() != 0) || (this.SpectatorSpawn.getBlock().getRelative(BlockFace.UP).getTypeId() != 0)) {
      this.SpectatorSpawn.add(0.0D, 1.0D, 0.0D);
    }
    return this.SpectatorSpawn;
  }
  
  public void SetSpectator(Player player)
  {
    this.Manager.Clear(player);
    
    player.teleport(GetSpectatorLocation());
    player.setGameMode(org.bukkit.GameMode.CREATIVE);
    player.setFlying(true);
    player.setFlySpeed(0.1F);
    ((CraftPlayer)player).getHandle().spectating = true;
    ((CraftPlayer)player).getHandle().k = false;
    
    this.Manager.GetCondition().Factory().Cloak("Spectator", player, player, 7777.0D, true, true);
    
    if ((GetTeam(player) != null) && (this._scoreboard.getTeam(GetTeam(player).GetName().toUpperCase()) != null))
    {
      this._scoreboard.getTeam(GetTeam(player).GetName().toUpperCase()).removePlayer(player);
    }
    
    SetPlayerScoreboardTeam(player, "SPEC");
  }
  
  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (GameTeam team : GetTeamList())
    {
      String name = team.GetColor() + team.GetName();
      if (name.length() > 16) {
        name = name.substring(0, 16);
      }
      Score score = GetObjectiveSide().getScore(name);
      score.setScore(team.GetPlayers(true).size());
    }
  }
  
  public DeathMessageType GetDeathMessageType()
  {
    if (!this.DeathMessages) {
      return DeathMessageType.None;
    }
    if (this.DeathOut) {
      return DeathMessageType.Detailed;
    }
    return DeathMessageType.Simple;
  }
  
  public boolean CanJoinTeam(GameTeam team)
  {
    return team.GetSize() < Math.max(1, UtilServer.getPlayers().length / GetTeamList().size());
  }
  
  public GameTeam GetTeamPreference(Player player)
  {
    for (GameTeam team : this._teamPreference.keySet())
    {
      if (((ArrayList)this._teamPreference.get(team)).contains(player)) {
        return team;
      }
    }
    return null;
  }
  
  public void RemoveTeamPreference(Player player)
  {
    for (ArrayList<Player> queue : this._teamPreference.values())
      queue.remove(player);
  }
  
  public String GetTeamQueuePosition(Player player) { ArrayList<Player> queue;
    int i;
    for (Iterator localIterator = this._teamPreference.values().iterator(); localIterator.hasNext(); 
        
        i < queue.size())
    {
      queue = (ArrayList)localIterator.next();
      
      i = 0; continue;
      
      if (((Player)queue.get(i)).equals(player)) {
        return i + 1 + "/" + queue.size();
      }
      i++;
    }
    




    return "Unknown";
  }
  
  public void InformQueuePositions() {
    Iterator localIterator2;
    for (Iterator localIterator1 = this._teamPreference.keySet().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
      localIterator2 = ((ArrayList)this._teamPreference.get(team)).iterator(); continue;Player player = (Player)localIterator2.next();
      
      UtilPlayer.message(player, F.main("Team", "You are " + F.elem(GetTeamQueuePosition(player)) + " in queue for " + F.elem(new StringBuilder(String.valueOf(team.GetFormattedName())).append(" Team").toString()) + "."));
    }
  }
  

  public void AnnounceGame()
  {
    for (Player player : ) {
      AnnounceGame(player);
    }
    if (this.AnnounceSilence) {
      this.Manager.GetChat().Silence(9000L, false);
    }
  }
  
  public void AnnounceGame(Player player) {
    player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
    
    for (int i = 0; i < 6 - GetDesc().length; i++) {
      UtilPlayer.message(player, "");
    }
    UtilPlayer.message(player, ArcadeFormat.Line);
    
    UtilPlayer.message(player, C.cGreen + "Game - " + C.cYellow + C.Bold + GetName());
    UtilPlayer.message(player, "");
    
    for (String line : GetDesc())
    {
      UtilPlayer.message(player, C.cWhite + "- " + line);
    }
    
    UtilPlayer.message(player, "");
    UtilPlayer.message(player, C.cGreen + "Map - " + C.cYellow + C.Bold + this.WorldData.MapName + ChatColor.RESET + C.cGray + " created by " + C.cYellow + C.Bold + this.WorldData.MapAuthor);
    
    UtilPlayer.message(player, ArcadeFormat.Line);
  }
  
  public void AnnounceEnd(GameTeam team)
  {
    if (!IsLive()) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, ArcadeFormat.Line);
      
      UtilPlayer.message(player, "§aGame - §f§l" + GetName());
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "");
      
      if (team != null)
      {
        this.WinnerTeam = team;
        this.Winner = (team.GetName() + " Team");
        UtilPlayer.message(player, team.GetColor() + C.Bold + team.GetName() + " won the game!");
      }
      else
      {
        UtilPlayer.message(player, ChatColor.WHITE + "§lNobody won the game...");
      }
      

      UtilPlayer.message(player, this._customWinLine);
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "§aMap - §f§l" + this.WorldData.MapName + C.cGray + " created by " + "§f§l" + this.WorldData.MapAuthor);
      
      UtilPlayer.message(player, ArcadeFormat.Line);
    }
    
    if (this.AnnounceSilence) {
      this.Manager.GetChat().Silence(5000L, false);
    }
  }
  
  public void AnnounceEnd(ArrayList<Player> places) {
    for (Player player : )
    {
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, ArcadeFormat.Line);
      
      UtilPlayer.message(player, "§aGame - §f§l" + GetName());
      UtilPlayer.message(player, "");
      
      if ((places == null) || (places.isEmpty()))
      {
        UtilPlayer.message(player, "");
        UtilPlayer.message(player, ChatColor.WHITE + "§lNobody won the game...");
        UtilPlayer.message(player, "");
      }
      else
      {
        if (places.size() >= 1)
        {
          this.Winner = ((Player)places.get(0)).getName();
          UtilPlayer.message(player, C.cRed + C.Bold + "1st Place" + C.cWhite + " - " + ((Player)places.get(0)).getName());
        }
        

        if (places.size() >= 2) {
          UtilPlayer.message(player, C.cGold + C.Bold + "2nd Place" + C.cWhite + " - " + ((Player)places.get(1)).getName());
        }
        if (places.size() >= 3) {
          UtilPlayer.message(player, C.cYellow + C.Bold + "3rd Place" + C.cWhite + " - " + ((Player)places.get(2)).getName());
        }
      }
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "§aMap - §f§l" + this.WorldData.MapName + C.cGray + " created by " + "§f§l" + this.WorldData.MapAuthor);
      
      UtilPlayer.message(player, ArcadeFormat.Line);
    }
    
    if (this.AnnounceSilence) {
      this.Manager.GetChat().Silence(5000L, false);
    }
  }
  
  public void Announce(String message) {
    for (Player player : )
    {
      player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
      
      UtilPlayer.message(player, message);
    }
    
    System.out.println("[Announcement] " + message);
  }
  
  public boolean AdvertiseText(GameLobbyManager gameLobbyManager, int _advertiseStage)
  {
    return false;
  }
  
  public boolean CanThrowTNT(Location location)
  {
    return true;
  }
  
  @EventHandler
  public void HelpUpdate(UpdateEvent event)
  {
    if ((this._help == null) || (this._help.length == 0)) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (GetState() != GameState.Recruit) {
      return;
    }
    if (!mineplex.core.common.util.UtilTime.elapsed(this._helpTimer, 8000L)) {
      return;
    }
    if (this._helpColor == ChatColor.YELLOW) {
      this._helpColor = ChatColor.GREEN;
    } else {
      this._helpColor = ChatColor.YELLOW;
    }
    this._helpTimer = System.currentTimeMillis();
    
    String msg = C.cWhite + C.Bold + "TIP " + ChatColor.RESET + this._helpColor + this._help[this._helpIndex];
    
    for (Player player : UtilServer.getPlayers())
    {
      player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
      
      UtilPlayer.message(player, msg);
    }
    
    this._helpIndex = ((this._helpIndex + 1) % this._help.length);
  }
  
  public void StartPrepareCountdown()
  {
    this._prepareCountdown = true;
  }
  
  public boolean CanStartPrepareCountdown()
  {
    return this._prepareCountdown;
  }
}
