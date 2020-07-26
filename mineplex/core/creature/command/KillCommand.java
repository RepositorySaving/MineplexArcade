package mineplex.core.creature.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.creature.Creature;
import mineplex.core.creature.event.CreatureKillEntitiesEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class KillCommand extends CommandBase<Creature>
{
  public KillCommand(Creature plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "kill", "k" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if ((args == null) || (args.length == 0))
    {
      UtilPlayer.message(caller, F.main(((Creature)this.Plugin).GetName(), "Missing Entity Type Parameter."));
      return;
    }
    
    EntityType type = UtilEnt.searchEntity(caller, args[0], true);
    
    if ((type == null) && (!args[0].equalsIgnoreCase("all"))) {
      return;
    }
    int count = 0;
    List<Entity> killList = new ArrayList();
    Iterator localIterator2;
    Entity ent; for (Iterator localIterator1 = UtilServer.getServer().getWorlds().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      World world = (World)localIterator1.next();
      
      localIterator2 = world.getEntities().iterator(); continue;ent = (Entity)localIterator2.next();
      
      if (ent.getType() != EntityType.PLAYER)
      {

        if ((type == null) || (ent.getType() == type))
        {
          killList.add(ent);
        }
      }
    }
    
    CreatureKillEntitiesEvent event = new CreatureKillEntitiesEvent(killList);
    ((Creature)this.Plugin).GetPlugin().getServer().getPluginManager().callEvent(event);
    
    for (Entity entity : event.GetEntities())
    {
      entity.remove();
      count++;
    }
    
    String target = "ALL";
    if (type != null) {
      target = UtilEnt.getName(type);
    }
    UtilPlayer.message(caller, F.main(((Creature)this.Plugin).GetName(), "Killed " + target + ". " + count + " Removed."));
  }
}
