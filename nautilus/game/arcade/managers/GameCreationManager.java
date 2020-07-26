package nautilus.game.arcade.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import mineplex.core.antistack.AntiStack;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.creature.Creature;
import mineplex.core.explosion.Explosion;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class GameCreationManager implements org.bukkit.event.Listener
{
  ArcadeManager Manager;
  private ArrayList<Game> _ended = new ArrayList();
  
  private GameType _nextGame = null;
  private HashMap<String, ChatColor> _nextGameTeams = null;
  
  private String _lastMap = "";
  private ArrayList<GameType> _lastGames = new ArrayList();
  
  public GameCreationManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  public String GetLastMap()
  {
    return this._lastMap;
  }
  
  public void SetLastMap(String file)
  {
    this._lastMap = file;
  }
  
  @org.bukkit.event.EventHandler
  public void NextGame(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGameList().isEmpty()) {
      return;
    }
    while (this._lastGames.size() > this.Manager.GetGameList().size() - 1) {
      this._lastGames.remove(this._lastGames.size() - 1);
    }
    if ((this.Manager.GetGame() == null) && (this._ended.isEmpty()))
    {
      CreateGame(null);
    }
    

    if (this.Manager.GetGame() != null)
    {
      if (this.Manager.GetGame().GetState() == nautilus.game.arcade.game.Game.GameState.Dead)
      {
        HandlerList.unregisterAll(this.Manager.GetGame());
        

        this._ended.add(this.Manager.GetGame());
        

        this.Manager.GetLobby().DisplayLast(this.Manager.GetGame());
        
        this.Manager.SetGame(null);
      }
    }
    

    Iterator<Game> gameIterator = this._ended.iterator();
    
    while (gameIterator.hasNext())
    {
      Game game = (Game)gameIterator.next();
      
      HandlerList.unregisterAll(game);
      

      if (game.WorldData == null)
      {
        gameIterator.remove();


      }
      else if (game.WorldData.World == null)
      {
        gameIterator.remove();

      }
      else
      {
        if (mineplex.core.common.util.UtilTime.elapsed(game.GetStateTime(), 10000L))
        {
          for (Player player : game.WorldData.World.getPlayers()) {
            player.kickPlayer("Dead World");
          }
        }
        
        if (game.WorldData.World.getPlayers().isEmpty())
        {
          game.WorldData.Uninitialize();
          game.WorldData = null;
        }
      }
    }
  }
  
  private void CreateGame(GameType gameType)
  {
    this.Manager.GetDamage().DisableDamageChanges = false;
    this.Manager.GetCreature().SetDisableCustomDrops(false);
    this.Manager.GetDamage().SetEnabled(true);
    this.Manager.GetExplosion().SetRegenerate(false);
    this.Manager.GetExplosion().SetTNTSpread(true);
    this.Manager.GetAntiStack().SetEnabled(true);
    
    HashMap<String, ChatColor> pastTeams = null;
    

    if ((this._nextGame != null) && (this._nextGameTeams != null))
    {
      gameType = this._nextGame;
      pastTeams = this._nextGameTeams;
      
      this._nextGame = null;
      this._nextGameTeams = null;
    }
    

    if (gameType == null)
    {
      for (int i = 0; i < 50; i++)
      {
        gameType = (GameType)this.Manager.GetGameList().get(UtilMath.r(this.Manager.GetGameList().size()));
        
        if (!this._lastGames.contains(gameType)) {
          break;
        }
      }
    }
    this._lastGames.add(0, gameType);
    

    this.Manager.SetGame(this.Manager.GetGameFactory().CreateGame(gameType, pastTeams));
    
    if (this.Manager.GetGame() == null)
    {
      return;
    }
    
    this.Manager.GetLobby().DisplayNext(this.Manager.GetGame(), pastTeams);
    
    UtilServer.getServer().getPluginManager().registerEvents(this.Manager.GetGame(), this.Manager.GetPlugin());
  }
}
