package mineplex.minecraft.game.classcombat.Class.event;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassSetupEvent
  extends Event
{
  public static enum SetupType
  {
    OpenMain, 
    
    ApplyDefaultBuilt, 
    
    OpenCustomBuilds, 
    
    ApplyCustomBuild, 
    SaveEditCustomBuild, 
    EditCustomBuild, 
    DeleteCustomBuild, 
    
    Close;
  }
  
  private static final HandlerList handlers = new HandlerList();
  
  private Player _player;
  private SetupType _actionType;
  private IPvpClass.ClassType _classType;
  private int _position = 0;
  
  private CustomBuildToken _classBuild;
  private boolean _cancelled = false;
  
  public ClassSetupEvent(Player player, SetupType type, IPvpClass.ClassType classType, int position, CustomBuildToken build)
  {
    this._player = player;
    
    this._actionType = type;
    this._classType = classType;
    
    this._position = position;
    
    this._classBuild = build;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public SetupType GetType()
  {
    return this._actionType;
  }
  
  public int GetPosition()
  {
    return this._position;
  }
  
  public Player GetPlayer()
  {
    return this._player;
  }
  
  public IPvpClass.ClassType GetClassType()
  {
    return this._classType;
  }
  
  public CustomBuildToken GetBuild()
  {
    return this._classBuild;
  }
  
  public void SetCancelled(boolean cancel)
  {
    this._cancelled = cancel;
  }
  
  public boolean IsCancelled()
  {
    return this._cancelled;
  }
}
