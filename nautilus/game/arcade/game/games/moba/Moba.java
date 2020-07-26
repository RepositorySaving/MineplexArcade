package nautilus.game.arcade.game.games.moba;

import java.util.ArrayList;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;


public class Moba
  extends TeamGame
{
  private ArrayList<String> _lastScoreboard = new ArrayList();
  









  public Moba(ArcadeManager manager)
  {
    super(manager, GameType.Sheep, new Kit[] {new NullKit(manager) }, new String[] {"..." });
    

    this.DeathOut = false;
    this.DeathSpectateSecs = 8.0D;
    
    this.HungerSet = 20;
  }
  
  public void ParseData() {}
}
