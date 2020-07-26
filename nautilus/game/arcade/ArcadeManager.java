package nautilus.game.arcade;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.blood.Blood;
import mineplex.core.chat.Chat;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.energy.Energy;
import mineplex.core.explosion.Explosion;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.movement.Movement;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.stats.StatsManager;
import mineplex.core.status.ServerStatusManager;
import mineplex.core.teleport.Teleport;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Condition.SkillConditionManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.event.ItemTriggerEvent;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import nautilus.game.arcade.addons.CompassAddon;
import nautilus.game.arcade.command.ParseCommand;
import nautilus.game.arcade.command.WriteCommand;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameServerConfig;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.managers.GameCreationManager;
import nautilus.game.arcade.managers.GameFlagManager;
import nautilus.game.arcade.managers.GameGemManager;
import nautilus.game.arcade.managers.GameLobbyManager;
import nautilus.game.arcade.managers.GameManager;
import nautilus.game.arcade.managers.GamePlayerManager;
import nautilus.game.arcade.managers.GameStatsManager;
import nautilus.game.arcade.managers.GameWorldManager;
import nautilus.game.arcade.managers.MiscManager;
import nautilus.game.arcade.shop.ArcadeShop;
import nautilus.game.arcade.world.FireworkHandler;
import nautilus.game.arcade.world.WorldData;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.PlayerInventory;

public class ArcadeManager extends MiniPlugin implements mineplex.minecraft.game.core.IRelation
{
  private AntiStack _antistack;
  private BlockRestore _blockRestore;
  private Blood _blood;
  private Chat _chat;
  private CoreClientManager _clientManager;
  private DisguiseManager _disguiseManager;
  private DonationManager _donationManager;
  private ConditionManager _conditionManager;
  private Creature _creature;
  private DamageManager _damageManager;
  private Explosion _explosionManager;
  private Fire _fire;
  private FireworkHandler _firework;
  private ProjectileManager _projectileManager;
  private Portal _portal;
  private ArcadeShop _arcadeShop;
  private GameFactory _gameFactory;
  private GameCreationManager _gameCreationManager;
  private GameGemManager _gameGemManager;
  private GameManager _gameManager;
  private GameLobbyManager _gameLobbyManager;
  private GameStatsManager _gameStatsManager;
  private GameWorldManager _gameWorldManager;
  private StatsManager _statsManager;
  private ClassManager _classManager;
  private SkillFactory _skillFactory;
  private ClassShopManager _classShopManager;
  private ClassCombatShop _classShop;
  private MiscManager _miscManager;
  private GameServerConfig _serverConfig;
  private Game _game;
  
