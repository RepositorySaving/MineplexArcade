package nautilus.game.arcade.game.games.christmas.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.UtilAlg;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.parts.Part5;
import net.minecraft.server.v1_7_R3.ControllerMove;
import net.minecraft.server.v1_7_R3.EntityCreature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class BossMobs
{
  private Part5 Host;
  private boolean _active = false;
  private int _difficulty = 0;
  
  private ArrayList<Location> _spawns;
  
  private long _lastSpawn;
  private HashMap<Creature, Player> _ents = new HashMap();
  
  public BossMobs(Part5 host, ArrayList<Location> spawns)
  {
    this.Host = host;
    
    this._spawns = spawns;
  }
  
  public void SetActive(boolean active, int difficulty)
  {
    this._active = active;
    this._difficulty = difficulty;
  }
  
  public void Update()
  {
    MoveDieHit();
    
    if (!this._active) {
      return;
    }
    
    if (!mineplex.core.common.util.UtilTime.elapsed(this._lastSpawn, 1500 - 250 * this._difficulty))
      return;
    this._lastSpawn = System.currentTimeMillis();
    

    this.Host.Host.CreatureAllowOverride = true;
    Creature ent = (Creature)((Location)UtilAlg.Random(this._spawns)).getWorld().spawn((Location)UtilAlg.Random(this._spawns), Skeleton.class);
    this.Host.Host.CreatureAllowOverride = false;
    

    double r = Math.random();
    if (r > 0.66D) { ent.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
    } else if (r > 0.33D) ent.getEquipment().setItemInHand(new ItemStack(Material.IRON_AXE)); else {
      ent.getEquipment().setItemInHand(new ItemStack(Material.BOW));
    }
    ent.setHealth(5.0D);
    

    this._ents.put(ent, null);
  }
  
  private void MoveDieHit()
  {
    Iterator<Creature> entIterator = this._ents.keySet().iterator();
    

    while (entIterator.hasNext())
    {
      Creature ent = (Creature)entIterator.next();
      

      Player target = (Player)this._ents.get(ent);
      if ((target == null) || (!target.isValid()) || (!this.Host.Host.IsAlive(target)))
      {
        if (this.Host.Host.GetPlayers(true).size() > 0)
        {
          target = (Player)UtilAlg.Random(this.Host.Host.GetPlayers(true));
          this._ents.put(ent, target);

        }
        


      }
      else
      {

        EntityCreature ec = ((CraftCreature)ent).getHandle();
        ec.getControllerMove().a(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 1.2D + 0.3D * this._difficulty);
        

        if (!ent.isValid())
        {
          ent.remove();
          entIterator.remove();
        }
      }
    }
  }
}
