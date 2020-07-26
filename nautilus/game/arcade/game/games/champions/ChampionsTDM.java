package nautilus.game.arcade.game.games.champions;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.DeathMessageType;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.champions.kits.KitAssassin;
import nautilus.game.arcade.game.games.champions.kits.KitBrute;
import nautilus.game.arcade.game.games.champions.kits.KitMage;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ChampionsTDM extends TeamGame
{
  private ArrayList<String> _lastScoreboard = new ArrayList();
  private HashMap<Player, Integer> _kills = new HashMap();
  







  private Objective _healthObj;
  







  public ChampionsTDM(ArcadeManager manager)
  {
    super(manager, GameType.ChampionsTDM, new Kit[] {new KitBrute(manager), new nautilus.game.arcade.game.games.champions.kits.KitRanger(manager), new nautilus.game.arcade.game.games.champions.kits.KitKnight(manager), new KitMage(manager), new KitAssassin(manager) }, new String[] {"Each player has " + C.cRed + C.Bold + "ONE LIFE", "Kill the other team to win!" });
    


    this.DeathOut = true;
    this.HungerSet = 20;
    this.WorldTimeSet = 2000;
    this.CompassEnabled = true;
    
    this.Manager.GetDamage().UseSimpleWeaponDamage = false;
    
    this._healthObj = GetScoreboard().registerNewObjective("HP", "dummy");
    this._healthObj.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.BELOW_NAME);
  }
  
  @EventHandler
  public void PlayerKillAward(CombatDeathEvent event)
  {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    SetPlayerState(killed, nautilus.game.arcade.game.GameTeam.PlayerState.OUT);
    
    GameTeam killedTeam = GetTeam(killed);
    if (killedTeam == null) {
      return;
    }
    if (event.GetLog().GetKiller() != null)
    {
      Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
      
      if ((killer != null) && (!killer.equals(killed)))
      {
        GameTeam killerTeam = GetTeam(killer);
        if (killerTeam == null) {
          return;
        }
        int kills = 1;
        if (killedTeam.equals(killerTeam)) {
          kills = -1;
        }
        if (this._kills.containsKey(killer)) {
          kills += ((Integer)this._kills.get(killer)).intValue();
        }
        this._kills.put(killer, Integer.valueOf(kills));
        
        ScoreboardRefresh();
      }
    }
  }
  
  @EventHandler
  public void Health(GameStateChangeEvent event)
  {
    if (event.GetState() != nautilus.game.arcade.game.Game.GameState.Live) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      player.setMaxHealth(30.0D);
      player.setHealth(player.getMaxHealth());
    }
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    if (!InProgress()) {
      return;
    }
    ScoreboardRefresh();
  }
  

  public void ScoreboardRefresh()
  {
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    
    int pos = 1;
    String space = " ";
    
    for (GameTeam team : GetTeamList())
    {
      for (Player player : team.GetPlayers(false))
      {
        int kills = 0;
        if (this._kills.containsKey(player)) {
          kills = ((Integer)this._kills.get(player)).intValue();
        }
        String out = kills + " ";
        
        if (IsAlive(player)) {
          out = out + team.GetColor() + player.getName();
        } else {
          out = out + C.cGray + player.getName();
        }
        if (out.length() >= 16) {
          out = out.substring(0, 15);
        }
        this._lastScoreboard.add(out);
        
        GetObjectiveSide().getScore(out).setScore(pos++);
      }
      
      GetObjectiveSide().getScore(space).setScore(pos++);
      
      space = space + " ";
    }
  }
  

  public double GetKillsGems(Player killer, Player killed, boolean assist)
  {
    return 4.0D;
  }
  
  public String GetMode()
  {
    return "Team Deathmatch";
  }
  


  public void ValidateKit(Player player, GameTeam team)
  {
    if (GetKit(player) == null)
    {
      SetKit(player, GetKits()[2], true);
      player.closeInventory();
    }
  }
  

  public DeathMessageType GetDeathMessageType()
  {
    return DeathMessageType.Detailed;
  }
  
  @EventHandler
  public void DisplayHealth(UpdateEvent event)
  {
    for (Player player : GetPlayers(true)) {
      this._healthObj.getScore(player.getName()).setScore((int)player.getHealth());
    }
  }
  
  @EventHandler
  public void WaterArrowCancel(EntityShootBowEvent event) {
    if (event.getEntity().getLocation().getBlock().isLiquid())
    {
      UtilPlayer.message(event.getEntity(), mineplex.core.common.util.F.main("Game", "You cannot use your Bow while swimming."));
      event.setCancelled(true);
    }
  }
}
