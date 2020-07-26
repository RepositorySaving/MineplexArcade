package mineplex.core.creature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.creature.command.MobCommand;
import mineplex.core.creature.event.CreatureSpawnCustomEvent;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Creature extends MiniPlugin
{
  private boolean _spawnForce = false;
  private boolean _disableCustom = true;
  
  public Creature(JavaPlugin plugin)
  {
    super("Creature", plugin);
  }
  

  public void AddCommands()
  {
    AddCommand(new MobCommand(this));
  }
  
  public Entity SpawnEntity(Location location, EntityType entityType)
  {
    this._spawnForce = true;
    Entity entity = location.getWorld().spawnEntity(location, entityType);
    this._spawnForce = false;
    
    return entity;
  }
  
  @EventHandler
  public void Spawn(CreatureSpawnEvent event)
  {
    if (this._disableCustom) {
      return;
    }
    if ((event.getEntity() instanceof LivingEntity)) {
      event.getEntity().setCanPickupItems(false);
    }
    if (this._spawnForce) {
      return;
    }
    
    if (event.getEntityType() == EntityType.SQUID)
    {
      event.setCancelled(true);
      return;
    }
    
    CreatureSpawnCustomEvent customEvent = new CreatureSpawnCustomEvent(event.getLocation());
    
    this._plugin.getServer().getPluginManager().callEvent(customEvent);
    
    if (customEvent.isCancelled())
    {
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler
  public void Death(EntityDeathEvent event)
  {
    if (this._disableCustom) {
      return;
    }
    event.setDroppedExp(0);
    List<org.bukkit.inventory.ItemStack> drops = event.getDrops();
    

    if (event.getEntityType() == EntityType.PLAYER) {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 1));
    } else {
      drops.clear();
    }
    
    if (event.getEntityType() == EntityType.CHICKEN)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RAW_CHICKEN, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.FEATHER, 2 + UtilMath.r(5)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 1));

    }
    else if (event.getEntityType() == EntityType.COW)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RAW_BEEF, 1 + UtilMath.r(4)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.LEATHER, 2 + UtilMath.r(3)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 3 + UtilMath.r(4)));
    }
    
    if (event.getEntityType() == EntityType.MUSHROOM_COW)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RAW_BEEF, 1 + UtilMath.r(4)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RED_MUSHROOM, 2 + UtilMath.r(3)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 3 + UtilMath.r(4)));

    }
    else if (event.getEntityType() == EntityType.OCELOT)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RAW_BEEF, 1 + UtilMath.r(2)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RAW_FISH, 2 + UtilMath.r(7)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 1 + UtilMath.r(2)));

    }
    else if (event.getEntityType() == EntityType.PIG)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.PORK, 1 + UtilMath.r(2)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 2 + UtilMath.r(2)));

    }
    else if (event.getEntityType() == EntityType.SHEEP)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.RAW_BEEF, 1 + UtilMath.r(3)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.WOOL, 1 + UtilMath.r(4)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 2 + UtilMath.r(3)));

    }
    else if (event.getEntityType() == EntityType.VILLAGER)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 2 + UtilMath.r(3)));


    }
    else if (event.getEntityType() == EntityType.BLAZE)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BLAZE_ROD, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 6 + UtilMath.r(7)));

    }
    else if (event.getEntityType() == EntityType.CAVE_SPIDER)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.WEB, 2 + UtilMath.r(3)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.SPIDER_EYE, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 6 + UtilMath.r(7)));

    }
    else if (event.getEntityType() == EntityType.CREEPER)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.COAL, 6 + UtilMath.r(13)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 12 + UtilMath.r(13)));

    }
    else if (event.getEntityType() == EntityType.ENDERMAN)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.ENDER_PEARL, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 12 + UtilMath.r(13)));

    }
    else if (event.getEntityType() == EntityType.GHAST)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.GHAST_TEAR, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 36 + UtilMath.r(37)));
      for (int i = 0; i < 5 + UtilMath.r(11); i++) {
        drops.add(ItemStackFactory.Instance.CreateStack(Material.EMERALD, 1));
      }
    }
    else if (event.getEntityType() == EntityType.IRON_GOLEM)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.IRON_INGOT, 2 + UtilMath.r(3)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 12 + UtilMath.r(13)));

    }
    else if (event.getEntityType() == EntityType.MAGMA_CUBE)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.MAGMA_CREAM, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 1 + UtilMath.r(2)));

    }
    else if (event.getEntityType() == EntityType.PIG_ZOMBIE)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.GRILLED_PORK, 1 + UtilMath.r(2)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.ROTTEN_FLESH, 1 + UtilMath.r(2)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 2 + UtilMath.r(2)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.ARROW, 1 + UtilMath.r(12)));
      if (UtilMath.r(100) > 90) { drops.add(ItemStackFactory.Instance.CreateStack(Material.GOLD_SWORD, 1));
      }
    }
    else if (event.getEntityType() == EntityType.SILVERFISH)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 1 + UtilMath.r(2)));

    }
    else if (event.getEntityType() == EntityType.SKELETON)
    {
      if (((Skeleton)event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.NORMAL)
      {
        drops.add(ItemStackFactory.Instance.CreateStack(Material.ARROW, 4 + UtilMath.r(5)));
        drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 12 + UtilMath.r(13)));
      }
      else
      {
        drops.add(ItemStackFactory.Instance.CreateStack(Material.ARROW, 4 + UtilMath.r(10)));
        drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 12 + UtilMath.r(26)));
      }
      

    }
    else if (event.getEntityType() == EntityType.SLIME)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 1 + UtilMath.r(2)));

    }
    else if (event.getEntityType() == EntityType.SPIDER)
    {
      drops.add(ItemStackFactory.Instance.CreateStack(Material.WEB, 2 + UtilMath.r(3)));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.SPIDER_EYE, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 6 + UtilMath.r(7)));

    }
    else if (event.getEntityType() == EntityType.ZOMBIE)
    {
      event.getDrops().add(ItemStackFactory.Instance.CreateStack(Material.ROTTEN_FLESH, 1));
      drops.add(ItemStackFactory.Instance.CreateStack(Material.BONE, 6 + UtilMath.r(7)));
    }
  }
  
  @EventHandler
  public void CustomCreeperExplode(EntityExplodeEvent event)
  {
    if (this._disableCustom) {
      return;
    }
    if (!(event.getEntity() instanceof Creeper)) {
      return;
    }
    HashMap<Player, Double> players = UtilPlayer.getInRadius(event.getEntity().getLocation(), 8.0D);
    for (Player cur : players.keySet())
    {
      org.bukkit.util.Vector vec = UtilAlg.getTrajectory(event.getEntity().getLocation(), cur.getLocation());
      UtilAction.velocity(cur, vec, 1.0D + 2.0D * ((Double)players.get(cur)).doubleValue(), false, 0.0D, 0.5D + 1.0D * ((Double)players.get(cur)).doubleValue(), 2.0D, true);
    }
  }
  
  public void SetForce(boolean force)
  {
    this._spawnForce = force;
  }
  
  public void AddEntityName(LivingEntity ent, String name)
  {
    if (ent == null) {
      return;
    }
    UtilEnt.GetEntityNames().put(ent, name);
  }
  
  @EventHandler
  public void UpdateEntityNames(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    HashSet<Entity> remove = new HashSet();
    
    for (Entity ent : UtilEnt.GetEntityNames().keySet()) {
      if ((ent.isDead()) || (!ent.isValid()))
        remove.add(ent);
    }
    for (Entity ent : remove) {
      UtilEnt.GetEntityNames().remove(ent);
    }
  }
  
  public void SetDisableCustomDrops(boolean var) {
    this._disableCustom = var;
  }
}
