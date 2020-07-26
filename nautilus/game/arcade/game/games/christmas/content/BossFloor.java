package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.parts.Part5;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class BossFloor
{
  private Part5 Host;
  private boolean _active = false;
  private int _difficulty = 0;
  

  private HashMap<Location, Byte> _floor;
  
  private boolean _inProgress = false;
  private int _state = 0;
  private long _stateTime = 0L;
  private byte _color = 0;
  private long _last = 0L;
  private Location _restoreLoc = null;
  
  public BossFloor(Part5 host, ArrayList<Location> floor)
  {
    this.Host = host;
    
    this._floor = new HashMap();
    
    for (Location loc : floor)
    {
      loc.getBlock().setType(Material.AIR);
      
      loc.add(0.0D, 1.0D, 0.0D);
      
      this._floor.put(loc, Byte.valueOf(loc.getBlock().getData()));
    }
  }
  
  public void SetActive(boolean active, int difficulty)
  {
    this._active = active;
    this._difficulty = difficulty;
  }
  
  public void Remove(byte ignore)
  {
    for (Location loc : this._floor.keySet())
    {
      if (((Byte)this._floor.get(loc)).byteValue() != ignore)
      {

        MapUtil.QuickChangeBlockAt(loc, Material.AIR);
      }
    }
  }
  
  public void Restore() {
    for (Location loc : this._floor.keySet())
    {
      if (loc.getBlock().getType() == Material.AIR) {
        MapUtil.QuickChangeBlockAt(loc, 35, ((Byte)this._floor.get(loc)).byteValue());
      }
    }
  }
  
  public void Update() {
    ChatColor textColor;
    if ((this._active) && (this._state == 0) && (UtilTime.elapsed(this._last, 6000 - 1000 * this._difficulty)))
    {
      for (Player player : UtilServer.getPlayers()) {
        player.setExp(0.0F);
      }
      this._state = 1;
      
      this._last = System.currentTimeMillis();
      
      this._color = this.Host.GetBoss().GetEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getData();
      
      String color = "White";
      textColor = ChatColor.WHITE;
      
      if (this._color == 1) { color = "Orange";textColor = ChatColor.GOLD;
      } else if (this._color == 2) { color = "Purple";textColor = ChatColor.LIGHT_PURPLE;
      } else if (this._color == 3) { color = "Blue";textColor = ChatColor.BLUE;
      } else if (this._color == 4) { color = "Yellow";textColor = ChatColor.YELLOW;
      } else if (this._color == 5) { color = "Green";textColor = ChatColor.GREEN;
      } else if (this._color == 6) { color = "Pink";textColor = ChatColor.RED;
      } else if (this._color == 7) { color = "Gray";textColor = ChatColor.GRAY;
      } else if (this._color == 8) { color = "Gray";textColor = ChatColor.GRAY;
      } else if (this._color == 9) { color = "Blue";textColor = ChatColor.BLUE;
      } else if (this._color == 10) { color = "Purple";textColor = ChatColor.LIGHT_PURPLE;
      } else if (this._color == 11) { color = "Blue";textColor = ChatColor.BLUE;
      } else if (this._color == 12) { color = "Brown";textColor = ChatColor.DARK_GRAY;
      } else if (this._color == 13) { color = "Green";textColor = ChatColor.GREEN;
      } else if (this._color == 14) { color = "Red";textColor = ChatColor.RED;
      } else if (this._color == 15) { color = "Black";textColor = ChatColor.BLACK;
      }
      this.Host.Host.SantaSay("Stay on " + textColor + C.Bold + color.toUpperCase());
      
      this._restoreLoc = this.Host.GetBoss().GetEntity().getLocation().getBlock().getLocation().add(0.5D, 0.0D, 0.5D);
      

      for (Player player : UtilServer.getPlayers())
        for (int i = 3; i < 9; i++)
          player.getInventory().setItem(i, ItemStackFactory.Instance.CreateStack(Material.WOOL, this._color));
    } else {
      Player player;
      if (this._state == 1)
      {
        long req = 4000 - 2000 * this._difficulty;
        
        for (player : UtilServer.getPlayers()) {
          player.setExp(Math.min(0.99F, Math.max(0.0F, (float)((req - (System.currentTimeMillis() - this._last)) / req))));
        }
        if (UtilTime.elapsed(this._last, req))
        {
          this._last = System.currentTimeMillis();
          
          this._state = 2;
          
          Remove(this._color);
        }
        
      }
      else if (this._state == 2) {
        Player[] arrayOfPlayer2;
        ChatColor localChatColor3 = (arrayOfPlayer2 = UtilServer.getPlayers()).length; for (ChatColor localChatColor1 = 0; localChatColor1 < localChatColor3; localChatColor1++) { Player player = arrayOfPlayer2[localChatColor1];
          player.setExp(0.99F);
        }
        if (UtilTime.elapsed(this._last, 2000 - 1000 * this._difficulty))
        {
          this._last = System.currentTimeMillis();
          
          this._state = 0;
          
          Restore();
          
          this.Host.GetBoss().GetEntity().teleport(this._restoreLoc);
          
          ChatColor localChatColor4 = (arrayOfPlayer2 = UtilServer.getPlayers()).length; for (ChatColor localChatColor2 = 0; localChatColor2 < localChatColor4; localChatColor2++) { Player player = arrayOfPlayer2[localChatColor2];
            player.getInventory().remove(Material.WOOL);
          }
        }
      }
    }
  }
  
  public boolean ShouldBossMove() { return this._state == 0; }
}
