package nautilus.game.arcade.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.RestartServerEvent;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GamePrepareCountdownCommence;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerPrepareTeleportEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public class GameManager implements org.bukkit.event.Listener
{
  ArcadeManager Manager;
  private int _colorId = 0;
  
  public GameManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler
  public void DisplayIP(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    if ((this.Manager.GetGame() != null) && (this.Manager.GetGame().GetState() != Game.GameState.Live))
    {
      ChatColor col = ChatColor.RED;
      if (this._colorId == 1) { col = ChatColor.YELLOW;
      } else if (this._colorId == 2) { col = ChatColor.GREEN;
      } else if (this._colorId == 3) col = ChatColor.AQUA;
      this._colorId = ((this._colorId + 1) % 4);
      
      String text = col + C.Bold + "US.MINEPLEX.COM       EU.MINEPLEX.COM";
      
      double health = 1.0D;
      if (this.Manager.GetGame().GetState() == Game.GameState.Prepare)
      {
        health = (9.0D - (System.currentTimeMillis() - this.Manager.GetGame().GetStateTime()) / 1000.0D) / 9.0D;
      }
      else if (this.Manager.GetGame().GetState() == Game.GameState.Recruit)
      {
        if (this.Manager.GetGame().GetCountdown() >= 0) {
          health = this.Manager.GetGame().GetCountdown() / 60.0D;
        }
      }
      for (Player player : UtilServer.getPlayers()) {
        mineplex.core.common.util.UtilDisplay.displayTextBar(this.Manager.GetPlugin(), player, health, text);
      }
    }
  }
  
  @EventHandler
  public void StateUpdate(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (game.GetState() == Game.GameState.Loading)
    {
      if (UtilTime.elapsed(game.GetStateTime(), 30000L))
      {
        System.out.println("Game Load Expired.");
        game.SetState(Game.GameState.Dead);
      }
    }
    else if (game.GetState() == Game.GameState.Recruit)
    {

      if ((game.GetCountdown() != -1) && 
        (UtilServer.getPlayers().length < this.Manager.GetPlayerMin()) && 
        (!game.GetCountdownForce()))
      {
        game.SetCountdown(-1);
        this.Manager.GetLobby().DisplayWaiting();
      }
      
      if (game.GetCountdown() != -1) {
        StateCountdown(game, -1, false);
      }
      else if (game.AutoStart)
      {
        if (UtilServer.getPlayers().length >= this.Manager.GetPlayerFull()) {
          StateCountdown(game, 20, false);
        }
        else if (UtilServer.getPlayers().length >= this.Manager.GetPlayerMin()) {
          StateCountdown(game, 60, false);
        }
      }
    } else if (game.GetState() == Game.GameState.Prepare)
    {
      if (game.CanStartPrepareCountdown())
      {
        if (UtilTime.elapsed(game.GetStateTime(), 9000L))
        {
          for (Player player : UtilServer.getPlayers()) {
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 2.0F, 2.0F);
          }
          if (game.GetPlayers(true).size() < 2)
          {
            game.Announce(C.cWhite + C.Bold + game.GetName() + " ended, not enough players!");
            game.SetState(Game.GameState.Dead);
          }
          else
          {
            game.SetState(Game.GameState.Live);
          }
        }
        else
        {
          for (Player player : UtilServer.getPlayers()) {
            player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
          }
        }
      }
    } else if (game.GetState() == Game.GameState.Live)
    {
      if (game.GetType() == GameType.Bridge)
      {
        if (UtilTime.elapsed(game.GetStateTime(), 96000000L))
        {
          game.SetState(Game.GameState.End);
        }
      }
      else if (game.GetType() == GameType.SurvivalGames)
      {
        if (UtilTime.elapsed(game.GetStateTime(), 9600000L))
        {
          game.SetState(Game.GameState.End);
        }
      }
      else if (game.GetType().toString().toLowerCase().contains("teams"))
      {
        if (UtilTime.elapsed(game.GetStateTime(), 9600000L))
        {
          game.SetState(Game.GameState.End);
        }
      }
      else if (game.GetType() != GameType.UHC)
      {
        if (UtilTime.elapsed(game.GetStateTime(), 1200000L))
        {
          game.SetState(Game.GameState.End);
        }
      }
    }
    else if (game.GetState() == Game.GameState.End)
    {
      if (UtilTime.elapsed(game.GetStateTime(), 10000L))
      {
        game.SetState(Game.GameState.Dead);
      }
    }
  }
  

  public void StateCountdown(Game game, int timer, boolean force)
  {
    if ((!game.GetCountdownForce()) && (!force) && (!UtilTime.elapsed(game.GetStateTime(), 15000L)))
    {
      return;
    }
    
    if (force) {
      game.SetCountdownForce(true);
    }
    
    TeamPreferenceJoin(game);
    

    TeamPreferenceSwap(game);
    

    TeamDefaultJoin(game);
    

    if (game.GetCountdown() == -1)
    {
      game.InformQueuePositions();
    }
    


    if (force) {
      game.SetCountdownForce(true);
    }
    
    if (game.GetCountdown() == -1) {
      game.SetCountdown(timer + 1);
    }
    
    if ((game.GetCountdown() > timer + 1) && (timer != -1)) {
      game.SetCountdown(timer + 1);
    }
    
    if (game.GetCountdown() > 0) {
      game.SetCountdown(game.GetCountdown() - 1);
    }
    
    if (game.GetCountdown() > 0)
    {
      this.Manager.GetLobby().WriteGameLine("starting in " + game.GetCountdown() + "...", 3, 159, (byte)13);
    }
    else
    {
      this.Manager.GetLobby().WriteGameLine("game in progress", 3, 159, (byte)13);
    }
    
    if ((game.GetCountdown() > 0) && (game.GetCountdown() <= 10)) {
      for (Player player : UtilServer.getPlayers()) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
      }
    }
    if (game.GetCountdown() == 0) {
      game.SetState(Game.GameState.Prepare);
    }
  }
  
  @EventHandler
  public void restartServerCheck(RestartServerEvent event) {
    if ((this.Manager.GetGame() != null) && (this.Manager.GetGame().GetState() != Game.GameState.Recruit)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void KitRegister(GameStateChangeEvent event) {
    if (event.GetState() != event.GetGame().KitRegisterState) {
      return;
    }
    event.GetGame().RegisterKits();
  }
  
  @EventHandler
  public void KitDeregister(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Dead) {
      return;
    }
    event.GetGame().DeregisterKits();
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void TeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    Game game = event.GetGame();
    
    for (String team : game.WorldData.SpawnLocs.keySet())
    {
      ChatColor color;
      ChatColor color;
      if (team.equalsIgnoreCase("RED")) { color = ChatColor.RED; } else { ChatColor color;
        if (team.equalsIgnoreCase("YELLOW")) { color = ChatColor.YELLOW; } else { ChatColor color;
          if (team.equalsIgnoreCase("GREEN")) { color = ChatColor.GREEN; } else { ChatColor color;
            if (team.equalsIgnoreCase("BLUE")) { color = ChatColor.AQUA;
            }
            else {
              color = ChatColor.DARK_GREEN;
              
              if ((game.GetTeamList().size() == 0) && (game.WorldData.SpawnLocs.size() > 1)) color = ChatColor.RED;
              if (game.GetTeamList().size() == 1) color = ChatColor.YELLOW;
              if (game.GetTeamList().size() == 2) color = ChatColor.GREEN;
              if (game.GetTeamList().size() == 3) color = ChatColor.AQUA;
              if (game.GetTeamList().size() == 4) color = ChatColor.GOLD;
              if (game.GetTeamList().size() == 5) color = ChatColor.LIGHT_PURPLE;
              if (game.GetTeamList().size() == 6) color = ChatColor.DARK_BLUE;
              if (game.GetTeamList().size() == 7) color = ChatColor.WHITE;
              if (game.GetTeamList().size() == 8) color = ChatColor.BLUE;
              if (game.GetTeamList().size() == 9) color = ChatColor.DARK_GREEN;
              if (game.GetTeamList().size() == 10) color = ChatColor.DARK_PURPLE;
              if (game.GetTeamList().size() == 11) color = ChatColor.DARK_GRAY;
              if (game.GetTeamList().size() == 12) color = ChatColor.DARK_RED;
            }
          } } }
      GameTeam newTeam = new GameTeam(game, team, color, (ArrayList)game.WorldData.SpawnLocs.get(team));
      game.AddTeam(newTeam);
    }
    

    game.RestrictKits();
    

    game.ParseData();
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void TeamScoreboardCreation(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    event.GetGame().CreateScoreboardTeams();
  }
  

  public void TeamPreferenceJoin(Game game)
  {
    for (GameTeam team : game.GetTeamPreferences().keySet())
    {
      Iterator<Player> queueIterator = ((ArrayList)game.GetTeamPreferences().get(team)).iterator();
      
      while (queueIterator.hasNext())
      {
        Player player = (Player)queueIterator.next();
        
        if (!game.CanJoinTeam(team)) {
          break;
        }
        queueIterator.remove();
        
        if (!game.IsPlaying(player))
        {
          PlayerAdd(game, player, team);
        }
        else
        {
          game.SetPlayerTeam(player, team);
        }
      }
    }
  }
  
  public void TeamPreferenceSwap(Game game)
  {
    Iterator<Player> queueIterator;
    for (Iterator localIterator1 = game.GetTeamPreferences().keySet().iterator(); localIterator1.hasNext(); 
        


        queueIterator.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
      queueIterator = ((ArrayList)game.GetTeamPreferences().get(team)).iterator();
      
      continue;
      
      Player player = (Player)queueIterator.next();
      
      GameTeam currentTeam = game.GetTeam(player);
      

      if (currentTeam != null)
      {


        if (team == currentTeam)
        {
          queueIterator.remove();
        }
        else
        {
          for (Player other : team.GetPlayers(false))
          {
            if (!other.equals(player))
            {

              GameTeam otherPref = game.GetTeamPreference(other);
              if (otherPref != null)
              {

                if (otherPref.equals(currentTeam))
                {
                  UtilPlayer.message(player, F.main("Team", "You swapped team with " + F.elem(new StringBuilder().append(team.GetColor()).append(other.getName()).toString()) + "."));
                  UtilPlayer.message(other, F.main("Team", "You swapped team with " + F.elem(new StringBuilder().append(currentTeam.GetColor()).append(player.getName()).toString()) + "."));
                  

                  queueIterator.remove();
                  game.SetPlayerTeam(player, team);
                  

                  game.SetPlayerTeam(other, currentTeam);
                } }
            }
          }
        }
      }
    }
  }
  
  public void TeamDefaultJoin(Game game) {
    for (Player player : )
    {
      if (player.isDead())
      {
        player.sendMessage(F.main("Afk Monitor", "You are being sent to the Lobby for being AFK."));
        this.Manager.GetPortal().SendPlayerToServer(player, "Lobby");
      }
      else if (!game.IsPlaying(player))
      {
        PlayerAdd(game, player, null);
      }
    }
  }
  
  @EventHandler
  public void TeamQueueSizeUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    for (GameTeam team : game.GetTeamList())
    {
      int amount = 0;
      if (game.GetTeamPreferences().containsKey(team))
      {
        amount = ((ArrayList)game.GetTeamPreferences().get(team)).size();
      }
      
      if (team.GetTeamEntity() != null)
      {

        if (game.GetCountdown() == -1)
        {
          team.GetTeamEntity().setCustomName(team.GetFormattedName() + " Team" + ChatColor.RESET + "  " + amount + " Queued");
        }
        else
        {
          team.GetTeamEntity().setCustomName(team.GetPlayers(false).size() + " Players  " + team.GetFormattedName() + " Team" + ChatColor.RESET + "  " + amount + " Queued");
        }
      }
    }
  }
  
  public boolean PlayerAdd(Game game, Player player, GameTeam team) {
    if (team == null) {
      team = game.ChooseTeam(player);
    }
    if (team == null) {
      return false;
    }
    game.SetPlayerTeam(player, team);
    

    player.setGameMode(org.bukkit.GameMode.SURVIVAL);
    
    return true;
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerPrepare(GameStateChangeEvent event)
  {
    final Game game = event.GetGame();
    
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    ArrayList<Player> players = game.GetPlayers(true);
    

    for (int i = 0; i < players.size(); i++)
    {
      final Player player = (Player)players.get(i);
      
      final GameTeam team = game.GetTeam(player);
      
      UtilServer.getServer().getScheduler().runTaskLater(this.Manager.GetPlugin(), new Runnable()
      {

        public void run()
        {
          team.SpawnTeleport(player);
          
          GameManager.this.Manager.Clear(player);
          UtilInv.Clear(player);
          
          game.ValidateKit(player, game.GetTeam(player));
          
          if (game.GetKit(player) != null) {
            game.GetKit(player).ApplyKit(player);
          }
          
          PlayerPrepareTeleportEvent playerStateEvent = new PlayerPrepareTeleportEvent(game, player);
          UtilServer.getServer().getPluginManager().callEvent(playerStateEvent);
        }
      }, i);
    }
    

    UtilServer.getServer().getScheduler().runTaskLater(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        game.AnnounceGame();
        game.StartPrepareCountdown();
        

        GamePrepareCountdownCommence event = new GamePrepareCountdownCommence(game);
        UtilServer.getServer().getPluginManager().callEvent(event);
      }
    }, players.size());
  }
  
  @EventHandler
  public void PlayerTeleportOut(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Dead) {
      return;
    }
    Player[] players = UtilServer.getPlayers();
    

    for (int i = 0; i < players.length; i++)
    {
      final Player player = players[i];
      
      UtilServer.getServer().getScheduler().runTaskLater(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          GameManager.this.Manager.Clear(player);
          UtilInv.Clear(player);
          
          GameManager.this.Manager.GetCondition().EndCondition(player, mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK, "Spectator");
          
          player.eject();
          player.leaveVehicle();
          player.teleport(GameManager.this.Manager.GetLobby().GetSpawn());
        }
      }, i);
    }
  }
  
  @EventHandler
  public void disguiseClean(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Dead) {
      return;
    }
    this.Manager.GetDisguise().clearDisguises();
  }
  

  @EventHandler
  public void WorldFireworksUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (game.GetState() != Game.GameState.End) {
      return;
    }
    Color color = Color.GREEN;
    
    if (game.WinnerTeam != null)
    {
      if (game.WinnerTeam.GetColor() == ChatColor.RED) { color = Color.RED;
      } else if (game.WinnerTeam.GetColor() == ChatColor.AQUA) { color = Color.BLUE;
      } else if (game.WinnerTeam.GetColor() == ChatColor.YELLOW) color = Color.YELLOW; else {
        color = Color.LIME;
      }
    }
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with(org.bukkit.FireworkEffect.Type.BALL_LARGE).trail(false).build();
    
    try
    {
      this.Manager.GetFirework().playFirework(game.GetSpectatorLocation().clone().add(
        Math.random() * 160.0D - 80.0D, 10.0D + Math.random() * 20.0D, Math.random() * 160.0D - 80.0D), effect);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void EndUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    game.EndCheck();
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void EndStateChange(PlayerStateChangeEvent event)
  {
    event.GetGame().EndCheck();
  }
}
