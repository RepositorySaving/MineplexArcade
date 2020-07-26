package nautilus.game.arcade.kit;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



public abstract class SmashKit
  extends Kit
{
  public SmashKit(ArcadeManager manager, String name, KitAvailability kitAvailability, String[] kitDesc, Perk[] kitPerks, EntityType entityType, ItemStack itemInHand)
  {
    super(manager, name, kitAvailability, kitDesc, kitPerks, entityType, itemInHand);
  }
  
  public void DisplayDesc(Player player)
  {
    for (int i = 0; i < 3; i++) {
      UtilPlayer.message(player, "");
    }
    UtilPlayer.message(player, ArcadeFormat.Line);
    
    UtilPlayer.message(player, "§aKit - §f§l" + GetName());
    

    for (String line : GetDesc())
    {
      UtilPlayer.message(player, C.cGray + "  " + line);
    }
    

    for (Perk perk : GetPerks())
    {
      if (perk.IsVisible())
      {

        for (String line : perk.GetDesc())
        {
          UtilPlayer.message(player, C.cGray + "  " + line);
        }
      }
    }
    UtilPlayer.message(player, ArcadeFormat.Line);
  }
  


  public int GetCost()
  {
    return 3000;
  }
}
