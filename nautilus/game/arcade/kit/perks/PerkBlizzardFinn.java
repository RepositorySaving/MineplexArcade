package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.WeakHashMap;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkBlizzardFinn extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  private WeakHashMap<Projectile, Player> _snowball = new WeakHashMap();
  


  public PerkBlizzardFinn()
  {
    super("Blizzard", new String[] {C.cYellow + "Block" + C.cGray + " with Diamond Sword to use " + C.cGreen + "Blizzard" });
  }
  

  @EventHandler
  public void Activate(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("DIAMOND_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 2000L, true, false)) {
      return;
    }
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (this._active.containsKey(player))
      {

        if (!player.isBlocking())
        {
          this._active.remove(player);


        }
        else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(player)).longValue(), 1000L))
        {
          this._active.remove(player);

        }
        else
        {
          for (int i = 0; i < 4; i++)
          {
            Snowball snow = (Snowball)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Snowball.class);
            double x = 0.1D - UtilMath.r(20) / 100.0D;
            double y = UtilMath.r(20) / 100.0D;
            double z = 0.1D - UtilMath.r(20) / 100.0D;
            snow.setVelocity(player.getLocation().getDirection().add(new Vector(x, y, z)).multiply(2));
            this._snowball.put(snow, player);
          }
          

          player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.STEP_SNOW, 0.1F, 0.5F);
        } }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Snowball(CustomDamageEvent event) {
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile proj = event.GetProjectile();
    if (proj == null) { return;
    }
    if (!(proj instanceof Snowball)) {
      return;
    }
    if (!this._snowball.containsKey(proj)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    event.SetCancelled("Snowball Damage");
    
    if ((damagee instanceof Player)) {
      return;
    }
    damagee.setVelocity(proj.getVelocity().multiply(0.15D).add(new Vector(0.0D, 0.15D, 0.0D)));
  }
  
  @EventHandler
  public void SnowballForm(ProjectileHitEvent event)
  {
    if (!(event.getEntity() instanceof Snowball)) {
      return;
    }
    if (this._snowball.remove(event.getEntity()) == null) {
      return;
    }
    this.Manager.GetBlockRestore().Snow(event.getEntity().getLocation().getBlock(), (byte)1, (byte)7, 2000L, 250L, 0);
  }
}
