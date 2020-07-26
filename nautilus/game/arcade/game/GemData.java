package nautilus.game.arcade.game;

public class GemData
{
  public double Gems;
  public int Amount;
  
  public GemData(double gems, boolean amount)
  {
    this.Gems = gems;
    
    if (amount) {
      this.Amount = 1;
    }
  }
  
  public void AddGems(double gems) {
    this.Gems += gems;
    
    if (this.Amount > 0) {
      this.Amount += 1;
    }
  }
}
