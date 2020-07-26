package nautilus.game.arcade.game.games.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerGameRespawnEvent;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.common.dominate_data.CapturePoint;
import nautilus.game.arcade.game.games.common.dominate_data.Emerald;
import nautilus.game.arcade.game.games.common.dominate_data.PlayerData;
import nautilus.game.arcade.game.games.common.dominate_data.Resupply;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class Domination extends TeamGame
{
  private ArrayList<CapturePoint> _points = new ArrayList();
  private ArrayList<Emerald> _emerald = new ArrayList();
  private ArrayList<Resupply> _resupply = new ArrayList();
  

  private HashMap<String, PlayerData> _stats = new HashMap();
  

  private ArrayList<String> _lastScoreboard = new ArrayList();
  

  private int _victoryScore = 15000;
  private int _redScore = 0;
  private int _blueScore = 0;
  







  public Domination(ArcadeManager manager, GameType type, Kit[] kits)
  {
    super(manager, type, kits, new String[] {"Capture Beacons for Points", "+500 Points for Emerald Powerups", "+50 Points for Kills", "First team to 15000 Points wins" });
    



    this.DeathOut = false;
    this.PrepareFreeze = true;
    this.HungerSet = 20;
    this.WorldTimeSet = 2000;
    
    this.DeathSpectateSecs = 10.0D;
  }
  



  public void ParseData()
  {
    for (String pointName : this.WorldData.GetAllCustomLocs().keySet())
    {
      this._points.add(new CapturePoint(this, pointName, (Location)((ArrayList)this.WorldData.GetAllCustomLocs().get(pointName)).get(0)));
    }
    
    for (Location loc : this.WorldData.GetDataLocs("YELLOW"))
    {
      this._resupply.add(new Resupply(this, loc));
    }
    
    for (Location loc : this.WorldData.GetDataLocs("LIME"))
    {
      this._emerald.add(new Emerald(this, loc));
    }
    

    if ((this instanceof nautilus.game.arcade.game.games.champions.ChampionsDominate))
    {
      this.CreatureAllowOverride = true;
      
      for (int i = 0; (i < GetKits().length) && (i < this.WorldData.GetDataLocs("RED").size()) && (i < this.WorldData.GetDataLocs("BLUE").size()); i++)
      {
        org.bukkit.entity.Entity ent = GetKits()[i].SpawnEntity((Location)this.WorldData.GetDataLocs("RED").get(i));
        this.Manager.GetLobby().AddKitLocation(ent, GetKits()[i], (Location)this.WorldData.GetDataLocs("RED").get(i));
        
        ent = GetKits()[i].SpawnEntity((Location)this.WorldData.GetDataLocs("BLUE").get(i));
        this.Manager.GetLobby().AddKitLocation(ent, GetKits()[i], (Location)this.WorldData.GetDataLocs("BLUE").get(i));
      }
      
      this.CreatureAllowOverride = false;
    }
  }
  
  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != nautilus.game.arcade.game.Game.GameState.Recruit) {
      return;
    }
    for (GameTeam team : GetTeamList()) {
      if (team.GetColor() == ChatColor.AQUA)
        team.SetColor(ChatColor.BLUE);
    }
  }
  
  @EventHandler
  public void Updates(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() == UpdateType.FAST) {
      for (CapturePoint cur : this._points)
        cur.Update();
    }
    if (event.getType() == UpdateType.FAST) {
      for (Emerald cur : this._emerald)
        cur.Update();
    }
    if (event.getType() == UpdateType.FAST) {
      for (Resupply cur : this._resupply)
        cur.Update();
    }
  }
  
  @EventHandler
  public void PowerupPickup(PlayerPickupItemEvent event) {
    for (Emerald cur : this._emerald) {
      cur.Pickup(event.getPlayer(), event.getItem());
    }
    for (Resupply cur : this._resupply) {
      cur.Pickup(event.getPlayer(), event.getItem());
    }
  }
  
  @EventHandler
  public void KillScore(CombatDeathEvent event) {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    
    GameTeam killedTeam = GetTeam(killed);
    if (killedTeam == null) { return;
    }
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    
    if (killer == null) {
      return;
    }
    GameTeam killerTeam = GetTeam(killer);
    if (killerTeam == null) { return;
    }
    if (killerTeam.equals(killedTeam)) {
      return;
    }
    AddScore(killerTeam, 50);
  }
  
  public void AddScore(GameTeam team, int score)
  {
    if (team.GetColor() == ChatColor.RED)
    {
      this._redScore = Math.min(this._victoryScore, this._redScore + score);
    }
    else
    {
      this._blueScore = Math.min(this._victoryScore, this._blueScore + score);
    }
    
    EndCheckScore();
  }
  

  @EventHandler
  public void ItemDespawn(ItemDespawnEvent event)
  {
    event.setCancelled(true);
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    ScoreboardWrite();
  }
  

  private void ScoreboardWrite()
  {
    if (!InProgress()) {
      return;
    }
    
    for (String string : this._lastScoreboard)
    {
      GetScoreboard().resetScores(string);
    }
    this._lastScoreboard.clear();
    

    String redScore = this._redScore + C.cRed + C.Bold + " Red";
    this._lastScoreboard.add(redScore);
    GetObjectiveSide().getScore(redScore).setScore(8);
    
    String blueScore = this._blueScore + C.cAqua + C.Bold + " Blue";
    this._lastScoreboard.add(blueScore);
    GetObjectiveSide().getScore(blueScore).setScore(7);
    
    this._lastScoreboard.add(" ");
    GetObjectiveSide().getScore(" ").setScore(6);
    

    for (int i = 0; i < this._points.size(); i++)
    {
      CapturePoint cp = (CapturePoint)this._points.get(i);
      
      String out = cp.GetScoreboardName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      GetObjectiveSide().getScore(out).setScore(5 - i);
    }
  }
  
  public void EndCheckScore()
  {
    GameTeam winner = null;
    
    if (this._redScore >= this._victoryScore) {
      winner = GetTeam(ChatColor.RED);
    } else if (this._blueScore >= this._victoryScore) {
      winner = GetTeam(ChatColor.BLUE);
    }
    if (winner == null) {
      return;
    }
    ScoreboardWrite();
    

    AnnounceEnd(winner);
    
    for (Iterator localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
        






        ???.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
      if ((this.WinnerTeam != null) && (team.equals(this.WinnerTeam)))
      {
        for (Player player : team.GetPlayers(false)) {
          AddGems(player, 10.0D, "Winning Team", false);
        }
      }
      ??? = team.GetPlayers(false).iterator(); continue;Player player = (Player)???.next();
      if (player.isOnline()) {
        AddGems(player, 10.0D, "Participation", false);
      }
    }
    
    SetState(nautilus.game.arcade.game.Game.GameState.End);
  }
  

  public double GetKillsGems(Player killer, Player killed, boolean assist)
  {
    return 1.0D;
  }
  
  @EventHandler
  public void InventoryLock(InventoryClickEvent event)
  {
    if (event.getInventory().getType() == org.bukkit.event.inventory.InventoryType.CRAFTING)
    {
      event.setCancelled(true);
      event.getWhoClicked().closeInventory();
    }
  }
  
  public String GetMode()
  {
    return "Domination";
  }
  
  @EventHandler
  public void RespawnRegen(PlayerGameRespawnEvent event)
  {
    this.Manager.GetCondition().Factory().Regen("Respawn", event.GetPlayer(), event.GetPlayer(), 8.0D, 3, false, false, false);
    this.Manager.GetCondition().Factory().Protection("Respawn", event.GetPlayer(), event.GetPlayer(), 8.0D, 3, false, false, false);
  }
  
  public PlayerData GetStats(Player player)
  {
    if (!this._stats.containsKey(player.getName())) {
      this._stats.put(player.getName(), new PlayerData(player.getName()));
    }
    return (PlayerData)this._stats.get(player.getName());
  }
  
  @EventHandler
  public void StatsKillAssistDeath(CombatDeathEvent event)
  {
    nautilus.game.arcade.game.Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    GetStats(killed).Deaths += 1;
    
    if (event.GetLog().GetKiller() != null)
    {
      Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
      
      if ((killer != null) && (!killer.equals(killed))) {
        GetStats(killer).Kills += 1;
      }
    }
    for (CombatComponent log : event.GetLog().GetAttackers())
    {
      if ((event.GetLog().GetKiller() == null) || (!log.equals(event.GetLog().GetKiller())))
      {

        Player assist = UtilPlayer.searchExact(log.GetName());
        

        if (assist != null)
          GetStats(assist).Assists += 1;
      }
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.MONITOR)
  public void StatsKillAssistDeath(CustomDamageEvent event) {
    Player damager = event.GetDamagerPlayer(true);
    if (damager != null)
    {
      GetStats(damager).DamageDealt += event.GetDamage();
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee != null)
    {
      GetStats(damagee).DamageTaken += event.GetDamage();
    }
  }
}
