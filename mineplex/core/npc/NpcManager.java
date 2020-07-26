package mineplex.core.npc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import mineplex.core.MiniPlugin;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.creature.event.CreatureKillEntitiesEvent;
import net.minecraft.server.v1_7_R3.EntityAgeable;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftAgeable;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class NpcManager extends MiniPlugin
{
  private mineplex.core.creature.Creature _creature;
  private NautHashMap<String, NpcEntry> _npcs;
  private NautHashMap<String, Integer> _failedAttempts;
  private NautHashMap<String, NpcEntry> _addTempList;
  private HashSet<String> _delTempList;
  
  public NpcManager(JavaPlugin plugin, mineplex.core.creature.Creature creature)
  {
    super("NpcManager", plugin);
    
    this._creature = creature;
    this._npcs = new NautHashMap();
    this._failedAttempts = new NautHashMap();
    this._addTempList = new NautHashMap();
    this._delTempList = new HashSet();
    
    this._plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this._plugin, new Runnable()
    {
      public void run()
      {
        NpcManager.this.UpdateNpcLocations();
      }
    }, 0L, 5L);
    
    this._plugin.getServer().getScheduler().scheduleSyncDelayedTask(this._plugin, new Runnable()
    {
      public void run()
      {
        NpcManager.this.ReattachNpcs();
      }
    }, 100L);
    
    this._plugin.getServer().getPluginManager().registerEvents(this, this._plugin);
    
    LoadNpcs();
  }
  
  public void AddCommands()
  {
    AddCommand(new mineplex.core.npc.Commands.NpcCommand(this));
  }
  
  public void Help(Player caller, String message)
  {
    UtilPlayer.message(caller, F.main(this._moduleName, "Commands List:"));
    UtilPlayer.message(caller, F.help("/npc add <radius> <name>", "Right click mob to attach npc.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc del ", "Right click npc to delete", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc clear", "Removes all npcs", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc home", " Teleport npcs to home locations.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc reattach", "Attempt to reattach npcs to entities.", Rank.OWNER));
    
    if (message != null) {
      UtilPlayer.message(caller, F.main(this._moduleName, org.bukkit.ChatColor.RED + message));
    }
  }
  
  public void Help(Player caller) {
    Help(caller, null);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void OnEntityDamage(EntityDamageEvent event)
  {
    if (this._npcs.containsKey(event.getEntity().getUniqueId().toString()))
    {
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void OnCreatureKillEntities(CreatureKillEntitiesEvent event)
  {
    Iterator<Entity> entityIterator = event.GetEntities().iterator();
    
    while (entityIterator.hasNext())
    {
      if (this._npcs.containsKey(((Entity)entityIterator.next()).getUniqueId().toString()))
      {
        entityIterator.remove();
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void OnEntityTarget(EntityTargetEvent event)
  {
    if (this._npcs.containsKey(event.getEntity().getUniqueId().toString()))
    {
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void OnEntityCombust(EntityCombustEvent event)
  {
    if (this._npcs.containsKey(event.getEntity().getUniqueId().toString()))
    {
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void OnChunkLoad(ChunkLoadEvent event)
  {
    for (Entity entity : event.getChunk().getEntities())
    {
      if (this._npcs.containsKey(entity.getUniqueId().toString()))
      {
        ((NpcEntry)this._npcs.get(entity.getUniqueId().toString())).Name = ((LivingEntity)entity).getCustomName();
        ((NpcEntry)this._npcs.get(entity.getUniqueId().toString())).Entity = entity;
        UtilEnt.silence(entity, true);
        UtilEnt.ghost(entity, true, false);
        
        if (((NpcEntry)this._npcs.get(entity.getUniqueId().toString())).Radius == 0)
        {
          UtilEnt.Vegetate(entity);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
  {
    if ((event.getRightClicked() instanceof LivingEntity))
    {
      if (this._addTempList.containsKey(event.getPlayer().getName()))
      {
        if (event.getRightClicked().getType() == EntityType.PLAYER)
        {
          event.getPlayer().sendMessage(F.main(GetName(), "Failed to add npc.  Can't attach to player."));
        }
        else
        {
          LivingEntity npc = (LivingEntity)event.getRightClicked();
          
          if (((NpcEntry)this._addTempList.get(event.getPlayer().getName())).Name != null)
          {
            npc.setCustomName(((NpcEntry)this._addTempList.get(event.getPlayer().getName())).Name);
            npc.setCustomNameVisible(true);
          }
          
          npc.getEquipment().setArmorContents(event.getPlayer().getInventory().getArmorContents());
          npc.getEquipment().setItemInHand(event.getPlayer().getItemInHand());
          npc.setCanPickupItems(false);
          ((EntityInsentient)((CraftLivingEntity)npc).getHandle()).persistent = true;
          
          if ((npc instanceof mineplex.core.creature.Creature))
          {
            ((org.bukkit.entity.Creature)npc).setTarget(null);
          }
          
          AddNpc(npc, (NpcEntry)this._addTempList.get(event.getPlayer().getName()), true);
          event.getPlayer().sendMessage(F.main(GetName(), "Added npc"));
        }
        
        this._addTempList.remove(event.getPlayer().getName());
      }
      else if (this._delTempList.contains(event.getPlayer().getName()))
      {
        if (DeleteNpc(event.getRightClicked()))
        {
          event.getPlayer().sendMessage(F.main(GetName(), "Deleted npc."));
        }
        else
        {
          event.getPlayer().sendMessage(F.main(GetName(), "Failed to delete npc.  That one isn't in the list."));
        }
        
        this._delTempList.remove(event.getPlayer().getName());
      }
      
      if (this._npcs.containsKey(event.getRightClicked().getUniqueId().toString()))
      {
        event.setCancelled(true);
        return;
      }
    }
  }
  
  public void SetNpcInfo(Player admin, int radius, String name, Location location)
  {
    this._addTempList.put(admin.getName(), new NpcEntry(name, null, radius, location));
  }
  
  public Entity AddNpc(EntityType entityType, int radius, String name, Location location)
  {
    LivingEntity entity = (LivingEntity)this._creature.SpawnEntity(location, entityType);
    
    entity.setCustomName(name);
    entity.setCustomNameVisible(true);
    
    entity.setCanPickupItems(false);
    entity.setRemoveWhenFarAway(false);
    ((EntityInsentient)((CraftLivingEntity)entity).getHandle()).persistent = true;
    
    if ((((CraftLivingEntity)entity).getHandle() instanceof EntityAgeable))
    {
      ((CraftAgeable)entity).getHandle().ageLocked = true;
    }
    
    if ((entity instanceof mineplex.core.creature.Creature))
    {
      ((org.bukkit.entity.Creature)entity).setTarget(null);
    }
    
    return AddNpc(entity, new NpcEntry(name, null, radius, location), true);
  }
  
  public Entity AddNpc(LivingEntity entity, NpcEntry entry, boolean save)
  {
    entry.Entity = entity;
    this._npcs.put(entity.getUniqueId().toString(), entry);
    
    if (entry.Radius == 0)
    {
      UtilEnt.Vegetate(entry.Entity);
      UtilEnt.silence(entry.Entity, true);
    }
    
    if (save) {
      SaveNpcs();
    }
    return entity;
  }
  
  public boolean DeleteNpc(Entity entity)
  {
    if ((entity instanceof LivingEntity))
    {
      if (this._npcs.containsKey(entity.getUniqueId().toString()))
      {
        entity.remove();
        this._npcs.remove(entity.getUniqueId().toString());
        
        return true;
      }
    }
    
    return false;
  }
  
  public void PrepDeleteNpc(Player admin)
  {
    this._delTempList.add(admin.getName());
  }
  
  public void ClearNpcs()
  {
    Iterator<String> npcIterator = this._npcs.keySet().iterator();
    
    while (npcIterator.hasNext())
    {
      String id = (String)npcIterator.next();
      
      if (((NpcEntry)this._npcs.get(id)).Entity != null) {
        ((NpcEntry)this._npcs.get(id)).Entity.remove();
      }
      npcIterator.remove();
    }
    
    SaveNpcs();
  }
  
  private void UpdateNpcLocations()
  {
    for (NpcEntry npc : this._npcs.values())
    {
      if (npc.Entity != null)
      {



        npc.Entity.setTicksLived(1);
        ((EntityInsentient)((CraftLivingEntity)npc.Entity).getHandle()).persistent = true;
        UtilEnt.silence(npc.Entity, true);
        
        if ((IsNpcChunkLoaded(npc.Entity)) && ((npc.Entity instanceof org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature)))
        {



          if ((!npc.Entity.isDead()) && (npc.Entity.isValid()))
          {
            String uuid = npc.Entity.getUniqueId().toString();
            
            ((LivingEntity)npc.Entity).getEquipment().getArmorContents()[0].setDurability((short)0);
            ((LivingEntity)npc.Entity).getEquipment().getArmorContents()[1].setDurability((short)0);
            ((LivingEntity)npc.Entity).getEquipment().getArmorContents()[2].setDurability((short)0);
            ((LivingEntity)npc.Entity).getEquipment().getArmorContents()[3].setDurability((short)0);
            
            if (!this._failedAttempts.containsKey(uuid)) {
              this._failedAttempts.put(uuid, Integer.valueOf(0));
            }
            if (((Integer)this._failedAttempts.get(uuid)).intValue() >= 10)
            {
              npc.Entity.teleport(npc.Location);
              this._failedAttempts.put(uuid, Integer.valueOf(0));
            }
            else if (!npc.IsInRadius())
            {
              npc.ReturnToPost();
              this._failedAttempts.put(uuid, Integer.valueOf(((Integer)this._failedAttempts.get(uuid)).intValue() + 1));
            }
            else
            {
              if (npc.Returning())
              {
                npc.ClearGoals();
              }
              
              this._failedAttempts.put(uuid, Integer.valueOf(0));
            }
          } }
      }
    }
  }
  
  public void TeleportNpcsHome() {
    for (NpcEntry npc : this._npcs.values())
    {
      if (npc.Entity != null)
      {



        if (IsNpcChunkLoaded(npc.Entity))
        {



          if ((!npc.Entity.isDead()) && (npc.Entity.isValid()))
          {
            npc.Entity.teleport(npc.Location);
            this._failedAttempts.put(npc.Entity.getUniqueId().toString(), Integer.valueOf(0));
          } }
      }
    }
  }
  
  public void ReattachNpcs() {
    for (Entity entity : UtilWorld.getWorldType(World.Environment.NORMAL).getEntities())
    {
      if (this._npcs.containsKey(entity.getUniqueId().toString()))
      {
        ((NpcEntry)this._npcs.get(entity.getUniqueId().toString())).Name = ((LivingEntity)entity).getCustomName();
        ((NpcEntry)this._npcs.get(entity.getUniqueId().toString())).Entity = entity;
      }
    }
  }
  
  public boolean IsNpcChunkLoaded(Entity entity)
  {
    return entity.getWorld().isChunkLoaded(entity.getLocation().getBlockX() >> 4, entity.getLocation().getBlockZ() >> 4);
  }
  
  public void LoadNpcs()
  {
    FileInputStream fstream = null;
    BufferedReader br = null;
    
    try
    {
      File npcFile = new File("npcs.dat");
      
      if (npcFile.exists())
      {
        fstream = new FileInputStream(npcFile);
        br = new BufferedReader(new InputStreamReader(fstream));
        
        String line = br.readLine();
        
        while (line != null)
        {
          UUID uuid = UUID.fromString(line.split(" ")[0]);
          String location = line.split(" ")[1];
          Integer radius = Integer.valueOf(Integer.parseInt(line.split(" ")[2]));
          
          this._npcs.put(uuid.toString(), new NpcEntry(null, null, radius.intValue(), UtilWorld.strToLoc(location)));
          
          line = br.readLine();
        }
      }
    }
    catch (Exception e)
    {
      System.out.println(F.main(GetName(), "Error parsing npc file."));
      


      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public void SaveNpcs()
  {
    FileWriter fstream = null;
    BufferedWriter out = null;
    
    try
    {
      fstream = new FileWriter("npcs.dat");
      out = new BufferedWriter(fstream);
      
      for (String key : this._npcs.keySet())
      {
        out.write(key + " " + UtilWorld.locToStr(((NpcEntry)this._npcs.get(key)).Location) + " " + ((NpcEntry)this._npcs.get(key)).Radius);
        out.newLine();
      }
      
      out.close();
    }
    catch (Exception e)
    {
      System.err.println("Npc Save Error: " + e.getMessage());
      


      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public NpcEntry GetNpcByUUID(UUID uniqueId)
  {
    return (NpcEntry)this._npcs.get(uniqueId.toString());
  }
}