  public ArcadeManager(Arcade plugin, ServerStatusManager serverStatusManager, GameServerConfig serverConfig, CoreClientManager clientManager, DonationManager donationManager, DamageManager damageManager, DisguiseManager disguiseManager, Creature creature, Teleport teleport, Blood blood, AntiStack antistack, Portal portal, PacketHandler packetHandler, String webAddress)
  {
    super("Game Manager", plugin);
    
    this._serverConfig = serverConfig;
    

    this._antistack = antistack;
    
    this._blockRestore = new BlockRestore(plugin);
    
    this._blood = blood;
    
    this._explosionManager = new Explosion(plugin, this._blockRestore);
    this._explosionManager.SetDebris(false);
    
    if ((serverConfig.GameList.contains(GameType.ChampionsDominate)) || (serverConfig.GameList.contains(GameType.ChampionsTDM)) || (serverConfig.GameList.contains(GameType.ChampionsMOBA)))
    {
      this._conditionManager = new SkillConditionManager(plugin);
    }
    else
    {
      this._conditionManager = new ConditionManager(plugin);
    }
    
    this._clientManager = clientManager;
    
    this._chat = new Chat(plugin, this._clientManager, serverStatusManager.getCurrentServerName());
    
    this._creature = creature;
    
    this._damageManager = damageManager;
    this._damageManager.UseSimpleWeaponDamage = true;
    
    this._disguiseManager = disguiseManager;
    
    this._donationManager = donationManager;
    
    this._firework = new FireworkHandler();
    this._fire = new Fire(plugin, this._conditionManager, damageManager);
    
    this._projectileManager = new ProjectileManager(plugin);
    
    if ((serverConfig.GameList.contains(GameType.ChampionsDominate)) || (serverConfig.GameList.contains(GameType.ChampionsTDM)) || (serverConfig.GameList.contains(GameType.ChampionsMOBA)))
    {
      Energy energy = new Energy(plugin);
      this._skillFactory = new SkillFactory(plugin, damageManager, this, this._damageManager.GetCombatManager(), this._conditionManager, this._projectileManager, this._blockRestore, this._fire, new Movement(plugin), teleport, energy, webAddress);
      this._classManager = new ClassManager(plugin, clientManager, donationManager, this._skillFactory, webAddress);
      
      this._classShopManager = new ClassShopManager(this._plugin, this._classManager, this._skillFactory, new ItemFactory(this._plugin, this._blockRestore, this._classManager, this._conditionManager, damageManager, energy, this._fire, this._projectileManager, webAddress));
      this._classShop = new ClassCombatShop(this._classShopManager, clientManager, donationManager, webAddress);
    }
    


    this._portal = portal;
    

    this._arcadeShop = new ArcadeShop(this, clientManager, donationManager);
    

    this._gameFactory = new GameFactory(this);
    

    new nautilus.game.arcade.managers.GameChatManager(this);
    this._gameCreationManager = new GameCreationManager(this);
    this._gameGemManager = new GameGemManager(this);
    this._gameManager = new GameManager(this);
    this._gameLobbyManager = new GameLobbyManager(this, packetHandler);
    new GameFlagManager(this);
    new GamePlayerManager(this);
    this._gameStatsManager = new GameStatsManager(this);
    this._gameWorldManager = new GameWorldManager(this);
    this._miscManager = new MiscManager(this);
    new nautilus.game.arcade.managers.IdleManager(this);
    

    new CompassAddon(plugin, this);
    new nautilus.game.arcade.addons.SoupAddon(plugin, this);
  }
  

  public void AddCommands()
  {
    AddCommand(new nautilus.game.arcade.command.GameCommand(this));
    AddCommand(new ParseCommand(this));
    AddCommand(new WriteCommand(this));
  }
  
  public GameServerConfig GetServerConfig()
  {
    return this._serverConfig;
  }
  
  public ArrayList<GameType> GetGameList()
  {
    return GetServerConfig().GameList;
  }
  
  public AntiStack GetAntiStack()
  {
    return this._antistack;
  }
  
  public Blood GetBlood()
  {
    return this._blood;
  }
  
  public Chat GetChat()
  {
    return this._chat;
  }
  
  public BlockRestore GetBlockRestore()
  {
    return this._blockRestore;
  }
  
  public CoreClientManager GetClients()
  {
    return this._clientManager;
  }
  
  public ConditionManager GetCondition()
  {
    return this._conditionManager;
  }
  
  public Creature GetCreature()
  {
    return this._creature;
  }
  
  public DisguiseManager GetDisguise()
  {
    return this._disguiseManager;
  }
  
  public DamageManager GetDamage()
  {
    return this._damageManager;
  }
  
  public DonationManager GetDonation()
  {
    return this._donationManager;
  }
  
  public Explosion GetExplosion()
  {
    return this._explosionManager;
  }
  
  public Fire GetFire()
  {
    return this._fire;
  }
  
  public FireworkHandler GetFirework()
  {
    return this._firework;
  }
  
  public ProjectileManager GetProjectile()
  {
    return this._projectileManager;
  }
  
  public Portal GetPortal()
  {
    return this._portal;
  }
  
  public GameLobbyManager GetLobby()
  {
    return this._gameLobbyManager;
  }
  
  public ArcadeShop GetShop()
  {
    return this._arcadeShop;
  }
  
  public GameStatsManager GetStats()
  {
    return this._gameStatsManager;
  }
  
