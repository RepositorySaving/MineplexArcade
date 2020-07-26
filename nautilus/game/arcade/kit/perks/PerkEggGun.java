package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkEggGun extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  


  public PerkEggGun()
  {
    super("Egg Blaster", new String[] {C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Egg Blaster" });
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
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 2500L, true, true)) {
      return;
    }
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this._active.containsKey(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);


        }
        else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 750L))
        {
          this._active.remove(cur);
        }
        else
        {
          Vector offset = cur.getLocation().getDirection();
          if (offset.getY() < 0.0D) {
            offset.setY(0);
          }
          Egg egg = (Egg)cur.getWorld().spawn(cur.getLocation().add(0.0D, 0.5D, 0.0D).add(offset), Egg.class);
          egg.setVelocity(cur.getLocation().getDirection().add(new Vector(0.0D, 0.2D, 0.0D)));
          egg.setShooter(cur);
          

          cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.CHICKEN_EGG_POP, 0.5F, 1.0F);
        } }
    }
  }
  
  @EventHandler
  public void EggHit(CustomDamageEvent event) {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Egg)) {
      return;
    }
    if (event.GetDamage() >= 1.0D) {
      return;
    }
    event.SetCancelled("Egg Blaster");
    
    Egg egg = (Egg)event.GetProjectile();
    

    this.Manager.GetDamage().NewDamageEvent(event.GetDamageeEntity(), egg.getShooter(), egg, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE, 1.0D, true, true, false, 
      mineplex.core.common.util.UtilEnt.getName(egg.getShooter()), GetName());
    
    event.GetDamageeEntity().setVelocity(new Vector(0, 0, 0));
  }
}
