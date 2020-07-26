package nautilus.game.arcade.game.games.hideseek.forms;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguiseCat;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.core.disguise.disguises.DisguiseSheep;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.hideseek.HideSeek;
import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.Entity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CreatureForm extends Form
{
  private EntityType _type;
  private DisguiseBase _disguise;
  
  public CreatureForm(HideSeek host, Player player, EntityType entityType)
  {
    super(host, player);
    
    this._type = entityType;
    
    Apply();
  }
  

  public void Apply()
  {
    Material icon = Material.PORK;
    
    if (this._type == EntityType.CHICKEN) { this._disguise = new DisguiseChicken(this.Player);icon = Material.FEATHER;
    } else if (this._type == EntityType.COW) { this._disguise = new mineplex.core.disguise.disguises.DisguiseCow(this.Player);icon = Material.LEATHER;
    } else if (this._type == EntityType.SHEEP) { this._disguise = new DisguiseSheep(this.Player);icon = Material.WOOL;
    } else if (this._type == EntityType.PIG) { this._disguise = new mineplex.core.disguise.disguises.DisguisePig(this.Player);icon = Material.PORK;
    }
    this._disguise.setSoundDisguise(new DisguiseCat(this.Player));
    this.Host.Manager.GetDisguise().disguise(this._disguise);
    
    ((CraftEntity)this.Player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)0));
    

    UtilPlayer.message(this.Player, F.main("Game", mineplex.core.common.util.C.cWhite + "You are now a " + F.elem(mineplex.core.common.util.UtilEnt.getName(this._type)) + "!"));
    

    this.Player.getInventory().setItem(8, new org.bukkit.inventory.ItemStack(this.Host.GetItemEquivilent(icon)));
    mineplex.core.common.util.UtilInv.Update(this.Player);
    

    this.Player.playSound(this.Player.getLocation(), org.bukkit.Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
  }
  

  public void Remove()
  {
    this.Host.Manager.GetDisguise().undisguise(this.Player);
    
    ((CraftEntity)this.Player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)0));
  }
}
