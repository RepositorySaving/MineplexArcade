package mineplex.core.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import net.minecraft.server.v1_7_R3.ControllerMove;
import net.minecraft.server.v1_7_R3.EntityBat;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.EntityEnderDragon;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.Navigation;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class UtilEnt
{
  private static HashMap<org.bukkit.entity.Entity, String> _nameMap = new HashMap();
  private static HashMap<String, EntityType> creatureMap = new HashMap();
  
  private static Field _goalSelector;
  private static Field _targetSelector;
  private static Field _bsRestrictionGoal;
  
  public static HashMap<org.bukkit.entity.Entity, String> GetEntityNames()
  {
    return _nameMap;
  }
  
  public static void silence(org.bukkit.entity.Entity entity, boolean silence)
  {
    ((CraftEntity)entity).getHandle().Silent = silence;
  }
  
  public static void ghost(org.bukkit.entity.Entity entity, boolean ghost, boolean invisible)
  {
    if ((entity instanceof LivingEntity))
    {
      ((CraftLivingEntity)entity).getHandle().ghost = ghost;
    }
    
    ((CraftEntity)entity).getHandle().Invisible = invisible;
    ((CraftEntity)entity).getHandle().setInvisible(invisible);
  }
  
  public static void Leash(LivingEntity leashed, org.bukkit.entity.Entity holder, boolean pull, boolean breakable)
  {
    if ((((CraftEntity)leashed).getHandle() instanceof EntityInsentient))
    {
      EntityInsentient creature = (EntityInsentient)((CraftEntity)leashed).getHandle();
      
      creature.PullWhileLeashed = pull;
      creature.BreakLeash = breakable;
    }
    
    leashed.setLeashHolder(holder);
  }
  
  public static void Vegetate(org.bukkit.entity.Entity entity)
  {
    Vegetate(entity, false);
  }
  
  public static void Vegetate(org.bukkit.entity.Entity entity, boolean mute)
  {
    try
    {
      if (_goalSelector == null)
      {
        _goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
        _goalSelector.setAccessible(true);
      }
      
      if (_targetSelector == null)
      {
        _targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
        _targetSelector.setAccessible(true);
      }
      
      if ((entity instanceof CraftCreature))
      {
        EntityCreature creature = ((CraftCreature)entity).getHandle();
        
        if (_bsRestrictionGoal == null)
        {
          _bsRestrictionGoal = EntityCreature.class.getDeclaredField("bs");
          _bsRestrictionGoal.setAccessible(true);
        }
        
        _bsRestrictionGoal.set(creature, new PathfinderGoalMoveTowardsRestriction(creature, 0.0D));
      }
      
      if ((((CraftEntity)entity).getHandle() instanceof EntityInsentient))
      {
        EntityInsentient creature = (EntityInsentient)((CraftEntity)entity).getHandle();
        
        creature.Vegetated = true;
        creature.Silent = mute;
        PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(((CraftWorld)entity.getWorld()).getHandle().methodProfiler);
        
        goalSelector.a(7, new PathfinderGoalLookAtPlayer(creature, EntityHuman.class, 6.0F));
        goalSelector.a(7, new net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround(creature));
        
        _goalSelector.set(creature, goalSelector);
        _targetSelector.set(creature, new PathfinderGoalSelector(((CraftWorld)entity.getWorld()).getHandle().methodProfiler));
      }
      
      if ((((CraftEntity)entity).getHandle() instanceof EntityBat))
      {
        ((EntityBat)((CraftEntity)entity).getHandle()).Vegetated = true;
      }
      
      if ((((CraftEntity)entity).getHandle() instanceof EntityEnderDragon))
      {
        EntityEnderDragon creature = (EntityEnderDragon)((CraftEntity)entity).getHandle();
        
        creature.Vegetated = true;
      }
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
  
  public static void removeGoalSelectors(org.bukkit.entity.Entity entity)
  {
    try
    {
      if (_goalSelector == null)
      {
        _goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
        _goalSelector.setAccessible(true);
      }
      
      if ((((CraftEntity)entity).getHandle() instanceof EntityInsentient))
      {
        EntityInsentient creature = (EntityInsentient)((CraftEntity)entity).getHandle();
        
        PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(((CraftWorld)entity.getWorld()).getHandle().methodProfiler);
        
        _goalSelector.set(creature, goalSelector);
      }
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
  
  public static void populate()
  {
    if (creatureMap.isEmpty())
    {
      creatureMap.put("Bat", EntityType.BAT);
      creatureMap.put("Blaze", EntityType.BLAZE);
      creatureMap.put("Arrow", EntityType.ARROW);
      creatureMap.put("Cave Spider", EntityType.CAVE_SPIDER);
      creatureMap.put("Chicken", EntityType.CHICKEN);
      creatureMap.put("Cow", EntityType.COW);
      creatureMap.put("Creeper", EntityType.CREEPER);
      creatureMap.put("Ender Dragon", EntityType.ENDER_DRAGON);
      creatureMap.put("Enderman", EntityType.ENDERMAN);
      creatureMap.put("Ghast", EntityType.GHAST);
      creatureMap.put("Giant", EntityType.GIANT);
      creatureMap.put("Horse", EntityType.HORSE);
      creatureMap.put("Iron Golem", EntityType.IRON_GOLEM);
      creatureMap.put("Item", EntityType.DROPPED_ITEM);
      creatureMap.put("Magma Cube", EntityType.MAGMA_CUBE);
      creatureMap.put("Mooshroom", EntityType.MUSHROOM_COW);
      creatureMap.put("Ocelot", EntityType.OCELOT);
      creatureMap.put("Pig", EntityType.PIG);
      creatureMap.put("Pig Zombie", EntityType.PIG_ZOMBIE);
      creatureMap.put("Sheep", EntityType.SHEEP);
      creatureMap.put("Silverfish", EntityType.SILVERFISH);
      creatureMap.put("Skeleton", EntityType.SKELETON);
      creatureMap.put("Slime", EntityType.SLIME);
      creatureMap.put("Snowman", EntityType.SNOWMAN);
      creatureMap.put("Spider", EntityType.SPIDER);
      creatureMap.put("Squid", EntityType.SQUID);
      creatureMap.put("Villager", EntityType.VILLAGER);
      creatureMap.put("Witch", EntityType.WITCH);
      creatureMap.put("Wither", EntityType.WITHER);
      creatureMap.put("WitherSkull", EntityType.WITHER_SKULL);
      creatureMap.put("Wolf", EntityType.WOLF);
      creatureMap.put("Zombie", EntityType.ZOMBIE);
      
      creatureMap.put("Item", EntityType.DROPPED_ITEM);
    }
  }
  
  public static String getName(org.bukkit.entity.Entity ent)
  {
    if (ent == null) {
      return "Null";
    }
    if (ent.getType() == EntityType.PLAYER) {
      return ((Player)ent).getName();
    }
    if (GetEntityNames().containsKey(ent)) {
      return (String)GetEntityNames().get(ent);
    }
    if ((ent instanceof LivingEntity))
    {
      LivingEntity le = (LivingEntity)ent;
      if (le.getCustomName() != null) {
        return le.getCustomName();
      }
    }
    return getName(ent.getType());
  }
  
  public static String getName(EntityType type)
  {
    for (String cur : creatureMap.keySet()) {
      if (creatureMap.get(cur) == type)
        return cur;
    }
    return type.getName();
  }
  
  public static String searchName(Player caller, String arg, boolean inform)
  {
    populate();
    
    arg = arg.toLowerCase().replaceAll("_", " ");
    LinkedList<String> matchList = new LinkedList();
    for (String cur : creatureMap.keySet())
    {
      if (cur.equalsIgnoreCase(arg)) {
        return cur;
      }
      if (cur.toLowerCase().contains(arg)) {
        matchList.add(cur);
      }
    }
    

    if (matchList.size() != 1)
    {
      if (!inform) {
        return null;
      }
      
      UtilPlayer.message(caller, F.main("Creature Search", 
        C.mCount + matchList.size() + 
        C.mBody + " matches for [" + 
        C.mElem + arg + 
        C.mBody + "]."));
      
      if (matchList.size() > 0)
      {
        String matchString = "";
        for (String cur : matchList)
          matchString = matchString + F.elem(cur) + ", ";
        if (matchString.length() > 1) {
          matchString = matchString.substring(0, matchString.length() - 2);
        }
        UtilPlayer.message(caller, F.main("Creature Search", 
          C.mBody + "Matches [" + 
          C.mElem + matchString + 
          C.mBody + "]."));
      }
      
      return null;
    }
    
    return (String)matchList.get(0);
  }
  
  public static EntityType searchEntity(Player caller, String arg, boolean inform)
  {
    populate();
    
    arg = arg.toLowerCase();
    LinkedList<EntityType> matchList = new LinkedList();
    for (String cur : creatureMap.keySet())
    {
      if (cur.equalsIgnoreCase(arg)) {
        return (EntityType)creatureMap.get(cur);
      }
      if (cur.toLowerCase().contains(arg)) {
        matchList.add((EntityType)creatureMap.get(cur));
      }
    }
    

    if (matchList.size() != 1)
    {
      if (!inform) {
        return null;
      }
      
      UtilPlayer.message(caller, F.main("Creature Search", 
        C.mCount + matchList.size() + 
        C.mBody + " matches for [" + 
        C.mElem + arg + 
        C.mBody + "]."));
      
      if (matchList.size() > 0)
      {
        String matchString = "";
        for (EntityType cur : matchList)
          matchString = matchString + F.elem(cur.getName()) + ", ";
        if (matchString.length() > 1) {
          matchString = matchString.substring(0, matchString.length() - 2);
        }
        UtilPlayer.message(caller, F.main("Creature Search", 
          C.mBody + "Matches [" + 
          C.mElem + matchString + 
          C.mBody + "]."));
      }
      
      return null;
    }
    
    return (EntityType)matchList.get(0);
  }
  
  public static HashMap<LivingEntity, Double> getInRadius(Location loc, double dR)
  {
    HashMap<LivingEntity, Double> ents = new HashMap();
    
    for (org.bukkit.entity.Entity cur : loc.getWorld().getEntities())
    {
      if (((cur instanceof LivingEntity)) && ((!(cur instanceof Player)) || (((Player)cur).getGameMode() != GameMode.CREATIVE)))
      {

        LivingEntity ent = (LivingEntity)cur;
        
        double offset = UtilMath.offset(loc, ent.getLocation());
        
        if (offset < dR)
          ents.put(ent, Double.valueOf(1.0D - offset / dR));
      }
    }
    return ents;
  }
  
  public static boolean hitBox(Location loc, LivingEntity ent, double mult, EntityType disguise)
  {
    if (disguise != null)
    {
      if (disguise == EntityType.SQUID)
      {
        if (UtilMath.offset(loc, ent.getLocation().add(0.0D, 0.4D, 0.0D)) < 0.6D * mult) {
          return true;
        }
        return false;
      }
    }
    
    if ((ent instanceof Player))
    {
      Player player = (Player)ent;
      
      if (UtilMath.offset(loc, player.getEyeLocation()) < 0.4D * mult)
      {
        return true;
      }
      if (UtilMath.offset2d(loc, player.getLocation()) < 0.6D * mult)
      {
        if ((loc.getY() > player.getLocation().getY()) && (loc.getY() < player.getEyeLocation().getY()))
        {
          return true;
        }
        
      }
      
    }
    else if ((ent instanceof Giant))
    {
      if ((loc.getY() > ent.getLocation().getY()) && (loc.getY() < ent.getLocation().getY() + 12.0D) && 
        (UtilMath.offset2d(loc, ent.getLocation()) < 4.0D)) {
        return true;
      }
      
    }
    else if ((loc.getY() > ent.getLocation().getY()) && (loc.getY() < ent.getLocation().getY() + 2.0D) && 
      (UtilMath.offset2d(loc, ent.getLocation()) < 0.5D * mult)) {
      return true;
    }
    



    return false;
  }
  
  public static boolean isGrounded(org.bukkit.entity.Entity ent)
  {
    if ((ent instanceof CraftEntity)) {
      return ((CraftEntity)ent).getHandle().onGround;
    }
    return UtilBlock.solid(ent.getLocation().getBlock().getRelative(BlockFace.DOWN));
  }
  
  public static void PlayDamageSound(LivingEntity damagee)
  {
    Sound sound = Sound.HURT_FLESH;
    
    if (damagee.getType() == EntityType.BAT) { sound = Sound.BAT_HURT;
    } else if (damagee.getType() == EntityType.BLAZE) { sound = Sound.BLAZE_HIT;
    } else if (damagee.getType() == EntityType.CAVE_SPIDER) { sound = Sound.SPIDER_IDLE;
    } else if (damagee.getType() == EntityType.CHICKEN) { sound = Sound.CHICKEN_HURT;
    } else if (damagee.getType() == EntityType.COW) { sound = Sound.COW_HURT;
    } else if (damagee.getType() == EntityType.CREEPER) { sound = Sound.CREEPER_HISS;
    } else if (damagee.getType() == EntityType.ENDER_DRAGON) { sound = Sound.ENDERDRAGON_GROWL;
    } else if (damagee.getType() == EntityType.ENDERMAN) { sound = Sound.ENDERMAN_HIT;
    } else if (damagee.getType() == EntityType.GHAST) { sound = Sound.GHAST_SCREAM;
    } else if (damagee.getType() == EntityType.GIANT) { sound = Sound.ZOMBIE_HURT;
    }
    else if (damagee.getType() == EntityType.IRON_GOLEM) { sound = Sound.IRONGOLEM_HIT;
    } else if (damagee.getType() == EntityType.MAGMA_CUBE) { sound = Sound.MAGMACUBE_JUMP;
    } else if (damagee.getType() == EntityType.MUSHROOM_COW) { sound = Sound.COW_HURT;
    } else if (damagee.getType() == EntityType.OCELOT) { sound = Sound.CAT_MEOW;
    } else if (damagee.getType() == EntityType.PIG) { sound = Sound.PIG_IDLE;
    } else if (damagee.getType() == EntityType.PIG_ZOMBIE) { sound = Sound.ZOMBIE_HURT;
    } else if (damagee.getType() == EntityType.SHEEP) { sound = Sound.SHEEP_IDLE;
    } else if (damagee.getType() == EntityType.SILVERFISH) { sound = Sound.SILVERFISH_HIT;
    } else if (damagee.getType() == EntityType.SKELETON) { sound = Sound.SKELETON_HURT;
    } else if (damagee.getType() == EntityType.SLIME) { sound = Sound.SLIME_ATTACK;
    } else if (damagee.getType() == EntityType.SNOWMAN) { sound = Sound.STEP_SNOW;
    } else if (damagee.getType() == EntityType.SPIDER) { sound = Sound.SPIDER_IDLE;


    }
    else if (damagee.getType() == EntityType.WITHER) { sound = Sound.WITHER_HURT;
    } else if (damagee.getType() == EntityType.WOLF) { sound = Sound.WOLF_HURT;
    } else if (damagee.getType() == EntityType.ZOMBIE) { sound = Sound.ZOMBIE_HURT;
    }
    damagee.getWorld().playSound(damagee.getLocation(), sound, 1.5F + (float)(0.5D * Math.random()), 0.8F + (float)(0.4000000059604645D * Math.random()));
  }
  

  public static boolean onBlock(Player player)
  {
    double xMod = player.getLocation().getX() % 1.0D;
    if (player.getLocation().getX() < 0.0D) {
      xMod += 1.0D;
    }
    double zMod = player.getLocation().getZ() % 1.0D;
    if (player.getLocation().getZ() < 0.0D) {
      zMod += 1.0D;
    }
    int xMin = 0;
    int xMax = 0;
    int zMin = 0;
    int zMax = 0;
    
    if (xMod < 0.3D) xMin = -1;
    if (xMod > 0.7D) { xMax = 1;
    }
    if (zMod < 0.3D) zMin = -1;
    if (zMod > 0.7D) { zMax = 1;
    }
    for (int x = xMin; x <= xMax; x++)
    {
      for (int z = zMin; z <= zMax; z++)
      {

        if ((player.getLocation().add(x, -0.5D, z).getBlock().getType() != Material.AIR) && (!player.getLocation().add(x, -0.5D, z).getBlock().isLiquid())) {
          return true;
        }
        
        Material beneath = player.getLocation().add(x, -1.5D, z).getBlock().getType();
        if ((player.getLocation().getY() % 0.5D == 0.0D) && (
          (beneath == Material.FENCE) || 
          (beneath == Material.NETHER_FENCE) || 
          (beneath == Material.COBBLE_WALL))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static void CreatureMove(org.bukkit.entity.Entity ent, Location target, float speed)
  {
    if (!(ent instanceof Creature)) {
      return;
    }
    if (UtilMath.offset(ent.getLocation(), target) < 0.1D) {
      return;
    }
    EntityCreature ec = ((CraftCreature)ent).getHandle();
    Navigation nav = ec.getNavigation();
    
    if (UtilMath.offset(ent.getLocation(), target) > 24.0D)
    {
      Location newTarget = ent.getLocation();
      
      newTarget.add(UtilAlg.getTrajectory(ent.getLocation(), target).multiply(24));
      
      nav.a(newTarget.getX(), newTarget.getY(), newTarget.getZ(), speed);
    }
    else
    {
      nav.a(target.getX(), target.getY(), target.getZ(), speed);
    }
  }
  
  public static boolean CreatureMoveFast(org.bukkit.entity.Entity ent, Location target, float speed)
  {
    if (!(ent instanceof Creature)) {
      return false;
    }
    if (UtilMath.offset(ent.getLocation(), target) < 0.1D) {
      return false;
    }
    if (UtilMath.offset(ent.getLocation(), target) < 2.0D) {
      speed = Math.min(speed, 1.0F);
    }
    EntityCreature ec = ((CraftCreature)ent).getHandle();
    ec.getControllerMove().a(target.getX(), target.getY(), target.getZ(), speed);
    
    return true;
  }
}
