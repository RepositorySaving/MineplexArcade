package nautilus.game.arcade.game.games.dragons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.dragons.kits.KitCoward;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkSparkler;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class DragonsTeams extends TeamGame
{
  private HashMap<EnderDragon, DragonTeamsData> _dragons = new HashMap();
  private ArrayList<Location> _dragonSpawns = new ArrayList();
  
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private HashMap<GameTeam, Integer> _teamScore = new HashMap();
  
  private PerkSparkler _sparkler = null;
  













  public DragonsTeams(ArcadeManager manager)
  {
    super(manager, GameType.DragonsTeams, new Kit[] {new KitCoward(manager), new nautilus.game.arcade.game.games.dragons.kits.KitMarksman(manager), new nautilus.game.arcade.game.games.dragons.kits.KitPyrotechnic(manager) }, new String[] {"You have angered the Dragons!", "Survive as best you can!!!", "Team with longest time survived wins!" });
    

    this.DamagePvP = false;
    this.HungerSet = 20;
    this.WorldWaterDamage = 4;
  }
  

  public void ParseData()
  {
    this._dragonSpawns = this.WorldData.GetDataLocs("RED");
  }
  
  @EventHandler
  public void SparklerAttract(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._sparkler == null)
    {
      for (Kit kit : GetKits())
      {
        for (Perk perk : kit.GetPerks())
        {
          if ((perk instanceof PerkSparkler))
          {
            this._sparkler = ((PerkSparkler)perk);
          }
        }
      }
    }
    
    for (Iterator localIterator = this._sparkler.GetItems().iterator(); localIterator.hasNext(); 
        
        ((Iterator)???).hasNext())
    {
      Item item = (Item)localIterator.next();
      
      ??? = this._dragons.values().iterator(); continue;DragonTeamsData data = (DragonTeamsData)((Iterator)???).next();
      
      if (UtilMath.offset(data.Location, item.getLocation()) < 48.0D)
      {
        data.TargetEntity = item;
      }
    }
  }
  

  @EventHandler
  public void Death(PlayerStateChangeEvent event)
  {
    if (event.GetState() != nautilus.game.arcade.game.GameTeam.PlayerState.OUT) {
      return;
    }
    long time = System.currentTimeMillis() - GetStateTime();
    double gems = time / 10000.0D;
    String reason = "Survived for " + mineplex.core.common.util.UtilTime.MakeStr(time);
    
    AddGems(event.GetPlayer(), gems, reason, false);
  }
  
  @EventHandler
  public void DragonSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    if (GetState() != Game.GameState.Live) {
      return;
    }
    Iterator<EnderDragon> dragonIterator = this._dragons.keySet().iterator();
    
    while (dragonIterator.hasNext())
    {
      EnderDragon ent = (EnderDragon)dragonIterator.next();
      
      if (!ent.isValid())
      {
        dragonIterator.remove();
        ent.remove();
      }
    }
    
    if (this._dragons.size() < 7)
    {
      this.CreatureAllowOverride = true;
      EnderDragon ent = (EnderDragon)GetSpectatorLocation().getWorld().spawn((Location)this._dragonSpawns.get(0), EnderDragon.class);
      UtilEnt.Vegetate(ent);
      this.CreatureAllowOverride = false;
      
      ent.getWorld().playSound(ent.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL, 20.0F, 1.0F);
      
      this._dragons.put(ent, new DragonTeamsData(this, ent));
    }
  }
  
  @EventHandler
  public void DragonLocation(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (GetState() != Game.GameState.Live) {
      return;
    }
    
    for (DragonTeamsData data : this._dragons.values())
    {
      data.Target();
      data.Move();
    }
  }
  
  @EventHandler
  public void DragonTargetCancel(EntityTargetEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void DragonArrowDamage(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!this._dragons.containsKey(event.GetDamageeEntity())) {
      return;
    }
    ((DragonTeamsData)this._dragons.get(event.GetDamageeEntity())).HitByArrow();
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (event.GetDamagerEntity(true) == null) {
      return;
    }
    event.SetCancelled("Dragon");
    event.AddMod("Dragon", "Damage Reduction", -1.0D * (event.GetDamageInitial() - 1.0D), false);
    
    event.SetKnockback(false);
    
    damagee.playEffect(EntityEffect.HURT);
    
    mineplex.core.common.util.UtilAction.velocity(damagee, mineplex.core.common.util.UtilAlg.getTrajectory(event.GetDamagerEntity(true), damagee), 1.0D, false, 0.0D, 0.6D, 2.0D, true);
  }
  
  @EventHandler
  public void FallDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FALL) {
      event.AddMod("Fall Reduction", "Fall Reduction", -1.0D, false);
    }
  }
  
  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (IsLive()) {
      for (GameTeam team : GetTeamList())
      {
        if (!this._teamScore.containsKey(team)) {
          this._teamScore.put(team, Integer.valueOf(0));
        }
        if (team.IsTeamAlive())
          this._teamScore.put(team, Integer.valueOf(((Integer)this._teamScore.get(team)).intValue() + team.GetPlayers(true).size()));
      }
    }
    WriteScoreboard();
  }
  

  private void WriteScoreboard()
  {
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    
    int i = 1;
    String space = " ";
    for (GameTeam team : this._teamScore.keySet())
    {

      int seconds = ((Integer)this._teamScore.get(team)).intValue();
      
      String out = team.GetColor() + seconds + " Seconds";
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(i++);
      

      out = team.GetColor() + C.Bold + team.GetName() + " Time";
      
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
      int bestTime = 0;
      
      for (GameTeam team : this._teamScore.keySet())
      {
        if ((winner == null) || (((Integer)this._teamScore.get(team)).intValue() > bestTime))
        {
          winner = team;
          bestTime = ((Integer)this._teamScore.get(team)).intValue();
        }
      }
      

      if (winner != null)
      {
        AnnounceEnd(winner);
        SetCustomWinLine("Survived " + bestTime + " Seconds!");
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
}
