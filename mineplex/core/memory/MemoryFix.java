package mineplex.core.memory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class MemoryFix
{
  private JavaPlugin _plugin;
  
  public MemoryFix(JavaPlugin plugin)
  {
    this._plugin = plugin;
    
    this._plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this._plugin, new Runnable()
    {
      public void run() {
        Iterator localIterator2;
        for (Iterator localIterator1 = org.bukkit.Bukkit.getWorlds().iterator(); localIterator1.hasNext(); 
            
            localIterator2.hasNext())
        {
          World world = (World)localIterator1.next();
          
          localIterator2 = ((CraftWorld)world).getHandle().tileEntityList.iterator(); continue;Object tileEntity = localIterator2.next();
          
          if ((tileEntity instanceof IInventory))
          {
            Iterator<HumanEntity> entityIterator = ((IInventory)tileEntity).getViewers().iterator();
            
            while (entityIterator.hasNext())
            {
              HumanEntity entity = (HumanEntity)entityIterator.next();
              
              if (((entity instanceof CraftPlayer)) && (!((CraftPlayer)entity).isOnline()))
              {
                entityIterator.remove();
              }
              
            }
          }
        }
      }
    }, 100L, 100L);
  }
}