  public GameCreationManager GetGameCreationManager()
  {
    return this._gameCreationManager;
  }
  
  public GameFactory GetGameFactory()
  {
    return this._gameFactory;
  }
  
  public GameManager GetGameManager()
  {
    return this._gameManager;
  }
  
  public GameGemManager GetGameGemManager()
  {
    return this._gameGemManager;
  }
  
  public GameWorldManager GetGameWorldManager()
  {
    return this._gameWorldManager;
  }
  
  public StatsManager GetStatsManager()
  {
    return this._statsManager;
  }
  
  public ChatColor GetColor(Player player)
  {
    if (this._game == null) {
      return ChatColor.GRAY;
    }
    GameTeam team = this._game.GetTeam(player);
    if (team == null) {
      return ChatColor.GRAY;
    }
    return team.GetColor();
  }
  

  public boolean CanHurt(String a, String b)
  {
    return CanHurt(UtilPlayer.searchExact(a), UtilPlayer.searchExact(b));
  }
  
  public boolean CanHurt(Player pA, Player pB)
  {
    if ((pA == null) || (pB == null)) {
      return false;
    }
    if (!this._game.Damage) {
      return false;
    }
    if (!this._game.DamagePvP) {
      return false;
    }
    
    if (pA.equals(pB)) {
      return this._game.DamageSelf;
    }
    GameTeam tA = this._game.GetTeam(pA);
    if (tA == null) {
      return false;
    }
    GameTeam tB = this._game.GetTeam(pB);
    if (tB == null) {
      return false;
    }
    if ((tA.equals(tB)) && (!this._game.DamageTeamSelf)) {
      return false;
    }
    if ((!tA.equals(tB)) && (!this._game.DamageTeamOther)) {
      return false;
    }
    return true;
  }
  

  public boolean IsSafe(Player player)
  {
    if (this._game == null) {
      return true;
    }
    if (this._game.IsPlaying(player)) {
      return false;
    }
    return true;
  }
  
  @EventHandler
  public void MessageMOTD(ServerListPingEvent event)
  {
    String extrainformation = "|" + this._serverConfig.ServerType + "|" + (this._game == null ? "Unknown" : this._game.GetName()) + "|" + ((this._game == null) || (this._game.WorldData == null) ? "Unknown" : this._game.WorldData.MapName);
    
    if ((this._game == null) || (this._game.GetState() == nautilus.game.arcade.game.Game.GameState.Recruit))
    {
      if ((this._game != null) && (this._game.GetType() == GameType.UHC))
      {
        event.setMotd(ChatColor.RED + "UHC - Season 5");
        return;
      }
      
      if ((this._game != null) && (this._game.GetCountdown() != -1))
      {
        event.setMotd(ChatColor.GREEN + "Starting in " + this._game.GetCountdown() + " Seconds" + extrainformation);
      }
      else
      {
        event.setMotd(ChatColor.GREEN + "Recruiting" + extrainformation);
      }
      
    }
    else
    {
      event.setMotd(ChatColor.YELLOW + "In Progress" + extrainformation);
    }
  }
  
  @EventHandler
  public void MessageJoin(PlayerJoinEvent event)
  {
    if ((this._game == null) || (this._game.AnnounceJoinQuit)) {
      event.setJoinMessage(F.sys("Join", event.getPlayer().getName()));
    } else {
      event.setJoinMessage(null);
    }
  }
  
  @EventHandler
  public void MessageQuit(PlayerQuitEvent event) {
    if ((this._game == null) || (this._game.AnnounceJoinQuit)) {
      event.setQuitMessage(F.sys("Quit", GetColor(event.getPlayer()) + event.getPlayer().getName()));
    } else {
      event.setQuitMessage(null);
    }
  }
  
  public Game GetGame() {
    return this._game;
  }
  
  public void SetGame(Game game)
  {
    this._game = game;
  }
  
  public int GetPlayerMin()
  {
    return GetServerConfig().MinPlayers;
  }
  
  public int GetPlayerFull()
  {
    return GetServerConfig().MaxPlayers;
  }
  
