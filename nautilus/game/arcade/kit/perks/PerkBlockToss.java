package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilServer;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.BlockTossData;
import nautilus.game.arcade.kit.perks.event.PerkBlockGrabEvent;
import nautilus.game.arcade.kit.perks.event.PerkBlockThrowEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class PerkBlockToss extends Perk implements mineplex.core.projectile.IThrown
{
  private HashMap<Player, BlockTossData> _hold = new HashMap();
  private HashMap<Player, Long> _charge = new HashMap();
  private HashSet<Player> _charged = new HashSet();
  private HashMap<FallingBlock, Player> _falling = new HashMap();
  



  public PerkBlockToss()
  {
    super("Block Toss", new String[] {C.cYellow + "Hold Block" + C.cGray + " to " + C.cGreen + "Grab Block", C.cYellow + "Release Block" + C.cGray + " to " + C.cGreen + "Throw Block" });
  }
  

  @EventHandler
  public void Grab(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!mineplex.core.common.util.UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R_BLOCK)) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!UtilGear.isSword(player.getItemInHand())) {
      return;
    }
    if (this._hold.containsKey(player)) {
      return;
    }
    Block grab = event.getClickedBlock();
    
    if (UtilBlock.usable(grab)) {
      return;
    }
    if ((!UtilBlock.airFoliage(grab.getRelative(BlockFace.UP))) || (this.Manager.GetBlockRestore().Contains(grab.getRelative(BlockFace.UP))))
    {
      mineplex.core.common.util.UtilPlayer.message(player, mineplex.core.common.util.F.main("Game", "You can only pick up blocks with Air above them."));
      return;
    }
    

    PerkBlockGrabEvent blockEvent = new PerkBlockGrabEvent(player, grab.getTypeId(), grab.getData());
    UtilServer.getServer().getPluginManager().callEvent(blockEvent);
    

    int id = grab.getTypeId();
    byte data = grab.getData();
    

    this.Manager.GetBlockRestore().Add(event.getClickedBlock(), 0, (byte)0, 10000L);
    
    this._hold.put(player, new BlockTossData(id, data));
    
    this._charge.put(player, Long.valueOf(System.currentTimeMillis()));
    

    player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, id);
  }
  
  @EventHandler
  public void Throw(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> throwSet = new HashSet();
    
    for (Player cur : this._hold.keySet())
    {

      if (!cur.isBlocking()) {
        throwSet.add(cur);
      }
      
      if ((!this._charged.contains(cur)) && 
        (System.currentTimeMillis() - ((Long)this._charge.get(cur)).longValue() > 800L))
      {
        this._charged.add(cur);
        cur.playEffect(cur.getLocation(), Effect.CLICK1, 0);
      }
    }
    
    for (Player cur : throwSet)
    {
      BlockTossData data = (BlockTossData)this._hold.remove(cur);
      
      FallingBlock block = cur.getWorld().spawnFallingBlock(cur.getEyeLocation().add(cur.getLocation().getDirection()), data.Type, data.Data);
      
      this._falling.put(block, cur);
      
      this._charged.remove(cur);
      
      long charge = System.currentTimeMillis() - ((Long)this._charge.remove(cur)).longValue();
      

      double mult = 1.4D;
      if (charge < 800L) {
        mult *= (0.25D + 0.75D * (charge / 800.0D));
      }
      
      mineplex.core.common.util.UtilAction.velocity(block, cur.getLocation().getDirection(), mult, false, 0.2D, 0.0D, 1.0D, true);
      this.Manager.GetProjectile().AddThrow(block, cur, this, -1L, true, true, true, 
        null, 0.0F, 0.0F, null, 0, UpdateType.FASTEST, 2.0D);
      

      PerkBlockThrowEvent blockEvent = new PerkBlockThrowEvent(cur);
      UtilServer.getServer().getPluginManager().callEvent(blockEvent);
    }
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE, 2.0D + data.GetThrown().getVelocity().length() * 8.0D, true, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
    

    if ((data.GetThrown() instanceof FallingBlock))
    {
      FallingBlock thrown = (FallingBlock)data.GetThrown();
      
      FallingBlock newThrown = data.GetThrown().getWorld().spawnFallingBlock(data.GetThrown().getLocation(), thrown.getMaterial(), (byte)0);
      

      this._falling.remove(thrown);
      thrown.remove();
      

      if ((data.GetThrower() instanceof Player)) {
        this._falling.put(newThrown, (Player)data.GetThrower());
      }
    }
  }
  



  public void Idle(ProjectileUser data) {}
  



  public void Expire(ProjectileUser data) {}
  


  @EventHandler
  public void BlockForm(EntityChangeBlockEvent event)
  {
    if (!(event.getEntity() instanceof FallingBlock)) {
      return;
    }
    FallingBlock falling = (FallingBlock)event.getEntity();
    
    falling.getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, falling.getBlockId());
    
    this._falling.remove(falling);
    falling.remove();
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.5D);
  }
}
