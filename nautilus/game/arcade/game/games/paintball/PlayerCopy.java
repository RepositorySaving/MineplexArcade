package nautilus.game.arcade.game.games.paintball;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import nautilus.game.arcade.game.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;

public class PlayerCopy
{
  private Paintball Host;
  private Skeleton _ent;
  private Player _owner;
  
  public PlayerCopy(Paintball host, Player owner)
  {
    this.Host = host;
    
    this._owner = owner;
    
    this.Host.CreatureAllowOverride = true;
    this._ent = ((Skeleton)owner.getWorld().spawn(owner.getLocation(), Skeleton.class));
    this.Host.CreatureAllowOverride = false;
    
    UtilEnt.ghost(this._ent, true, false);
    
    UtilEnt.Vegetate(this._ent);
    

    this._ent.getEquipment().setArmorContents(owner.getInventory().getArmorContents());
    
    this._ent.setCustomName(C.cWhite + C.Bold + C.Scramble + "XX" + ChatColor.RESET + " " + host.GetTeam(owner).GetColor() + owner.getName() + " " + C.cWhite + C.Bold + C.Scramble + "XX");
    this._ent.setCustomNameVisible(true);
  }
  




  public LivingEntity GetEntity()
  {
    return this._ent;
  }
  
  public Player GetPlayer()
  {
    return this._owner;
  }
}
