package nautilus.game.arcade.game.games.common.dominate_data;


public class PlayerData
{
  public String Name;
  public int Kills = 0;
  public int Deaths = 0;
  public int Assists = 0;
  
  public double DamageDealt = 0.0D;
  public double DamageTaken = 0.0D;
  
  public int CaptureScore = 0;
  
  public PlayerData(String name)
  {
    this.Name = name;
  }
}
