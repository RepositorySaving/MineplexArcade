package nautilus.game.arcade.game.games.dragonescape;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.explosion.Explosion;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.dragonescape.kits.KitWarper;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.FireworkHandler;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class DragonEscapeTeams extends TeamGame
{
  private ArrayList<DragonScore> _ranks = new ArrayList();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private NautHashMap<Player, Long> _warpTime = new NautHashMap();
  
  private Location _dragon;
  
  private ArrayList<Location> _waypoints;
  private HashMap<Location, Double> _waypointScore = new HashMap();
  
  private DragonEscapeTeamsData _dragonData;
  
  private double _speedMult = 1.0D;
  













  public DragonEscapeTeams(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.DragonEscapeTeams, new Kit[] {new nautilus.game.arcade.game.games.dragonescape.kits.KitLeaper(manager), new nautilus.game.arcade.game.games.dragonescape.kits.KitDisruptor(manager), new KitWarper(manager) }, new String[] {"Douglas the Dragon is after you!", "RUN!!!!!!!!!!", "Last player alive wins!" });
    

    this.DamagePvP = false;
    this.HungerSet = 20;
  }
  

  public void ParseData()
  {
    this._dragon = ((Location)this.WorldData.GetDataLocs("RED").get(0));
    this._waypoints = new ArrayList();
    

    Location last = this._dragon;
    
    while (!this.WorldData.GetDataLocs("BLACK").isEmpty())
    {
      Location best = null;
      double bestDist = 0.0D;
      

      for (Location loc : this.WorldData.GetDataLocs("BLACK"))
      {
        double dist = UtilMath.offset(loc, last);
        
        if ((best == null) || (dist < bestDist))
        {
          best = loc;
          bestDist = dist;
        }
      }
      

      if ((bestDist < 3.0D) && (this.WorldData.GetDataLocs("BLACK").size() > 1))
      {
        System.out.println("Ignoring Node");
        this.WorldData.GetDataLocs("BLACK").remove(best);
      }
      else
      {
        this._waypoints.add(best);
        this.WorldData.GetDataLocs("BLACK").remove(best);
        best.subtract(new Vector(0, 1, 0));
        
        last = best;
      }
    }
    
    double dist = 0.0D;
    Location lastLoc = null;
    for (int i = 0; i < this._waypoints.size(); i++)
    {
      Location newLoc = (Location)this._waypoints.get(i);
      

      if (lastLoc == null)
      {
        this._waypointScore.put(newLoc, Double.valueOf(0.0D));
        lastLoc = newLoc;
      }
      else
      {
        dist += UtilMath.offset(lastLoc, newLoc);
        this._waypointScore.put(newLoc, Double.valueOf(dist));
        lastLoc = newLoc;
      }
    }
    if (!this.WorldData.GetDataLocs("GREEN").isEmpty()) {
      this._speedMult = (((Location)this.WorldData.GetDataLocs("GREEN").get(0)).getX() / 100.0D);
    }
    if (this.WorldData.MapName.contains("Hell")) {
      this.WorldTimeSet = 16000;
    }
  }
  
  @EventHandler
  public void SpawnDragon(GameStateChangeEvent event) {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    this.CreatureAllowOverride = true;
    EnderDragon dragon = (EnderDragon)this._dragon.getWorld().spawn(this._dragon, EnderDragon.class);
    this.CreatureAllowOverride = false;
    
    dragon.setCustomName(org.bukkit.ChatColor.YELLOW + C.Bold + "Douglas the Dragon");
    
    this._dragonData = new DragonEscapeTeamsData(this, dragon, (Location)this._waypoints.get(0));
  }
  
  @EventHandler
  public void MoveDragon(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._dragonData == null) {
      return;
    }
    this._dragonData.Target = ((Location)this._waypoints.get(Math.min(this._waypoints.size() - 1, GetWaypointIndex(this._dragonData.Location) + 1)));
    
    this._dragonData.Move();
    
    this.Manager.GetExplosion().BlockExplosion(mineplex.core.common.util.UtilBlock.getInRadius(this._dragonData.Location, 10.0D).keySet(), this._dragonData.Location, false);
  }
  
  @EventHandler
  public void UpdateScores(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (this._dragonData == null) {
      return;
    }
    double dragonScore = GetScore(this._dragonData.Dragon);
    
    for (Player player : GetPlayers(true))
    {
      double playerScore = GetScore(player);
      
      SetScore(player, playerScore);
      
      if (dragonScore > playerScore) {
        player.damage(50.0D);
      }
    }
  }
  
  public ArrayList<DragonScore> GetScores() {
    return this._ranks;
  }
  
  public double GetPlayerScore(Player player)
  {
    for (DragonScore score : this._ranks)
    {
      if (score.Player.equals(player)) {
        return Math.max(0.0D, score.Score);
      }
    }
    return 0.0D;
  }
  

  public void SetScore(Player player, double playerScore)
  {
    for (DragonScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {

        int preNode = (int)(score.Score / 10000.0D);
        int postNode = (int)(playerScore / 10000.0D);
        

        if (preNode - postNode >= 3)
        {
          return;
        }
        

        if (postNode - preNode >= 3)
        {
          if ((!this._warpTime.containsKey(score.Player)) || (mineplex.core.common.util.UtilTime.elapsed(((Long)this._warpTime.get(score.Player)).longValue(), 1000L)))
          {
            score.Player.damage(500.0D);
            UtilPlayer.message(player, F.main("Game", "You were killed for trying to cheat!"));
            return;
          }
        }
        
        if (playerScore > score.Score) {
          score.Score = playerScore;
        }
        return;
      }
    }
    
    this._ranks.add(new DragonScore(player, playerScore));
  }
  


  public double GetScore(Entity ent)
  {
    int index = GetWaypointIndex(ent.getLocation());
    

    if (index < this._waypoints.size() - 1)
    {
      double score = ((Double)this._waypointScore.get(this._waypoints.get(index + 1))).doubleValue();
      score -= UtilMath.offset(ent.getLocation(), (Location)this._waypoints.get(index + 1));
      return score;
    }
    

    return ((Double)this._waypointScore.get(this._waypoints.get(index))).doubleValue();
  }
  

  public int GetWaypointIndex(Location loc)
  {
    int best = -1;
    double bestDist = 0.0D;
    
    for (int i = 0; i < this._waypoints.size(); i++)
    {
      Location waypoint = (Location)this._waypoints.get(i);
      
      double dist = UtilMath.offset(waypoint, loc);
      
      if ((best == -1) || (dist < bestDist))
      {
        best = i;
        bestDist = dist;
      }
    }
    
    return best;
  }
  
  public Location GetWaypoint(Location loc)
  {
    Location best = null;
    double bestDist = 0.0D;
    
    for (int i = 0; i < this._waypoints.size(); i++)
    {
      Location waypoint = (Location)this._waypoints.get(i);
      
      double dist = UtilMath.offset(waypoint, loc);
      
      if ((best == null) || (dist < bestDist))
      {
        best = waypoint;
        bestDist = dist;
      }
    }
    
    return best;
  }
  
  public HashMap<GameTeam, Double> GetTeamScores()
  {
    HashMap<GameTeam, Double> scores = new HashMap();
    
    for (GameTeam team : GetTeamList())
    {
      double score = 0.0D;
      for (Player player : team.GetPlayers(false)) {
        score += GetPlayerScore(player);
      }
      scores.put(team, Double.valueOf(score));
    }
    
    return scores;
  }
  
  private void SortScores()
  {
    for (int i = 0; i < this._ranks.size(); i++)
    {
      for (int j = this._ranks.size() - 1; j > 0; j--)
      {
        if (((DragonScore)this._ranks.get(j)).Score > ((DragonScore)this._ranks.get(j - 1)).Score)
        {
          DragonScore temp = (DragonScore)this._ranks.get(j);
          this._ranks.set(j, (DragonScore)this._ranks.get(j - 1));
          this._ranks.set(j - 1, temp);
        }
      }
    }
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    
    int i = 1;
    String space = " ";
    HashMap<GameTeam, Double> scores = GetTeamScores();
    for (GameTeam team : scores.keySet())
    {

      int score = ((Double)scores.get(team)).intValue();
      
      String out = team.GetColor() + score;
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(i++);
      

      out = team.GetColor() + C.Bold + team.GetName() + " Score";
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(i++);
      

      space = space + " ";
      out = space;
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(i++);
    }
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
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    ArrayList<GameTeam> teamsAlive = new ArrayList();
    
    for (GameTeam team : GetTeamList()) {
      if (team.GetPlayers(true).size() > 0)
        teamsAlive.add(team);
    }
    if (teamsAlive.size() <= 0)
    {

      GameTeam winner = null;
      double bestScore = 0.0D;
      
      HashMap<GameTeam, Double> scores = GetTeamScores();
      for (GameTeam team : scores.keySet())
      {
        if ((winner == null) || (((Double)scores.get(team)).doubleValue() > bestScore))
        {
          winner = team;
          bestScore = ((Double)scores.get(team)).doubleValue();
        }
      }
      

      if (winner != null)
      {
        AnnounceEnd(winner);
      }
      
      for (??? = GetTeamList().iterator(); ???.hasNext(); 
          






          ???.hasNext())
      {
        GameTeam team = (GameTeam)???.next();
        
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
      
      SetState(Game.GameState.End);
    }
  }
  
  public double GetSpeedMult()
  {
    return this._speedMult;
  }
  
  @EventHandler
  public void Warp(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK) && 
      (event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_AIR)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilInv.IsItem(player.getItemInHand(), Material.ENDER_PEARL, (byte)0)) {
      return;
    }
    event.setCancelled(true);
    
    SortScores();
    
    Player target = null;
    boolean self = false;
    
    for (int i = this._ranks.size() - 1; i >= 0; i--)
    {
      DragonScore score = (DragonScore)this._ranks.get(i);
      
      if (score.Player.equals(player))
      {
        self = true;
      }
      else if (self)
      {
        if (IsAlive(score.Player))
        {
          target = score.Player;
          break;
        }
      }
    }
    
    if (target != null)
    {
      UtilInv.remove(player, Material.ENDER_PEARL, (byte)0, 1);
      UtilInv.Update(player);
      

      FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(org.bukkit.Color.BLACK).with(org.bukkit.FireworkEffect.Type.BALL).trail(false).build();
      try
      {
        this.Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      

      player.teleport(target.getLocation().add(0.0D, 0.5D, 0.0D));
      player.setVelocity(new Vector(0, 0, 0));
      player.setFallDistance(0.0F);
      

      this._warpTime.put(player, Long.valueOf(System.currentTimeMillis()));
      

      UtilPlayer.message(player, F.main("Game", "You warped to " + F.name(target.getName()) + "!"));
      

      player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1.0F, 1.0F);
      


      try
      {
        this.Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      UtilPlayer.message(player, F.main("Game", "There is no one infront of you!"));
    }
  }
}
