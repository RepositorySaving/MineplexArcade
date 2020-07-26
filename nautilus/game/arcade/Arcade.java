

java.io.BufferedReader
java.io.BufferedWriter
java.io.DataInputStream
java.io.File
java.io.FileInputStream
java.io.FileWriter
java.io.InputStreamReader
java.io.PrintStream
java.util.ArrayList
mineplex.core.INautilusPlugin
mineplex.core.account.CoreClientManager
mineplex.core.antistack.AntiStack
mineplex.core.blood.Blood
mineplex.core.command.CommandCenter
mineplex.core.common.util.FileUtil
mineplex.core.common.util.UtilServer
mineplex.core.creature.Creature
mineplex.core.disguise.DisguiseManager
mineplex.core.donation.DonationManager
mineplex.core.itemstack.ItemStackFactory
mineplex.core.memory.MemoryFix
mineplex.core.message.MessageManager
mineplex.core.monitor.LagMeter
mineplex.core.npc.NpcManager
mineplex.core.packethandler.PacketHandler
mineplex.core.portal.Portal
mineplex.core.punish.Punish
mineplex.core.recharge.Recharge
mineplex.core.spawn.Spawn
mineplex.core.status.ServerStatusManager
mineplex.core.teleport.Teleport
mineplex.minecraft.game.core.combat.CombatManager
mineplex.minecraft.game.core.damage.DamageManager
nautilus.game.arcade.game.Game
nautilus.game.arcade.game.GameServerConfig
nautilus.game.arcade.world.WorldData
org.bukkit.Server
org.bukkit.configuration.file.FileConfiguration
org.bukkit.entity.Player
org.bukkit.plugin.PluginManager
org.bukkit.plugin.java.JavaPlugin
org.bukkit.scheduler.BukkitScheduler

Arcade

  WEB_CONFIG = "webServer"
  
  _clientManager
  
  _donationManager
  
  _damageManager
  
  _gameManager
  

  onEnable
  
    DeleteFolders()
    

    getConfig()addDefaultWEB_CONFIG, "http://accounts.mineplex.com/");
    getConfig().set(this.WEB_CONFIG, getConfig().getString(this.WEB_CONFIG));
    saveConfig();
    
    mineplex.core.logger.Logger.initialize(this);
    
    this._clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
    
    CommandCenter.Initialize(this, this._clientManager);
    
    ItemStackFactory.Initialize(this, false);
    Recharge.Initialize(this);
    
    this._donationManager = new DonationManager(this, GetWebServerAddress());
    
    new MessageManager(this, this._clientManager);
    
    AntiStack antistack = new AntiStack(this);
    
    Creature creature = new Creature(this);
    Spawn spawn = new Spawn(this);
    Teleport teleport = new Teleport(this, this._clientManager, spawn);
    new mineplex.core.updater.FileUpdater(this, new Portal(this));
    ServerStatusManager serverStatusManager = new ServerStatusManager(this, new LagMeter(this, this._clientManager));
    
    PacketHandler packetHandler = new PacketHandler(this);
    DisguiseManager disguiseManager = new DisguiseManager(this, packetHandler);
    
    this._damageManager = new DamageManager(this, new CombatManager(this), new NpcManager(this, creature), disguiseManager);
    
    Portal portal = new Portal(this);
    

    this._gameManager = new ArcadeManager(this, serverStatusManager, ReadServerConfig(), this._clientManager, this._donationManager, this._damageManager, disguiseManager, creature, teleport, new Blood(this), antistack, portal, packetHandler, GetWebServerAddress());
    
    Punish punish = new Punish(this, GetWebServerAddress(), this._clientManager);
    mineplex.core.antihack.AntiHack.Initialize(this, punish, portal);
    
    new MemoryFix(this);
    

    getServer().getScheduler().scheduleSyncRepeatingTask(this, new mineplex.core.updater.Updater(this), 1L, 1L);
  }
  


  public void onDisable()
  {
    for (Player player : ) {
      player.kickPlayer("Server Shutdown");
    }
    if ((this._gameManager.GetGame() != null) && 
      (this._gameManager.GetGame().WorldData != null)) {
      this._gameManager.GetGame().WorldData.Uninitialize();
    }
  }
  
  public GameServerConfig ReadServerConfig() {
    GameServerConfig config = new GameServerConfig();
    

    String line = null;
    
    try
    {
      File file = new File("ArcadeSettings.config");
      if (!file.exists()) {
        WriteServerConfig(GetDefaultConfig());
      }
      FileInputStream fstream = new FileInputStream("ArcadeSettings.config");
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      
      while ((line = br.readLine()) != null)
      {
        String[] tokens = line.split("=");
        
        if (tokens.length >= 2)
        {

          if (tokens[0].equals("SERVER_TYPE"))
          {
            config.ServerType = tokens[1];
          }
          else if (tokens[0].equals("PLAYERS_MIN"))
          {
            config.MinPlayers = Integer.parseInt(tokens[1]);
          }
          else if (tokens[0].equals("PLAYERS_MAX"))
          {
            config.MaxPlayers = Integer.parseInt(tokens[1]);
          }
          else
          {
            try
            {

              GameType type = GameType.valueOf(tokens[0]);
              boolean enabled = Boolean.valueOf(tokens[1]).booleanValue();
              
              if (enabled) {
                config.GameList.add(type);
              }
            }
            catch (Exception localException) {}
          }
        }
      }
      


      in.close();
    }
    catch (Exception localException1) {}
    



    if (!config.IsValid()) {
      config = GetDefaultConfig();
    }
    WriteServerConfig(config);
    return config;
  }
  
  public GameServerConfig GetDefaultConfig()
  {
    GameServerConfig config = new GameServerConfig();
    
    config.ServerType = "Minigames";
    config.MinPlayers = 8;
    config.MaxPlayers = 16;
    
    return config;
  }
  
  public void WriteServerConfig(GameServerConfig config)
  {
    try
    {
      FileWriter fstream = new FileWriter("ArcadeSettings.config");
      BufferedWriter out = new BufferedWriter(fstream);
      
      out.write("SERVER_TYPE=" + config.ServerType + "\n");
      out.write("PLAYERS_MIN=" + config.MinPlayers + "\n");
      out.write("PLAYERS_MAX=" + config.MaxPlayers + "\n");
      out.write("\n\nGames List;\n");
      
      for (GameType type : GameType.values())
      {
        out.write(type.toString() + "=" + config.GameList.contains(type) + "\n");
      }
      
      out.close();
    }
    catch (Exception localException) {}
  }
  



  private void DeleteFolders()
  {
    File curDir = new File(".");
    
    File[] filesList = curDir.listFiles();
    for (File file : filesList)
    {
      if (file.isDirectory())
      {

        if (file.getName().length() >= 4)
        {

          if (file.getName().substring(0, 4).equalsIgnoreCase("Game"))
          {

            FileUtil.DeleteFolder(file);
            
            System.out.println("Deleted Old Game: " + file.getName());
          } }
      }
    }
  }
  
  public JavaPlugin GetPlugin() {
    return this;
  }
  

  public String GetWebServerAddress()
  {
    String webServerAddress = getConfig().getString(this.WEB_CONFIG);
    
    return webServerAddress;
  }
  

  public Server GetRealServer()
  {
    return getServer();
  }
  

  public PluginManager GetPluginManager()
  {
    return GetRealServer().getPluginManager();
  }
}
