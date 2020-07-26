package nautilus.game.arcade.game.games.runner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.runner.kits.KitFrosty;
import nautilus.game.arcade.game.games.runner.kits.KitLeaper;
import nautilus.game.arcade.kit.Kit;
import net.minecraft.server.v1_7_R3.EntityArrow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class Runner extends SoloGame implements mineplex.core.projectile.IThrown
{
  private HashMap<Block, Long> _blocks = new HashMap();
  














  public Runner(ArcadeManager manager)
  {
    super(manager, GameType.Runner, new Kit[] {new KitLeaper(manager), new nautilus.game.arcade.game.games.runner.kits.KitArcher(manager), new KitFrosty(manager) }, new String[] {"Blocks fall from underneath you", "Keep running to stay alive", "Avoid falling blocks from above", "Last player alive wins!" });
    

    this.DamagePvP = false;
    this.HungerSet = 20;
    this.WorldWaterDamage = 4;
    
    this.PrepareFreeze = false;
  }
  
  @EventHandler
  public void ArrowDamage(ProjectileHitEvent event)
  {
    if (!(event.getEntity() instanceof Arrow)) {
      return;
    }
    final Arrow arrow = (Arrow)event.getEntity();
    
    this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        try
        {
          EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();
          
          Field fieldX = EntityArrow.class.getDeclaredField("d");
          Field fieldY = EntityArrow.class.getDeclaredField("e");
          Field fieldZ = EntityArrow.class.getDeclaredField("f");
          
          fieldX.setAccessible(true);
          fieldY.setAccessible(true);
          fieldZ.setAccessible(true);
          
          int x = fieldX.getInt(entityArrow);
          int y = fieldY.getInt(entityArrow);
          int z = fieldZ.getInt(entityArrow);
          
          Block block = arrow.getWorld().getBlockAt(x, y, z);
          
          double radius = 2.5D;
          
          for (Block other : UtilBlock.getInRadius(block.getLocation().add(0.5D, 0.5D, 0.5D), radius).keySet())
          {
            Runner.this.AddBlock(other);
          }
          
          arrow.remove();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }, 0L);
  }
  
  public void AddBlock(Block block)
  {
    if ((block == null) || (block.getTypeId() == 0) || (block.getTypeId() == 7) || (block.isLiquid())) {
      return;
    }
    if (block.getRelative(org.bukkit.block.BlockFace.UP).getTypeId() != 0) {
      return;
    }
    if (this._blocks.containsKey(block)) {
      return;
    }
    this._blocks.put(block, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void BlockBreak(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    if (!IsLive())
      return;
    int xMax;
    int x;
    for (Iterator localIterator = GetPlayers(true).iterator(); localIterator.hasNext(); 
        




















        x <= xMax)
    {
      Player player = (Player)localIterator.next();
      

      double xMod = player.getLocation().getX() % 1.0D;
      if (player.getLocation().getX() < 0.0D) {
        xMod += 1.0D;
      }
      double zMod = player.getLocation().getZ() % 1.0D;
      if (player.getLocation().getZ() < 0.0D) {
        zMod += 1.0D;
      }
      int xMin = 0;
      xMax = 0;
      int zMin = 0;
      int zMax = 0;
      
      if (xMod < 0.3D) xMin = -1;
      if (xMod > 0.7D) { xMax = 1;
      }
      if (zMod < 0.3D) zMin = -1;
      if (zMod > 0.7D) { zMax = 1;
      }
      x = xMin; continue;
      
      for (int z = zMin; z <= zMax; z++)
      {
        AddBlock(player.getLocation().add(x, -0.5D, z).getBlock());
      }
      x++;
    }
    







    HashSet<Block> readd = new HashSet();
    
    Object blockIterator = this._blocks.keySet().iterator();
    int id;
    while (((Iterator)blockIterator).hasNext())
    {
      Block block = (Block)((Iterator)blockIterator).next();
      
      if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._blocks.get(block)).longValue(), 120L))
      {

        ((Iterator)blockIterator).remove();
        

        if (block.getTypeId() == 98)
        {
          if (block.getData() == 0)
          {
            readd.add(block);
            block.setData((byte)2);
            continue;
          }
        }
        

        if ((block.getTypeId() == 35) || (block.getTypeId() == 159))
        {
          if (block.getData() == 3)
          {
            readd.add(block);
            block.setData((byte)5);
            continue;
          }
          
          if (block.getData() == 5)
          {
            readd.add(block);
            block.setData((byte)4);
            continue;
          }
          
          if (block.getData() == 4)
          {
            readd.add(block);
            block.setData((byte)1);
            continue;
          }
          
          if (block.getData() == 1)
          {
            readd.add(block);
            block.setData((byte)14);
            continue;
          }
          
          if (block.getData() != 14)
          {
            readd.add(block);
            block.setData((byte)3);
            continue;
          }
        }
        

        id = block.getTypeId();
        byte data = block.getData();
        MapUtil.QuickChangeBlockAt(block.getLocation(), Material.AIR);
        FallingBlock ent = block.getWorld().spawnFallingBlock(block.getLocation(), id, data);
        this.Manager.GetProjectile().AddThrow(ent, null, this, -1L, true, false, false, false, 1.0D);
      }
    }
    
    for (Block block : readd)
    {
      this._blocks.put(block, Long.valueOf(System.currentTimeMillis()));
    }
  }
  
  @EventHandler
  public void BlockForm(EntityChangeBlockEvent event)
  {
    BlockSmash(event.getEntity());
    
    event.setCancelled(true);
  }
  
  public void BlockSmash(Entity ent)
  {
    if (!(ent instanceof FallingBlock)) {
      return;
    }
    FallingBlock block = (FallingBlock)ent;
    
    int id = block.getBlockId();
    if ((id == 35) || (id == 159)) {
      id = 152;
    }
    block.getWorld().playEffect(block.getLocation(), org.bukkit.Effect.STEP_SOUND, id);
    
    ent.remove();
  }
  



  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    if ((target instanceof Player))
    {
      if (!this.Manager.GetGame().IsAlive((Player)target))
      {
        return;
      }
    }
    

    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.ENTITY_ATTACK, 6.0D, true, true, false, 
      "Falling Block", "Falling Block");
    
    BlockSmash(data.GetThrown());
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
