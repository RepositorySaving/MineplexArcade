package mineplex.core.creature.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.creature.Creature;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public class MobCommand extends MultiCommandBase<Creature>
{
  public MobCommand(Creature plugin)
  {
    super(plugin, Rank.ADMIN, new String[] { "mob" });
    
    AddCommand(new KillCommand((Creature)this.Plugin));
  }
  
  protected void Help(Player caller, String[] args)
  {
    Iterator localIterator2;
    if (args == null)
    {
      HashMap<EntityType, Integer> entMap = new HashMap();
      
      int count = 0;
      for (Iterator localIterator1 = mineplex.core.common.util.UtilServer.getServer().getWorlds().iterator(); localIterator1.hasNext(); 
          
          localIterator2.hasNext())
      {
        World world = (World)localIterator1.next();
        
        localIterator2 = world.getEntities().iterator(); continue;Entity ent = (Entity)localIterator2.next();
        
        if (!entMap.containsKey(ent.getType())) {
          entMap.put(ent.getType(), Integer.valueOf(0));
        }
        entMap.put(ent.getType(), Integer.valueOf(1 + ((Integer)entMap.get(ent.getType())).intValue()));
        count++;
      }
      

      UtilPlayer.message(caller, F.main(((Creature)this.Plugin).GetName(), "Listing Entities:"));
      for (EntityType cur : entMap.keySet())
      {
        UtilPlayer.message(caller, F.desc(UtilEnt.getName(cur), entMap.get(cur)));
      }
      
      UtilPlayer.message(caller, F.desc("Total", count));
    }
    else
    {
      EntityType type = UtilEnt.searchEntity(caller, args[0], true);
      
      if (type == null) {
        return;
      }
      UtilPlayer.message(caller, F.main(((Creature)this.Plugin).GetName(), "Spawning Creature(s);"));
      

      HashSet<String> argSet = new HashSet();
      for (int i = 1; i < args.length; i++) {
        if (args[i].length() > 0) {
          argSet.add(args[i]);
        }
      }
      
      int count = 1;
      Object argHandle = new HashSet();
      for (String arg : argSet)
      {
        try
        {
          int newCount = Integer.parseInt(arg);
          
          if (newCount > 0)
          {


            count = newCount;
            UtilPlayer.message(caller, F.desc("Amount", count));
            

            ((HashSet)argHandle).add(arg);
          }
        }
        catch (Exception localException1) {}
      }
      


      for (String arg : (HashSet)argHandle) {
        argSet.remove(arg);
      }
      
      HashSet<Entity> entSet = new HashSet();
      for (int i = 0; i < count; i++)
      {
        entSet.add(((Creature)this.Plugin).SpawnEntity(caller.getTargetBlock(null, 0).getLocation().add(0.5D, 1.0D, 0.5D), type));
      }
      

      for (String arg : argSet)
      {
        if (arg.length() != 0)
        {


          if ((arg.equalsIgnoreCase("baby")) || (arg.equalsIgnoreCase("b")))
          {
            for (Entity ent : entSet)
            {
              if ((ent instanceof Ageable)) {
                ((Ageable)ent).setBaby();
              } else if ((ent instanceof Zombie)) {
                ((Zombie)ent).setBaby(true);
              }
            }
            UtilPlayer.message(caller, F.desc("Baby", "True"));
            ((HashSet)argHandle).add(arg);


          }
          else if ((arg.equalsIgnoreCase("age")) || (arg.equalsIgnoreCase("lock")))
          {
            for (Entity ent : entSet) {
              if ((ent instanceof Ageable))
              {
                ((Ageable)ent).setAgeLock(true);
                UtilPlayer.message(caller, F.desc("Age", "False"));
              }
            }
            ((HashSet)argHandle).add(arg);


          }
          else if ((arg.equalsIgnoreCase("angry")) || (arg.equalsIgnoreCase("a")))
          {
            for (Entity ent : entSet) {
              if ((ent instanceof Wolf))
                ((Wolf)ent).setAngry(true);
            }
            for (Entity ent : entSet) {
              if ((ent instanceof Skeleton))
                ((Skeleton)ent).setSkeletonType(org.bukkit.entity.Skeleton.SkeletonType.WITHER);
            }
            UtilPlayer.message(caller, F.desc("Angry", "True"));
            ((HashSet)argHandle).add(arg);
          }
          else {
            Entity ent;
            if (arg.toLowerCase().charAt(0) == 'p')
            {
              try
              {
                String prof = arg.substring(1, arg.length());
                
                Villager.Profession profession = null;
                for (Villager.Profession cur : Villager.Profession.values()) {
                  if (cur.name().toLowerCase().contains(prof.toLowerCase()))
                    profession = cur;
                }
                UtilPlayer.message(caller, F.desc("Profession", profession.name()));
                
                for (Iterator localIterator4 = entSet.iterator(); localIterator4.hasNext();) { ent = (Entity)localIterator4.next();
                  if ((ent instanceof Villager)) {
                    ((Villager)ent).setProfession(profession);
                  }
                }
              } catch (Exception e) {
                UtilPlayer.message(caller, F.desc("Profession", "Invalid [" + arg + "] on " + type.name()));
              }
              ((HashSet)argHandle).add(arg);


            }
            else if (arg.toLowerCase().charAt(0) == 's')
            {
              try
              {
                String size = arg.substring(1, arg.length());
                
                UtilPlayer.message(caller, F.desc("Size", Integer.parseInt(size)));
                
                for (Entity ent : entSet) {
                  if ((ent instanceof Slime)) {
                    ((Slime)ent).setSize(Integer.parseInt(size));
                  }
                }
              } catch (Exception e) {
                UtilPlayer.message(caller, F.desc("Size", "Invalid [" + arg + "] on " + type.name()));
              }
              ((HashSet)argHandle).add(arg);

            }
            else if ((arg.toLowerCase().charAt(0) == 'n') && (arg.length() > 1))
            {
              try
              {
                String name = "";
                char[] arrayOfChar;
                Entity localEntity1 = (arrayOfChar = arg.substring(1, arg.length()).toCharArray()).length; for (ent = 0; ent < localEntity1; ent++) { char c = arrayOfChar[ent];
                  
                  if (c != '_') {
                    name = name + c;
                  } else {
                    name = name + " ";
                  }
                }
                for (Entity ent : entSet)
                {
                  if ((ent instanceof CraftLivingEntity))
                  {
                    CraftLivingEntity cEnt = (CraftLivingEntity)ent;
                    cEnt.setCustomName(name);
                    cEnt.setCustomNameVisible(true);
                  }
                }
              }
              catch (Exception e)
              {
                UtilPlayer.message(caller, F.desc("Size", "Invalid [" + arg + "] on " + type.name()));
              }
              ((HashSet)argHandle).add(arg);
            }
          } } }
      for (String arg : (HashSet)argHandle) {
        argSet.remove(arg);
      }
      for (String arg : argSet) {
        UtilPlayer.message(caller, F.desc("Unhandled", arg));
      }
      
      UtilPlayer.message(caller, F.main(((Creature)this.Plugin).GetName(), "Spawned " + count + " " + UtilEnt.getName(type) + "."));
    }
  }
}
