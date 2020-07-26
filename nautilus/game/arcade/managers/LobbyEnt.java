package nautilus.game.arcade.managers;

import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;


public class LobbyEnt
{
  private Kit _kit;
  private GameTeam _team;
  private Entity _ent;
  private Location _loc;
  
  public LobbyEnt(Entity ent, Location loc, Kit kit)
  {
    this._ent = ent;
    this._loc = loc;
    this._kit = kit;
  }
  
  public LobbyEnt(Entity ent, Location loc, GameTeam team)
  {
    this._ent = ent;
    this._loc = loc;
    this._team = team;
  }
  
  public Kit GetKit()
  {
    return this._kit;
  }
  
  public GameTeam GetTeam()
  {
    return this._team;
  }
  
  public Entity GetEnt()
  {
    return this._ent;
  }
  
  public Location GetLocation()
  {
    return this._loc;
  }
}
