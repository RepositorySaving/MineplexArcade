package nautilus.game.arcade.game.games.squidshooter;

import java.util.ArrayList;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.quiver.QuiverScore;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.Objective;

public class SquidShooter extends SoloGame
{
  private ArrayList<QuiverScore> _ranks = new ArrayList();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  














  public SquidShooter(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.SquidShooter, new Kit[] {new nautilus.game.arcade.game.games.squidshooter.kits.KitRifle(manager), new nautilus.game.arcade.game.games.squidshooter.kits.KitShotgun(manager), new nautilus.game.arcade.game.games.squidshooter.kits.KitSniper(manager) }, new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Attack", C.cYellow + "Hold Crouch" + C.cGray + " to use " + C.cGreen + "Squid Swim", C.cYellow + "Tap Crouch Quickly" + C.cGray + " to use " + C.cGreen + "Squid Thrust", "First player to 20 kills wins." });
    

    this.DeathOut = false;
    this.DamageSelf = false;
    this.DamageTeamSelf = true;
    this.PrepareFreeze = false;
    this.SpawnDistanceRequirement = 16;
    this.CompassEnabled = true;
    this.KitRegisterState = Game.GameState.Prepare;
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    GetObjectiveSide().setDisplayName(C.cWhite + C.Bold + "First to " + C.cGold + C.Bold + "20 Kills");
  }
  
  @EventHandler
  public void Death(CombatDeathEvent event)
  {
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    if (!event.GetLog().GetKiller().IsPlayer()) {
      return;
    }
    Player player = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (player == null) {
      return;
    }
    
    AddKill(player);
  }
  

  public void AddKill(Player player)
  {
    for (QuiverScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        score.Kills += 1;
        EndCheck();
        return;
      }
    }
    
    this._ranks.add(new QuiverScore(player, 1));
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
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    for (String string : this._lastScoreboard)
    {
      GetScoreboard().resetScores(string);
    }
    this._lastScoreboard.clear();
    
    SortScores();
    

    for (QuiverScore score : this._ranks)
    {
      String out = score.Kills + " " + C.cGreen + score.Player.getName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(score.Kills);
    }
  }
  
  @EventHandler
  public void AirDamage(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      if (player.getLocation().getBlock().isLiquid())
      {
        player.setFoodLevel(20);

      }
      else
      {
        if (player.getFoodLevel() == 0) {
          player.damage(1.0D);
        }
        
        UtilPlayer.hunger(player, -1);
        

        if (mineplex.core.common.util.UtilEnt.isGrounded(player)) {
          this.Manager.GetCondition().Factory().Slow("On Land", player, player, 0.9D, 2, false, false, false, false);
        }
      }
    }
  }
  
  public void EndCheck() {
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
      
      for (Player player : GetPlayers(false)) {
        if (player.isOnline())
          AddGems(player, 10.0D, "Participation", false);
      }
      SetState(Game.GameState.End);
      AnnounceEnd(this._places);
    }
  }
}