  public void HubClock(Player player)
  {
    if ((this._game != null) && (!this._game.GiveClock)) {
      return;
    }
    player.getInventory().setItem(8, ItemStackFactory.Instance.CreateStack(org.bukkit.Material.WATCH, (byte)0, 1, (short)0, C.cGreen + "Return to Hub", 
      new String[] { "", ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub." }));
  }
  

  @EventHandler
  public void Login(PlayerLoginEvent event)
  {
    if (Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers())
    {
      if ((this._clientManager.Get(event.getPlayer().getName()).GetRank().Has(event.getPlayer(), Rank.ULTRA, false)) || (this._donationManager.Get(event.getPlayer().getName()).OwnsUnknownPackage(this._serverConfig.ServerType + " ULTRA")))
      {
        event.allow();
        event.setResult(PlayerLoginEvent.Result.ALLOWED);
        return;
      }
      
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Full > Purchase Ultra at www.mineplex.com/shop");
    }
  }
  
  public boolean IsAlive(Player player)
  {
    if (this._game == null) {
      return false;
    }
    return this._game.IsAlive(player);
  }
  
  public void Clear(Player player)
  {
    player.setGameMode(GameMode.SURVIVAL);
    player.setAllowFlight(false);
    UtilInv.Clear(player);
    
    ((CraftEntity)player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)0));
    
    player.setSprinting(false);
    
    player.setFoodLevel(20);
    player.setSaturation(3.0F);
    player.setExhaustion(0.0F);
    
    player.setMaxHealth(20.0D);
    player.setHealth(player.getMaxHealth());
    
    player.setFireTicks(0);
    player.setFallDistance(0.0F);
    
    player.setLevel(0);
    player.setExp(0.0F);
    
    ((CraftPlayer)player).getHandle().spectating = false;
    ((CraftPlayer)player).getHandle().k = true;
    

    ((CraftPlayer)player).getHandle().p(0);
    
    GetCondition().EndCondition(player, mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK, null);
    
    HubClock(player);
    
    GetDisguise().undisguise(player);
  }
  
  public ArrayList<String> LoadFiles(String gameName)
  {
    File folder = new File(".." + File.separatorChar + ".." + File.separatorChar + "update" + File.separatorChar + "maps" + File.separatorChar + gameName);
    if (!folder.exists()) { folder.mkdirs();
    }
    ArrayList<String> maps = new ArrayList();
    
    System.out.println("Searching Maps in: " + folder);
    
    for (File file : folder.listFiles())
    {
      if (file.isFile())
      {

        String name = file.getName();
        
        if (name.length() >= 5)
        {

          name = name.substring(name.length() - 4, name.length());
          
          if (!file.getName().equals(".zip"))
          {

            maps.add(file.getName().substring(0, file.getName().length() - 4)); }
        }
      } }
    for (String map : maps) {
      System.out.println("Found Map: " + map);
    }
    return maps;
  }
  
  public ClassManager getClassManager()
  {
    return this._classManager;
  }
  
  public ClassCombatShop getClassShop()
  {
    return this._classShop;
  }
  
  public void openClassShop(Player player)
  {
    this._classShop.attemptShopOpen(player);
  }
  
  @EventHandler
  public void BlockBurn(BlockBurnEvent event)
  {
    if (this._game == null) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void BlockSpread(BlockSpreadEvent event) {
    if (this._game == null) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void BlockFade(BlockFadeEvent event) {
    if (this._game == null) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void BlockDecay(LeavesDecayEvent event) {
    if (this._game == null) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void MobSpawn(CreatureSpawnEvent event) {
    if (this._game == null) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void SkillTrigger(SkillTriggerEvent event) {
    if ((this._game == null) || (!this._game.IsLive()))
    {
      event.SetCancelled(true);
    }
  }
  
  @EventHandler
  public void ItemTrigger(ItemTriggerEvent event)
  {
    if ((this._game == null) || (!this._game.IsLive()))
    {
      event.SetCancelled(true);
    }
  }
}
