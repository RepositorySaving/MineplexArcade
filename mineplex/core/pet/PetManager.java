package mineplex.core.pet;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.pet.repository.PetRepository;
import mineplex.core.pet.repository.token.ClientPetTokenWrapper;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.Navigation;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public class PetManager
  extends MiniClientPlugin<PetClient>
{
  private static Object _petOwnerSynch = new Object();
  private static Object _petRenameSynch = new Object();
  
  private mineplex.core.creature.Creature _creatureModule;
  
  private PetRepository _repository;
  
  private PetFactory _petFactory;
  
  private NautHashMap<String, org.bukkit.entity.Creature> _activePetOwners;
  
  private NautHashMap<String, Integer> _failedAttempts;
  private PetShop _petShop;
  private Field _goalSelector;
  private Field _targetSelector;
  private NautHashMap<String, EntityType> _petOwnerQueue = new NautHashMap();
  private NautHashMap<String, String> _petRenameQueue = new NautHashMap();
  
  public PetManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, mineplex.core.creature.Creature creatureModule, String webAddress)
  {
    super("Pet Manager", plugin);
    
    this._creatureModule = creatureModule;
    this._repository = new PetRepository(webAddress);
    this._petFactory = new PetFactory(this._repository);
    this._petShop = new PetShop(this, clientManager, donationManager);
    
    this._activePetOwners = new NautHashMap();
    this._failedAttempts = new NautHashMap();
  }
  
  public void addPetOwnerToQueue(String playerName, EntityType entityType)
  {
    synchronized (_petOwnerSynch)
    {
      this._petOwnerQueue.put(playerName, entityType);
    }
  }
  
  public void addRenamePetToQueue(String playerName, String petName)
  {
    synchronized (_petRenameSynch)
    {
      this._petRenameQueue.put(playerName, petName);
    }
  }
  
  @EventHandler
  public void processQueues(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    synchronized (_petOwnerSynch)
    {
      for (String playerName : this._petOwnerQueue.keySet())
      {
        Player player = Bukkit.getPlayerExact(playerName);
        
        if ((player != null) && (player.isOnline()))
        {
          AddPetOwner(player, (EntityType)this._petOwnerQueue.get(playerName), player.getLocation());
        }
      }
      
      this._petOwnerQueue.clear();
    }
    
    synchronized (this._petRenameQueue)
    {
      for (String playerName : this._petRenameQueue.keySet())
      {
        Player player = Bukkit.getPlayerExact(playerName);
        
        if ((player != null) && (player.isOnline()))
        {
          getActivePet(playerName).setCustomNameVisible(true);
          getActivePet(playerName).setCustomName((String)this._petRenameQueue.get(playerName));
        }
      }
      
      this._petRenameQueue.clear();
    }
  }
  
  public void AddPetOwner(Player player, EntityType entityType, Location location)
  {
    if (this._activePetOwners.containsKey(player.getName()))
    {
      if (((org.bukkit.entity.Creature)this._activePetOwners.get(player.getName())).getType() != entityType)
      {
        RemovePet(player, true);
      }
      else {
        return;
      }
    }
    org.bukkit.entity.Creature pet = (org.bukkit.entity.Creature)this._creatureModule.SpawnEntity(location, entityType);
    pet.setCustomNameVisible(true);
    pet.setCustomName((String)((PetClient)Get(player)).GetPets().get(entityType));
    
    this._activePetOwners.put(player.getName(), pet);
    this._failedAttempts.put(player.getName(), Integer.valueOf(0));
    
    if ((pet instanceof Ageable))
    {
      ((Ageable)pet).setBaby();
      ((Ageable)pet).setAgeLock(true);
    }
    
    ClearPetGoals(pet);
  }
  
  public org.bukkit.entity.Creature GetPet(Player player)
  {
    return (org.bukkit.entity.Creature)this._activePetOwners.get(player.getName());
  }
  
  public void RemovePet(Player player, boolean removeOwner)
  {
    if (this._activePetOwners.containsKey(player.getName()))
    {
      org.bukkit.entity.Creature pet = (org.bukkit.entity.Creature)this._activePetOwners.get(player.getName());
      pet.remove();
      
      if (removeOwner)
      {
        this._activePetOwners.remove(player.getName());
      }
    }
  }
  
  @EventHandler
  public void TryToAddPetOwner(PlayerInteractEvent event)
  {
    if ((event.hasItem()) && (event.getItem().getType() == Material.BONE))
    {
      this._petShop.attemptShopOpen(event.getPlayer());
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void preventWolfBone(PlayerInteractEntityEvent event)
  {
    if (event.getPlayer().getItemInHand().getType() == Material.BONE)
    {
      event.setCancelled(true);
      event.getPlayer().updateInventory();
    }
  }
  
  @EventHandler
  public void orderThatBone(final PlayerDropItemEvent event)
  {
    if (event.getItemDrop().getItemStack().getType() == Material.BONE)
    {
      Bukkit.getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
      {
        public void run()
        {
          if (event.getPlayer().isOnline())
          {
            event.getPlayer().getInventory().remove(Material.BONE);
            event.getPlayer().getInventory().setItem(3, ItemStackFactory.Instance.CreateStack(Material.BONE, (byte)0, 1, ChatColor.RESET + C.cGreen + "Pet Menu"));
            event.getPlayer().updateInventory();
          }
        }
      });
    }
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    event.getPlayer().getInventory().setItem(3, ItemStackFactory.Instance.CreateStack(Material.BONE, (byte)0, 1, ChatColor.RESET + C.cGreen + "Pet Menu"));
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    RemovePet(event.getPlayer(), true);
  }
  
  @EventHandler
  public void onEntityTarget(EntityTargetEvent event)
  {
    if (((event.getEntity() instanceof org.bukkit.entity.Creature)) && (this._activePetOwners.containsValue((org.bukkit.entity.Creature)event.getEntity())))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onEntityDamage(EntityDamageEvent event)
  {
    if (((event.getEntity() instanceof org.bukkit.entity.Creature)) && (this._activePetOwners.containsValue((org.bukkit.entity.Creature)event.getEntity())))
    {
      if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
      {
        String playerName = null;
        
        for (Map.Entry<String, org.bukkit.entity.Creature> entry : this._activePetOwners.entrySet())
        {
          if (entry.getValue() == event.getEntity()) {
            playerName = (String)entry.getKey();
          }
        }
        if (playerName != null)
        {
          Player player = Bukkit.getPlayerExact(playerName);
          
          if ((player != null) && (player.isOnline()))
          {
            RemovePet(player, true);
          }
        }
      }
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    



    Iterator<String> ownerIterator = this._activePetOwners.keySet().iterator();
    
    while (ownerIterator.hasNext())
    {
      String playerName = (String)ownerIterator.next();
      Player owner = Bukkit.getPlayer(playerName);
      
      org.bukkit.entity.Creature pet = (org.bukkit.entity.Creature)this._activePetOwners.get(playerName);
      Location petSpot = pet.getLocation();
      Location ownerSpot = owner.getLocation();
      int xDiff = Math.abs(petSpot.getBlockX() - ownerSpot.getBlockX());
      int yDiff = Math.abs(petSpot.getBlockY() - ownerSpot.getBlockY());
      int zDiff = Math.abs(petSpot.getBlockZ() - ownerSpot.getBlockZ());
      
      if (xDiff + yDiff + zDiff > 4)
      {
        EntityCreature ec = ((CraftCreature)pet).getHandle();
        Navigation nav = ec.getNavigation();
        
        int xIndex = -1;
        int zIndex = -1;
        Block targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
        while ((targetBlock.isEmpty()) || (targetBlock.isLiquid()))
        {
          if (xIndex < 2) {
            xIndex++;
          } else if (zIndex < 2)
          {
            xIndex = -1;
            zIndex++;
          }
          else {
            return;
          }
          targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
        }
        
        if (((Integer)this._failedAttempts.get(playerName)).intValue() > 4)
        {
          pet.teleport(owner);
          this._failedAttempts.put(playerName, Integer.valueOf(0));
        }
        else if (!nav.a(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ(), 1.5D))
        {
          if (pet.getFallDistance() == 0.0F)
          {
            this._failedAttempts.put(playerName, Integer.valueOf(((Integer)this._failedAttempts.get(playerName)).intValue() + 1));
          }
        }
        else
        {
          this._failedAttempts.put(playerName, Integer.valueOf(0));
        }
      }
    }
  }
  
  private void ClearPetGoals(org.bukkit.entity.Creature pet)
  {
    try
    {
      this._goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
      this._goalSelector.setAccessible(true);
      this._targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
      this._targetSelector.setAccessible(true);
      
      EntityCreature creature = ((CraftCreature)pet).getHandle();
      
      PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(((CraftWorld)pet.getWorld()).getHandle().methodProfiler);
      
      goalSelector.a(0, new PathfinderGoalLookAtPlayer(creature, EntityHuman.class, 6.0F));
      goalSelector.a(1, new PathfinderGoalRandomLookaround(creature));
      
      this._goalSelector.set(creature, goalSelector);
      this._targetSelector.set(creature, new PathfinderGoalSelector(((CraftWorld)pet.getWorld()).getHandle().methodProfiler));
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (NoSuchFieldException e)
    {
      e.printStackTrace();
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
  }
  
  @EventHandler
  public void OnClientWebResponse(ClientWebResponseEvent event)
  {
    ClientPetTokenWrapper token = (ClientPetTokenWrapper)new Gson().fromJson(event.GetResponse(), ClientPetTokenWrapper.class);
    
    ((PetClient)Get(token.Name)).Load(token.DonorToken);
  }
  

  protected PetClient AddPlayer(String player)
  {
    return new PetClient();
  }
  
  public PetFactory GetFactory()
  {
    return this._petFactory;
  }
  
  public PetRepository GetRepository()
  {
    return this._repository;
  }
  
  public boolean hasActivePet(String name)
  {
    return this._activePetOwners.containsKey(name);
  }
  
  public org.bukkit.entity.Creature getActivePet(String name)
  {
    return (org.bukkit.entity.Creature)this._activePetOwners.get(name);
  }
}
