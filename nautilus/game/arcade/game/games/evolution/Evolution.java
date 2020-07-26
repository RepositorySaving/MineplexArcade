package nautilus.game.arcade.game.games.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.evolution.kits.KitHealth;
import nautilus.game.arcade.game.games.evolution.mobs.KitSkeleton;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Evolution extends SoloGame
{
  private ArrayList<EvoScore> _ranks = new ArrayList();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private HashMap<Player, Kit> _bonusKit = new HashMap();
  













  private Objective _scoreObj;
  













  public Evolution(ArcadeManager manager)
  {
    super(manager, GameType.Evolution, new Kit[] {new KitHealth(manager), new nautilus.game.arcade.game.games.evolution.kits.KitAgility(manager), new nautilus.game.arcade.game.games.evolution.kits.KitRecharge(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitGolem(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitBlaze(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitSlime(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitCreeper(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitEnderman(manager), new KitSkeleton(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitSpider(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitSnowman(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitWolf(manager), new nautilus.game.arcade.game.games.evolution.mobs.KitChicken(manager) }, new String[] {"You evolve when you get a kill.", "Each evolution has unique skills.", "", "First to get through 10 evolutions wins!" });
    

    this.DamageTeamSelf = true;
    
    this.HungerSet = 20;
    
    this.DeathOut = false;
    
    this.PrepareFreeze = false;
    
    this.SpawnDistanceRequirement = 16;
    
    this._scoreObj = GetScoreboard().registerNewObjective("Evolutions", "dummy");
    this._scoreObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void RegisterMobKits(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {}
  }
  


  @EventHandler(priority=EventPriority.MONITOR)
  public void StoreBonusKits(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      this._bonusKit.put(player, GetKit(player));
      UpgradeKit(player, true);
    }
  }
  

  public boolean HasKit(Player player, Kit kit)
  {
    if (GetKit(player) == null) {
      return false;
    }
    if (GetKit(player).equals(kit)) {
      return true;
    }
    
    if ((this._bonusKit.containsKey(player)) && 
      (((Kit)this._bonusKit.get(player)).equals(kit))) {
      return true;
    }
    return false;
  }
  
  @EventHandler
  public void PlayerKillAward(CombatDeathEvent event)
  {
    nautilus.game.arcade.game.Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    final Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (killer == null) {
      return;
    }
    if (killer.equals(event.GetEvent().getEntity())) {
      return;
    }
    this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        Evolution.this.UpgradeKit(killer, false);
      }
    }, 0L);
  }
  
  public void UpgradeKit(Player player, boolean first)
  {
    if (!Recharge.Instance.use(player, "Evolve", 500L, false, false)) {
      return;
    }
    
    Recharge.Instance.Reset(player);
    Recharge.Instance.useForce(player, "Evolve", 500L);
    
    Kit kit = GetKit(player);
    
    for (int i = 3; i < GetKits().length; i++)
    {
      if ((kit.equals(GetKits()[i])) || (first))
      {
        if (!first) {
          i++;
        }
        if (i < GetKits().length)
        {
          SetKit(player, GetKits()[i], false);
          

          SetScore(player, i - 3);
          

          GetKits()[i].ApplyKit(player);
          

          FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(org.bukkit.Color.LIME).with(org.bukkit.FireworkEffect.Type.BALL).trail(false).build();
          
          try
          {
            this.Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
          

          player.teleport(GetTeam(player).GetSpawn());
          
          return;
        }
        

        End();
        return;
      }
    }
  }
  

  public void SetScore(Player player, int level)
  {
    this._scoreObj.getScore(player).setScore(level);
    

    for (EvoScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        score.Kills = level;
        return;
      }
    }
    
    this._ranks.add(new EvoScore(player, level));
  }
  
  public int GetScore(Player player)
  {
    if (!IsAlive(player)) {
      return 0;
    }
    
    for (EvoScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        return score.Kills;
      }
    }
    
    return 0;
  }
  
  private void SortScores()
  {
    for (int i = 0; i < this._ranks.size(); i++)
    {
      for (int j = this._ranks.size() - 1; j > 0; j--)
      {
        if (((EvoScore)this._ranks.get(j)).Kills > ((EvoScore)this._ranks.get(j - 1)).Kills)
        {
          EvoScore temp = (EvoScore)this._ranks.get(j);
          this._ranks.set(j, (EvoScore)this._ranks.get(j - 1));
          this._ranks.set(j - 1, temp);
        }
      }
    }
  }
  
  private void End()
  {
    SortScores();
    

    this._places.clear();
    for (int i = 0; i < this._ranks.size(); i++) {
      this._places.add(i, ((EvoScore)this._ranks.get(i)).Player);
    }
    
    if (this._ranks.size() >= 1) {
      AddGems(((EvoScore)this._ranks.get(0)).Player, 20.0D, "1st Place", false);
    }
    if (this._ranks.size() >= 2) {
      AddGems(((EvoScore)this._ranks.get(1)).Player, 15.0D, "2nd Place", false);
    }
    if (this._ranks.size() >= 3) {
      AddGems(((EvoScore)this._ranks.get(2)).Player, 10.0D, "3rd Place", false);
    }
    
    for (Player player : GetPlayers(false)) {
      if (player.isOnline())
        AddGems(player, 10.0D, "Participation", false);
    }
    SetState(Game.GameState.End);
    AnnounceEnd(this._places);
  }
  



  public void EndCheck() {}
  


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
    


    for (Player player : GetPlayers(true))
    {
      int score = GetScore(player);
      
      String out = score + " " + mineplex.core.common.util.C.cGreen + player.getName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(score);
    }
  }
}
