package nautilus.game.arcade.game.games.draw;

import java.util.ArrayList;
import java.util.HashSet;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;



public class DrawRound
{
  public Draw Host;
  public Player Drawer;
  public String Word;
  public int Revealed = 0;
  public boolean[] WordReveal;
  public long Time;
  public double TimeMax = 45.0D;
  public HashSet<String> Guessed = new HashSet();
  
  public DrawRound(Draw draw, Player player, String word)
  {
    this.Host = draw;
    
    this.Drawer = player;
    this.Word = word;
    this.Time = System.currentTimeMillis();
    
    this.WordReveal = new boolean[word.length()];
    for (int i = 0; i < this.WordReveal.length; i++) {
      this.WordReveal[i] = false;
    }
  }
  
  public boolean Guessed(Player player) {
    if (this.Guessed.add(player.getName()))
    {
      this.TimeMax -= 5.0D;
      return true;
    }
    
    return false;
  }
  
  public void UpdateReveal()
  {
    int required = (int)((System.currentTimeMillis() - this.Time) / 1000.0D / (this.TimeMax * 1.75D) * this.Word.length());
    
    if (this.Revealed < required)
    {
      for (int i = 0; i < 50; i++)
      {
        int j = UtilMath.r(this.WordReveal.length);
        
        if ((this.WordReveal[j] == 0) && (this.Word.charAt(j) != ' '))
        {
          this.WordReveal[j] = true;
          this.Revealed += 1;
          
          for (Player player : UtilServer.getPlayers())
          {
            if (!this.Host.GetTeam(ChatColor.RED).HasPlayer(player))
            {

              player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5F, 2.0F);
            }
          }
          break;
        }
      }
    }
  }
  
  public String GetRevealedWord()
  {
    String out = "";
    
    for (int i = 0; i < this.Word.length(); i++)
    {
      if (this.Word.charAt(i) == ' ')
      {
        out = out + "  ";
      }
      else if (this.WordReveal[i] != 0)
      {
        out = out + this.Word.charAt(i) + " ";
      }
      else
      {
        out = out + "_ ";
      }
    }
    
    if (out.length() > 0) {
      out = out.substring(0, out.length() - 1);
    }
    return out;
  }
  
  public boolean IsDone()
  {
    UpdateReveal();
    
    return UtilTime.elapsed(this.Time, (this.TimeMax * 1000.0D));
  }
  
  public double GetTimePercent()
  {
    return (this.TimeMax - (System.currentTimeMillis() - this.Time) / 1000.0D) / 45.0D;
  }
  
  public boolean AllGuessed(ArrayList<Player> players)
  {
    for (Player player : players) {
      if (!this.Guessed.contains(player.getName()))
        return false;
    }
    return true;
  }
}
