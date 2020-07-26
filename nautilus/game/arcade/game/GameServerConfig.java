package nautilus.game.arcade.game;

import java.util.ArrayList;
import nautilus.game.arcade.GameType;


public class GameServerConfig
{
  public String ServerType = null;
  public int MinPlayers = -1;
  public int MaxPlayers = -1;
  public ArrayList<GameType> GameList = new ArrayList();
  
  public boolean IsValid()
  {
    return (this.ServerType != null) && (this.MinPlayers != -1) && (this.MaxPlayers != -1);
  }
}
