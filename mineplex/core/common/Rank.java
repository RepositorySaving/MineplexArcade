package mineplex.core.common;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public enum Rank
{
  OWNER("Owner", ChatColor.DARK_RED), 
  DEVELOPER("Dev", ChatColor.RED), 
  ADMIN("Admin", ChatColor.RED), 
  MODERATOR("Mod", ChatColor.GOLD), 
  HELPER("Helper", ChatColor.GREEN), 
  MAPDEV("Mapper", ChatColor.BLUE), 
  YOUTUBE("Legend", ChatColor.DARK_GREEN), 
  HERO("Hero", ChatColor.LIGHT_PURPLE), 
  ULTRA("Ultra", ChatColor.AQUA), 
  ALL("All", ChatColor.YELLOW);
  
  private ChatColor Color;
  public String Name;
  
  private Rank(String name, ChatColor color)
  {
    this.Color = color;
    this.Name = name;
  }
  
  public boolean Has(Rank rank)
  {
    return Has(null, rank, false);
  }
  
  public boolean Has(Player player, Rank rank, boolean inform)
  {
    if (compareTo(rank) <= 0) {
      return true;
    }
    if (inform)
    {
      UtilPlayer.message(player, C.mHead + "Permissions > " + 
        C.mBody + "This requires Permission Rank [" + 
        C.mHead + rank + 
        C.mBody + "].");
    }
    
    return false;
  }
  
  public String GetTag(boolean bold, boolean uppercase)
  {
    String name = this.Name;
    if (uppercase) {
      name = this.Name.toUpperCase();
    }
    if (bold) return this.Color + C.Bold + name;
    return this.Color + name;
  }
}
