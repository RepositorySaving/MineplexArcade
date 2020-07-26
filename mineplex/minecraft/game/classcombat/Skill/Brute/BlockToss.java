package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillCharge;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class BlockToss extends SkillCharge implements mineplex.core.projectile.IThrown
{
  private HashMap<Player, FallingBlock> _holding = new HashMap();
  private HashMap<FallingBlock, Player> _falling = new HashMap();
  

  public BlockToss(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels, 0.01F, 0.005F);
    
    SetDesc(
      new String[] {
      "Hold Block to pick up a block,", 
      "Release Block to throw it,", 
      "dealing up to #6#1 damage.", 
      "", 
      GetChargeString(), 
      "", 
      "You can only pick up Stone, Dirt,", 
      "Cobblestone, Sand, Gravel or Snow." });
  }
  


  public String GetRechargeString()
  {
    return "Recharge: #" + UtilMath.trim(1, 4.0D) + "#" + UtilMath.trim(1, -0.5D) + " Seconds";
  }
  
  @EventHandler
  public void Grab(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R_BLOCK)) {
      return;
    }
    if (!mineplex.core.common.util.UtilGear.isSword(player.getItemInHand())) {
      return;
    }
    
    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    org.bukkit.Bukkit.getServer().getPluginManager().callEvent(trigger);
    
    if (trigger.IsCancelled()) {
      return;
    }
    if (this._holding.containsKey(player)) {
      return;
    }
    
    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    if (!Recharge.Instance.use(player, GetName(), 1000L, false, false)) {
      return;
    }
    Block grab = event.getClickedBlock();
    
    int id = event.getClickedBlock().getTypeId();
    

    if ((id != 1) && 
      (id != 2) && 
      (id != 3) && 
      (id != 12) && 
      (id != 13) && 
      (id != 80)) {
      return;
    }
    
    if ((grab.getRelative(BlockFace.UP).getTypeId() == 64) || (grab.getRelative(BlockFace.UP).getTypeId() == 71))
    {
      UtilPlayer.message(player, F.main(GetName(), "You cannot grab this block."));
      return;
    }
    

    if ((grab.getRelative(BlockFace.NORTH).getType() == Material.TRAP_DOOR) || 
      (grab.getRelative(BlockFace.SOUTH).getType() == Material.TRAP_DOOR) || 
      (grab.getRelative(BlockFace.EAST).getType() == Material.TRAP_DOOR) || 
      (grab.getRelative(BlockFace.WEST).getType() == Material.TRAP_DOOR))
    {
      UtilPlayer.message(player, F.main(GetName(), "You cannot grab this block."));
      return;
    }
    

    FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), event.getClickedBlock().getType(), (byte)0);
    this.Factory.BlockRestore().Add(event.getClickedBlock(), 0, (byte)0, 10000L);
    

    player.eject();
    player.setPassenger(block);
    this._holding.put(player, block);
    this._falling.put(block, player);
    

    player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, block.getMaterial().getId());
  }
  
  @EventHandler
  public void Throw(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> voidSet = new HashSet();
    HashSet<Player> throwSet = new HashSet();
    
    for (Player cur : this._holding.keySet())
    {
      if (cur.getPassenger() == null)
      {
        voidSet.add(cur);


      }
      else if (((FallingBlock)this._holding.get(cur)).getVehicle() == null)
      {
        voidSet.add(cur);


      }
      else if (!((FallingBlock)this._holding.get(cur)).getVehicle().equals(cur))
      {
        voidSet.add(cur);

      }
      else
      {
        if (!cur.isBlocking()) {
          throwSet.add(cur);
        }
        
        Charge(cur);
      }
    }
    for (Player cur : voidSet)
    {
      FallingBlock block = (FallingBlock)this._holding.remove(cur);
      this._charge.remove(cur);
      block.remove();
    }
    
    for (Player cur : throwSet)
    {
      FallingBlock block = (FallingBlock)this._holding.remove(cur);
      float charge = ((Float)this._charge.remove(cur)).floatValue();
      

      cur.eject();
      double mult = Math.max(0.4D, charge * 2.0F);
      

      UtilAction.velocity(block, cur.getLocation().getDirection(), mult, false, 0.0D, 0.0D, 1.0D, true);
      this.Factory.Projectile().AddThrow(block, cur, this, -1L, true, true, true, 
        null, 0.0F, 0.0F, null, 0, UpdateType.FASTEST, 2.5D);
      

      UtilAction.velocity(cur, cur.getLocation().getDirection().multiply(-1), 0.4D, false, 0.0D, 0.0D, 1.0D, false);
      

      mineplex.core.common.util.UtilServer.getServer().getPluginManager().callEvent(new mineplex.minecraft.game.classcombat.Skill.event.SkillEvent(cur, GetName(), IPvpClass.ClassType.Brute));
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event)
  {
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if ((event.GetReason() == null) || (!event.GetReason().equals(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 1.5D);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    int level = getLevel(data.GetThrower());
    

    this.Factory.Damage().NewDamageEvent(target, data.GetThrower(), null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, data.GetThrown().getVelocity().length() * (2.5D + 0.5D * level), true, true, false, 
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
  public void CreateBlock(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<FallingBlock> fallen = new HashSet();
    
    for (FallingBlock cur : this._falling.keySet())
    {
      if ((cur.isDead()) || (!cur.isValid())) {
        fallen.add(cur);
      }
    }
    for (FallingBlock cur : fallen)
    {
      this._falling.remove(cur);
      Block block = cur.getLocation().getBlock();
      
      int id = block.getTypeId();
      
      if ((id == 1) || 
        (id == 2) || 
        (id == 3) || 
        (id == 4) || 
        (id == 12) || 
        (id == 13) || 
        (id == 80))
      {

        block.setTypeIdAndData(0, (byte)0, false);
        

        this.Factory.BlockRestore().Add(block, cur.getBlockId(), (byte)0, 10000L);
        

        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
      }
    }
  }
  
  @EventHandler
  public void ItemSpawn(ItemSpawnEvent event) {
    int id = event.getEntity().getItemStack().getTypeId();
    

    if ((id != 1) && 
      (id != 2) && 
      (id != 3) && 
      (id != 4) && 
      (id != 12) && 
      (id != 13) && 
      (id != 80)) {
      return;
    }
    for (FallingBlock block : this._falling.keySet()) {
      if (UtilMath.offset(event.getEntity().getLocation(), block.getLocation()) < 1.0D) {
        event.setCancelled(true);
      }
    }
  }
  
  public void Reset(Player player) {
    if (this._holding.containsKey(player))
    {
      player.eject();
    }
    
    this._holding.remove(player);
    this._charge.remove(player);
  }
}
