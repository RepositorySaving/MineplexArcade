package nautilus.game.arcade.game.games.quiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.quiver.kits.KitElementalist;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Quiver extends SoloGame
{
  private ArrayList<QuiverScore> _ranks = new ArrayList();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  private HashMap<Player, Integer> _combo = new HashMap();
  private HashMap<Player, Integer> _bestCombo = new HashMap();
  private HashMap<Player, Long> _deathTime = new HashMap();
  







  private Objective _scoreObj;
  







  public Quiver(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Quiver, new Kit[] {new nautilus.game.arcade.game.games.quiver.kits.KitLeaper(manager), new nautilus.game.arcade.game.games.quiver.kits.KitBrawler(manager), new KitElementalist(manager) }, new String[] {"Bow and Arrow insta-kills.", "You receive 1 Arrow per kill.", "Glass blocks are breakable", "First player to 20 kills wins." });
    

    this.HungerSet = 20;
    this.DeathOut = false;
    this.DamageSelf = false;
    this.DamageTeamSelf = true;
    this.PrepareFreeze = false;
    this.SpawnDistanceRequirement = 16;
    this.BlockBreakAllow.add(Integer.valueOf(102));
    this.BlockBreakAllow.add(Integer.valueOf(20));
    
    this._scoreObj = GetScoreboard().registerNewObjective("Kills", "dummy");
    this._scoreObj.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.BELOW_NAME);
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
    
    GetObjectiveSide().setDisplayName(C.cWhite + C.Bold + "First to " + C.cGold + C.Bold + "20 Kills");
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
        if (!player.getInventory().contains(Material.ARROW))
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
    Player player = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (player == null) { return;
    }
    
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, F.item("Super Arrow")) });
    player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 3.0F, 2.0F);
    

    AddKill(player);
  }
  
  @EventHandler
  public void ComboReset(CombatDeathEvent event)
  {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.GetEvent().getEntity();
    
    if (!this._combo.containsKey(player)) {
      return;
    }
    int combo = ((Integer)this._combo.remove(player)).intValue();
    
    int best = 0;
    if (this._bestCombo.containsKey(player)) {
      best = ((Integer)this._bestCombo.get(player)).intValue();
    }
    if (combo > best) {
      this._bestCombo.put(player, Integer.valueOf(combo));
    }
  }
  
  public void AddKill(Player player)
  {
    int combo = 1;
    if (this._combo.containsKey(player)) {
      combo += ((Integer)this._combo.get(player)).intValue();
    }
    this._combo.put(player, Integer.valueOf(combo));
    
    AnnounceCombo(player, combo);
    

    for (QuiverScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        score.Kills += 1;
        this._scoreObj.getScore(player.getName()).setScore(score.Kills);
        EndCheck();
        return;
      }
    }
    
    this._ranks.add(new QuiverScore(player, 1));
    this._scoreObj.getScore(player.getName()).setScore(1);
  }
  
  private void AnnounceCombo(Player player, int combo)
  {
    String killType = null;
    if (combo == 20) { killType = "PERFECT RUN";
    } else if (combo == 13) { killType = "GODLIKE";
    } else if (combo == 11) { killType = "UNSTOPPABLE";
    } else if (combo == 9) { killType = "ULTRA KILL";
    } else if (combo == 7) { killType = "MONSTER KILL";
    } else if (combo == 5) { killType = "MEGA KILL";
    } else if (combo == 3) { killType = "TRIPLE KILL";
    }
    if (killType == null) {
      return;
    }
    
    for (Player other : mineplex.core.common.util.UtilServer.getPlayers())
    {
      UtilPlayer.message(other, F.main("Game", C.cGreen + C.Bold + player.getName() + org.bukkit.ChatColor.RESET + " got " + 
        F.elem(new StringBuilder(String.valueOf(C.cAqua)).append(C.Bold).append(killType).append(" (").append(combo).append(" Kills)!").toString())));
      other.playSound(other.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F + combo / 10.0F, 1.0F + combo / 10.0F);
    }
  }
  
  private void SortScores()
  {
    for (int i = 0; i < this._ranks.size(); i++)
    {
      for (int j = this._ranks.size() - 1; j > 0; j--)
      {
        if (((QuiverScore)this._ranks.get(j)).Kills > ((QuiverScore)this._ranks.get(j - 1)).Kills)
        {
          QuiverScore temp = (QuiverScore)this._ranks.get(j);
          this._ranks.set(j, (QuiverScore)this._ranks.get(j - 1));
          this._ranks.set(j - 1, temp);
        }
      }
    }
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
    
    for (String string : this._lastScoreboard)
    {
      GetScoreboard().resetScores(string);
    }
    this._lastScoreboard.clear();
    
    SortScores();
    

    for (int i = 0; (i < this._ranks.size()) && (i < 15); i++)
    {
      QuiverScore score = (QuiverScore)this._ranks.get(i);
      
      String out = score.Kills + " " + C.cGreen + score.Player.getName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(score.Kills);
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
    SortScores();
    
    if (((!this._ranks.isEmpty()) && (((QuiverScore)this._ranks.get(0)).Kills >= 20)) || (GetPlayers(true).size() <= 1))
    {

      this._places.clear();
      for (int i = 0; i < this._ranks.size(); i++) {
        this._places.add(i, ((QuiverScore)this._ranks.get(i)).Player);
      }
      
      if (this._ranks.size() >= 1) {
        AddGems(((QuiverScore)this._ranks.get(0)).Player, 20.0D, "1st Place", false);
      }
      if (this._ranks.size() >= 2) {
        AddGems(((QuiverScore)this._ranks.get(1)).Player, 15.0D, "2nd Place", false);
      }
      if (this._ranks.size() >= 3) {
        AddGems(((QuiverScore)this._ranks.get(2)).Player, 10.0D, "3rd Place", false);
      }
      
      for (Player player : this._bestCombo.keySet())
      {
        int combo = ((Integer)this._bestCombo.get(player)).intValue();
        
        if (combo >= 20) { AddGems(player, 40.0D, "PERFECT - 20 Kill Combo", false);
        } else if (combo >= 13) { AddGems(player, 24.0D, "GODLIKE - 13 Kill Combo", false);
        } else if (combo >= 11) { AddGems(player, 20.0D, "UNSTOPPABLE - 11 Kill Combo", false);
        } else if (combo >= 9) { AddGems(player, 16.0D, "ULTRA KILL - 9 Kill Combo", false);
        } else if (combo >= 7) { AddGems(player, 12.0D, "MONSTER KILL - 7 Kill Combo", false);
        } else if (combo >= 5) { AddGems(player, 8.0D, "MEGA KILL - 5 Kill Combo", false);
        } else if (combo >= 3) { AddGems(player, 4.0D, "TRIPLE KILL - 3 Kill Combo", false);
        }
      }
      
      for (Player player : GetPlayers(false)) {
        if (player.isOnline())
          AddGems(player, 10.0D, "Participation", false);
      }
      SetState(Game.GameState.End);
      AnnounceEnd(this._places);
    }
  }
}
