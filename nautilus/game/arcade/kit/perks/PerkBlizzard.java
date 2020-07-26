package nautilus.game.arcade.kit.perks;

import java.util.WeakHashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class PerkBlizzard extends Perk
{
  private WeakHashMap<Projectile, Player> _snowball = new WeakHashMap();
  


  public PerkBlizzard()
  {
    super("Blizzard", new String[] {C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Blizzard" });
  }
  

  @EventHandler
  public void EnergyUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        player.setExp((float)Math.min(0.999D, player.getExp() + 0.007D));
      }
    }
  }
  
  @EventHandler
  public void Snow(UpdateEvent event) {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (player.isBlocking())
      {

        if (this.Kit.HasKit(player))
        {


          if (player.getExp() >= 0.1D)
          {

            player.setExp(Math.max(0.0F, player.getExp() - 0.1111111F));
            
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
          } } }
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
    event.SetCancelled("Blizzard");
    damagee.setVelocity(proj.getVelocity().multiply(0.15D).add(new Vector(0.0D, 0.15D, 0.0D)));
    

    if (((damagee instanceof Player)) && 
      (Recharge.Instance.use((Player)damagee, GetName(), 250L, false, false))) {
      this.Manager.GetDamage().NewDamageEvent(damagee, event.GetDamagerEntity(true), null, 
        EntityDamageEvent.DamageCause.PROJECTILE, 1.0D, false, true, false, 
        mineplex.core.common.util.UtilEnt.getName(event.GetDamagerEntity(true)), GetName());
    }
  }
  
  @EventHandler
  public void SnowballForm(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Snowball)) {
      return;
    }
    if (this._snowball.remove(event.getEntity()) == null) {
      return;
    }
    this.Manager.GetBlockRestore().Snow(event.getEntity().getLocation().getBlock(), (byte)1, (byte)7, 2000L, 250L, 0);
  }
}
