package nautilus.game.arcade.game.games.snowfight;

import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class SnowFight
  extends TeamGame
{
  public SnowFight(ArcadeManager manager)
  {
    super(manager, GameType.SnowFight, new Kit[] {new NullKit(manager) }, new String[] {"Just like... kill your enemies. with snow." });
  }
  


  @EventHandler
  public void Weather(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    World world = UtilWorld.getWorldType(World.Environment.NORMAL);
    
    if (world == null) {
      return;
    }
    world.setStorm(true);
    world.setThundering(false);
    world.setWeatherDuration(40);
    world.setTime(4000L);
  }
  
  @EventHandler
  public void BlockDamage(BlockDamageEvent event)
  {
    Player player = event.getPlayer();
    
    if (!IsLive()) {
      return;
    }
    if (!IsPlaying(player)) {
      return;
    }
    if (!IsSnow(event.getBlock())) {
      return;
    }
    if (player.getInventory().getHeldItemSlot() != 0) {
      return;
    }
    
    SnowballCount(player, 1);
    

    SnowDecrease(event.getBlock(), 1);
    

    event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, 80);
  }
  
  @EventHandler
  public void InteractSnowball(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!IsLive()) {
      return;
    }
    if (!IsPlaying(player)) {
      return;
    }
    if (!UtilGear.isMat(player.getItemInHand(), Material.SNOW_BALL)) {
      return;
    }
    event.setCancelled(true);
    
    if (UtilEvent.isAction(event, UtilEvent.ActionType.L)) {
      SnowballThrow(player);
    }
    else if (UtilEvent.isAction(event, UtilEvent.ActionType.R_BLOCK)) {
      SnowballPlace(player, event.getClickedBlock(), 1);
    }
  }
  
  private void SnowballPlace(Player player, Block block, int above) {
    if ((block.getTypeId() == 78) || (UtilBlock.airFoliage(block)))
    {

      if (block.getTypeId() == 78)
      {
        block.setTypeIdAndData(78, (byte)(block.getData() + 1), true);
        
        if (block.getData() >= 7) {
          block.setTypeIdAndData(80, (byte)0, true);
        }
      }
      else {
        block.setTypeIdAndData(78, (byte)0, true);
      }
      

      block.getWorld().playSound(block.getLocation(), Sound.STEP_SNOW, 1.0F, 0.6F);
      

      SnowballCount(player, -1);
    }
    else if (((IsSnow(block)) || (UtilBlock.solid(block))) && (above > 0))
    {
      SnowballPlace(player, block.getRelative(BlockFace.UP), above - 1);
    }
  }
  

  private void SnowballThrow(Player player)
  {
    player.launchProjectile(Snowball.class);
    

    SnowballCount(player, -1);
    

    player.getWorld().playSound(player.getLocation(), Sound.STEP_SNOW, 3.0F, 1.5F);
  }
  
  private void SnowballCount(Player player, int count)
  {
    if (player.getInventory().getItem(1) != null) {
      count += player.getInventory().getItem(1).getAmount();
    }
    if (count > 16) {
      count = 16;
    }
    if (count > 0) {
      player.getInventory().setItem(1, ItemStackFactory.Instance.CreateStack(Material.SNOW_BALL, count));
    } else {
      player.getInventory().setItem(1, null);
    }
  }
  
  private void SnowDecrease(Block block, int height) {
    if (height <= 0) {
      return;
    }
    if (!IsSnow(block)) {
      return;
    }
    
    while (IsSnow(block.getRelative(BlockFace.UP))) {
      block = block.getRelative(BlockFace.UP);
    }
    
    int snowLevel = 8;
    if (block.getTypeId() == 78) {
      snowLevel = block.getData() + 1;
    }
    
    if (height >= snowLevel)
    {
      block.setTypeIdAndData(0, (byte)0, true);
      SnowDecrease(block.getRelative(BlockFace.DOWN), height - snowLevel);
    }
    else
    {
      block.setTypeIdAndData(78, (byte)(snowLevel - height - 1), true);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void SnowballDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile proj = event.GetProjectile();
    if (proj == null) { return;
    }
    if (!(proj instanceof Snowball)) {
      return;
    }
    event.AddMod("Snowball", "Snowball", 3.0D, true);
    event.SetIgnoreRate(true);
    

    proj.getWorld().playEffect(proj.getLocation(), Effect.STEP_SOUND, 80);
  }
  
  private boolean IsSnow(Block block)
  {
    return (block.getTypeId() == 78) || (block.getTypeId() == 80);
  }
}
