package nautilus.game.arcade.game.games.deathtag;

import java.util.ArrayList;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class DeathTag extends SoloGame
{
  private GameTeam _runners;
  private GameTeam _chasers;
  private NautHashMap<Player, Location> _deathLocation = new NautHashMap();
  
  private int _currentSpeed = -1;
  
















  public DeathTag(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.DeathTag, new Kit[] {new nautilus.game.arcade.game.games.deathtag.kits.KitRunnerBasher(manager), new nautilus.game.arcade.game.games.deathtag.kits.KitRunnerArcher(manager), new nautilus.game.arcade.game.games.deathtag.kits.KitRunnerTraitor(manager), new nautilus.game.arcade.kit.NullKit(manager), new nautilus.game.arcade.game.games.deathtag.kits.KitAlphaChaser(manager), new nautilus.game.arcade.game.games.deathtag.kits.KitChaser(manager) }, new String[] {"Run from the Undead!", "If you die, you become Undead!", "The last Runner alive wins!" });
    

    this.DeathOut = false;
    this.HungerSet = 20;
    
    this.CompassEnabled = true;
    
    this.PrepareFreeze = false;
  }
  

  public void RestrictKits()
  {
    for (Kit kit : GetKits())
    {
      for (GameTeam team : GetTeamList())
      {
        if (team.GetColor() == ChatColor.RED)
        {
          if (kit.GetName().contains("ZOMBIE")) {
            team.GetRestrictedKits().add(kit);
          }
          
        }
        else if (kit.GetName().contains("Chaser")) {
          team.GetRestrictedKits().add(kit);
        }
      }
    }
  }
  

  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    this._runners = ((GameTeam)GetTeamList().get(0));
    this._runners.SetName("Runners");
    

    this._chasers = new GameTeam(this, "Chasers", ChatColor.RED, this._runners.GetSpawns());
    this._chasers.SetVisible(false);
    GetTeamList().add(this._chasers);
    
    RestrictKits();
  }
  

  public GameTeam ChooseTeam(Player player)
  {
    return this._runners;
  }
  
  @EventHandler
  public void UpdateSpeed(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    double ratio = this._chasers.GetPlayers(false).size() / GetPlayers(false).size();
    
    if ((this._currentSpeed == -1) && (ratio > 0.25D))
    {
      Announce(C.cGreen + C.Bold + "Runners receive Speed I");
      this._currentSpeed = 0;
    }
    else if ((this._currentSpeed == -1) && (ratio > 0.5D))
    {
      Announce(C.cGreen + C.Bold + "Runners receive Speed II");
      this._currentSpeed = 1;
    }
    else if ((this._currentSpeed == -1) && (ratio > 0.75D))
    {
      Announce(C.cGreen + C.Bold + "Runners receive Speed III");
      this._currentSpeed = 2;
    }
  }
  
  @EventHandler
  public void ApplyConditions(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this._currentSpeed >= 0) {
      for (Player player : this._runners.GetPlayers(false))
      {
        this.Manager.GetCondition().Factory().Speed("Runner", player, player, 1.9D, this._currentSpeed, false, false, true);
      }
    }
    for (Player player : this._chasers.GetPlayers(false))
    {
      this.Manager.GetCondition().Factory().Regen("Undying", player, player, 1.9D, 4, false, false, false);
      
      if (this._currentSpeed < 0) {
        this.Manager.GetCondition().Factory().Speed("Haste", player, player, 1.9D, 0, false, false, true);
      }
    }
  }
  
  @EventHandler
  public void UpdateChasers(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    int req = 1 + (int)((System.currentTimeMillis() - GetStateTime()) / 30000L);
    
    while ((this._chasers.GetPlayers(true).size() < req) && (this._runners.GetPlayers(true).size() > 0))
    {
      Player player = (Player)this._runners.GetPlayers(true).get(mineplex.core.common.util.UtilMath.r(this._runners.GetPlayers(true).size()));
      SetChaser(player, true);
    }
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    if (this._runners.HasPlayer(event.getEntity()))
    {
      this._deathLocation.put(event.getEntity(), event.getEntity().getLocation());
      SetChaser(event.getEntity(), false);
    }
  }
  
  public void SetChaser(Player player, boolean forced)
  {
    if (!GetPlaces().contains(player)) {
      GetPlaces().add(0, player);
    }
    SetPlayerTeam(player, this._chasers);
    

    Kit newKit = GetKits()[5];
    if (forced) { newKit = GetKits()[4];
    }
    SetKit(player, newKit, false);
    newKit.ApplyKit(player);
    

    for (Player other : UtilServer.getPlayers())
    {
      other.hidePlayer(player);
      other.showPlayer(player);
    }
    
    if (forced)
    {
      AddGems(player, 10.0D, "Forced Chaser", false);
      
      Announce(F.main("Game", F.elem(new StringBuilder().append(this._runners.GetColor()).append(player.getName()).toString()) + " has become an " + 
        F.elem(new StringBuilder().append(this._chasers.GetColor()).append(newKit.GetName()).toString()) + "."));
      
      player.getWorld().strikeLightningEffect(player.getLocation());
    }
    
    UtilPlayer.message(player, C.cRed + C.Bold + "You are now a Chaser!");
    UtilPlayer.message(player, C.cRed + C.Bold + "KILL THEM ALL!!!!!!");
  }
  

  public void RespawnPlayer(final Player player)
  {
    this.Manager.Clear(player);
    
    if (this._chasers.HasPlayer(player))
    {
      player.eject();
      
      if (this._deathLocation.containsKey(player)) {
        player.teleport((Location)this._deathLocation.remove(player));
      } else {
        player.teleport(this._chasers.GetSpawn());
      }
    }
    
    this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        DeathTag.this.GetKit(player).ApplyKit(player);
        

        for (Player other : UtilServer.getPlayers())
        {
          other.hidePlayer(player);
          other.showPlayer(player);
        }
      }
    }, 0L);
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if (this._runners.GetPlayers(true).size() <= 1)
    {
      if (this._runners.GetPlayers(true).size() == 1) {
        GetPlaces().add(0, (Player)GetPlayers(true).get(0));
      }
      if (GetPlaces().size() >= 1) {
        AddGems((Player)GetPlaces().get(0), 15.0D, "1st Place", false);
      }
      if (GetPlaces().size() >= 2) {
        AddGems((Player)GetPlaces().get(1), 10.0D, "2nd Place", false);
      }
      if (GetPlaces().size() >= 3) {
        AddGems((Player)GetPlaces().get(2), 5.0D, "3rd Place", false);
      }
      for (Player player : GetPlayers(false)) {
        if (player.isOnline())
          AddGems(player, 10.0D, "Participation", false);
      }
      SetState(Game.GameState.End);
      AnnounceEnd(GetPlaces());
    }
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if ((this._runners == null) || (this._chasers == null)) {
      return;
    }
    GetObjectiveSide().getScore(this._runners.GetColor() + this._runners.GetName()).setScore(this._runners.GetPlayers(true).size());
    GetObjectiveSide().getScore(this._chasers.GetColor() + this._chasers.GetName()).setScore(this._chasers.GetPlayers(true).size());
  }
  

  public boolean CanJoinTeam(GameTeam team)
  {
    if (team.GetColor() == ChatColor.RED)
    {
      return team.GetSize() < 1 + UtilServer.getPlayers().length / 8;
    }
    
    return true;
  }
  

  public double GetKillsGems(Player killer, Player killed, boolean assist)
  {
    if (GetTeam(killed).equals(this._runners))
    {
      return 4.0D;
    }
    
    return 0.0D;
  }
}
