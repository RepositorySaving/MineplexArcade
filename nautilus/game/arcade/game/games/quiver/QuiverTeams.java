package nautilus.game.arcade.game.games.quiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class QuiverTeams extends TeamGame
{
  private HashMap<GameTeam, Integer> _teamKills = new HashMap();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  private HashMap<Player, Long> _deathTime = new HashMap();
  
  private int _reqKills = 100;
  













  public QuiverTeams(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.QuiverTeams, new nautilus.game.arcade.kit.Kit[] {new nautilus.game.arcade.game.games.quiver.kits.KitLeaper(manager), new nautilus.game.arcade.game.games.quiver.kits.KitBrawler(manager) }, new String[] {"Bow and Arrow insta-kills.", "You receive 1 Arrow per kill.", "Glass blocks are breakable", "First team to 100 kills wins." });
    

    this.HungerSet = 20;
    this.DeathOut = false;
    this.DamageSelf = false;
    this.DamageTeamSelf = false;
    this.PrepareFreeze = false;
    this.SpawnDistanceRequirement = 16;
    this.BlockBreakAllow.add(Integer.valueOf(102));
    this.BlockBreakAllow.add(Integer.valueOf(20));
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, F.item("Super Arrow")) });
      player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 3.0F, 2.0F);
    }
    
    GetObjectiveSide().setDisplayName(C.cWhite + C.Bold + "First to " + C.cGold + C.Bold + this._reqKills + " Kills");
    
    for (GameTeam team : GetTeamList())
    {
      this._teamKills.put(team, Integer.valueOf(0));
    }
  }
  
  @EventHandler
  public void BowShoot(EntityShootBowEvent event)
  {
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Arrow arrow = (Arrow)event.getProjectile();
    
    if (arrow.getShooter() == null) {
      return;
    }
    if (!(arrow.getShooter() instanceof Player)) {
      return;
    }
    if (!this._deathTime.containsKey(arrow.getShooter())) {
      return;
    }
    if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._deathTime.get(arrow.getShooter())).longValue(), 1000L)) {
      return;
    }
    event.getProjectile().remove();
    
    final Player player = (Player)arrow.getShooter();
    
    this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        if (!player.getInventory().contains(org.bukkit.Material.ARROW))
          player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, F.item("Super Arrow")) });
      }
    }, 10L);
  }
  

  @EventHandler
  public void Death(CombatDeathEvent event)
  {
    if ((event.GetEvent().getEntity() instanceof Player))
    {
      this._deathTime.put((Player)event.GetEvent().getEntity(), Long.valueOf(System.currentTimeMillis()));
    }
    
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    if (!event.GetLog().GetKiller().IsPlayer()) {
      return;
    }
    Player player = mineplex.core.common.util.UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (player == null) { return;
    }
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, F.item("Super Arrow")) });
    player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 3.0F, 2.0F);
    

    AddKill(player);
  }
  

  public void AddKill(Player player)
  {
    GameTeam team = GetTeam(player);
    if (team == null) { return;
    }
    this._teamKills.put(team, Integer.valueOf(((Integer)this._teamKills.get(team)).intValue() + 1));
    
    WriteScoreboard();
    EndCheck();
  }
  
  @EventHandler
  public void ArrowDamage(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    event.AddMod("Projectile", "Instagib", 9001.0D, false);
    event.SetKnockback(false);
    
    event.GetProjectile().remove();
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    WriteScoreboard();
  }
  

  private void WriteScoreboard()
  {
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    
    for (GameTeam team : this._teamKills.keySet())
    {
      int kills = ((Integer)this._teamKills.get(team)).intValue();
      
      String out = kills + " " + team.GetColor() + team.GetName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      if (kills == 0) {
        kills = -1;
      }
      GetObjectiveSide().getScore(out).setScore(kills);
    }
  }
  
  @EventHandler
  public void PickupCancel(PlayerPickupItemEvent event)
  {
    event.setCancelled(true);
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    GameTeam winner = null;
    
    for (GameTeam team : this._teamKills.keySet())
    {
      if (((Integer)this._teamKills.get(team)).intValue() >= this._reqKills)
      {
        winner = team;
        break;
      }
    }
    
    ArrayList<GameTeam> teamsAlive = new ArrayList();
    
    for (GameTeam team : GetTeamList()) {
      if (team.GetPlayers(true).size() > 0)
        teamsAlive.add(team);
    }
    if ((winner != null) || (teamsAlive.size() <= 1) || (GetPlayers(true).size() <= 1))
    {

      if (winner != null) {
        AnnounceEnd(winner);
      } else if (teamsAlive.size() == 1) {
        AnnounceEnd((GameTeam)teamsAlive.get(0));
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
