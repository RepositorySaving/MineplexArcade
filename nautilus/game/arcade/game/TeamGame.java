package nautilus.game.arcade.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class TeamGame
  extends Game
{
  private NautHashMap<String, Long> _rejoinTime = new NautHashMap();
  protected NautHashMap<String, GameTeam> RejoinTeam = new NautHashMap();
  
  public TeamGame(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc)
  {
    super(manager, gameType, kits, gameDesc);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerQuit(PlayerQuitEvent event)
  {
    if (!InProgress()) {
      return;
    }
    Player player = event.getPlayer();
    
    GameTeam team = GetTeam(player);
    if (team == null) { return;
    }
    if (!team.IsAlive(player)) {
      return;
    }
    team.RemovePlayer(player);
    
    if (player.isDead()) {
      return;
    }
    if (!this.QuitOut)
    {

      this._rejoinTime.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
      this.RejoinTeam.put(player.getName(), team);
      GetLocationStore().put(player.getName(), player.getLocation());
      

      Announce(team.GetColor() + C.Bold + player.getName() + " has disconnected! 3 minutes to rejoin.");
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerLoginAllow(PlayerLoginEvent event)
  {
    if ((!InProgress()) || (this.QuitOut)) {
      return;
    }
    
    GameTeam team = (GameTeam)this.RejoinTeam.remove(event.getPlayer().getName());
    if ((team != null) && (this._rejoinTime.remove(event.getPlayer().getName()) != null))
    {
      team.AddPlayer(event.getPlayer());
      Announce(team.GetColor() + C.Bold + event.getPlayer().getName() + " has reconnected!");
      return;
    }
  }
  










  @EventHandler
  public void PlayerRejoinExpire(UpdateEvent event)
  {
    if ((event.getType() != UpdateType.SEC) || (this.QuitOut)) {
      return;
    }
    Iterator<String> rejoinIterator = this._rejoinTime.keySet().iterator();
    
    while (rejoinIterator.hasNext())
    {
      String name = (String)rejoinIterator.next();
      
      if (UtilTime.elapsed(((Long)this._rejoinTime.get(name)).longValue(), 180000L))
      {

        rejoinIterator.remove();
        

        GameTeam team = (GameTeam)this.RejoinTeam.remove(name);
        if (team != null)
          Announce(team.GetColor() + C.Bold + name + " did not reconnect in time!");
      }
    }
  }
  
  @EventHandler
  public void RejoinCommand(PlayerCommandPreprocessEvent event) {
    if ((!this.QuitOut) && (event.getPlayer().isOp()) && (event.getMessage().startsWith("/allowrejoin")))
    {
      String[] toks = event.getMessage().split(" ");
      
      if (toks.length <= 1)
      {
        event.getPlayer().sendMessage("Missing Param!");
      }
      else
      {
        this._rejoinTime.put(toks[1], Long.valueOf(System.currentTimeMillis()));
        event.getPlayer().sendMessage("Allowed " + toks[1] + " to rejoin!");
      }
      
      event.setCancelled(true);
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
    if (!this.QuitOut)
    {

      for (GameTeam team : this.RejoinTeam.values()) {
        teamsAlive.add(team);
      }
    }
    if (teamsAlive.size() <= 1)
    {

      if (teamsAlive.size() > 0) {
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
