package nautilus.game.arcade.game;

import java.util.ArrayList;
import mineplex.core.common.util.C;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;


public abstract class SoloGame
  extends Game
{
  protected ArrayList<Player> _places = new ArrayList();
  
  public SoloGame(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc)
  {
    super(manager, gameType, kits, gameDesc);
  }
  
  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    ((GameTeam)GetTeamList().get(0)).SetColor(ChatColor.YELLOW);
    ((GameTeam)GetTeamList().get(0)).SetName("Players");
  }
  
  @EventHandler
  public void EndStateChange(PlayerStateChangeEvent event)
  {
    if (event.GetState() == GameTeam.PlayerState.OUT) {
      if (!this._places.contains(event.GetPlayer())) {
        this._places.add(0, event.GetPlayer());
      }
      else {
        this._places.remove(event.GetPlayer());
      }
    }
  }
  
  public void EndCheck() {
    if (!IsLive()) {
      return;
    }
    
    if (GetPlayers(true).size() == 1)
    {
      SetPlayerState((Player)GetPlayers(true).get(0), GameTeam.PlayerState.OUT);
      return;
    }
    
    if (GetPlayers(true).size() <= 0)
    {

      AnnounceEnd(this._places);
      

      if (this._places.size() >= 1) {
        AddGems((Player)this._places.get(0), 20.0D, "1st Place", false);
      }
      if (this._places.size() >= 2) {
        AddGems((Player)this._places.get(1), 15.0D, "2nd Place", false);
      }
      if (this._places.size() >= 3) {
        AddGems((Player)this._places.get(2), 10.0D, "3rd Place", false);
      }
      for (Player player : GetPlayers(false)) {
        if (player.isOnline()) {
          AddGems(player, 10.0D, "Participation", false);
        }
      }
      SetState(Game.GameState.End);
    }
  }
  


  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (GetTeamList().isEmpty()) {
      return;
    }
    GameTeam team = (GameTeam)GetTeamList().get(0);
    
    GetObjectiveSide().getScore(team.GetColor() + "Alive").setScore(team.GetPlayers(true).size());
    GetObjectiveSide().getScore(C.cRed + "Dead").setScore(team.GetPlayers(false).size() - team.GetPlayers(true).size());
  }
  
  public ArrayList<Player> GetPlaces()
  {
    return this._places;
  }
  
  public int GetScoreboardScore(Player player)
  {
    return 0;
  }
}
