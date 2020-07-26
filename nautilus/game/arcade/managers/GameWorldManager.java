package nautilus.game.arcade.managers;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.PluginManager;

public class GameWorldManager implements Listener
{
  ArcadeManager Manager;
  private HashSet<WorldData> _worldLoader = new HashSet();
  
  public GameWorldManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler
  public void LoadWorldChunks(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<WorldData> worldIterator = this._worldLoader.iterator();
    
    long endTime = System.currentTimeMillis() + 25L;
    
    while (worldIterator.hasNext())
    {
      long timeLeft = endTime - System.currentTimeMillis();
      if (timeLeft > 0L)
      {
        WorldData worldData = (WorldData)worldIterator.next();
        
        if (worldData.World == null)
        {
          worldIterator.remove();
        }
        else if (worldData.LoadChunks(timeLeft))
        {
          worldData.Host.SetState(Game.GameState.Recruit);
          worldIterator.remove();
        }
      }
    }
  }
  









  @EventHandler
  public void ChunkUnload(ChunkUnloadEvent event)
  {
    if (event.getWorld().getName().equals("world"))
    {
      event.setCancelled(true);
      return;
    }
    
    if ((this.Manager.GetGame() != null) && 
      (this.Manager.GetGame().WorldData != null)) {
      this.Manager.GetGame().WorldData.ChunkUnload(event);
    }
  }
  
  public void RegisterWorld(WorldData worldData) {
    this._worldLoader.add(worldData);
  }
}
