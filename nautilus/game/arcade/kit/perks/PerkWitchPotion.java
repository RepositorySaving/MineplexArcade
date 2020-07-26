package nautilus.game.arcade.kit.perks;

import java.util.ArrayList;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkWitchPotion extends Perk
{
  private ArrayList<Projectile> _proj = new ArrayList();
  


  public PerkWitchPotion()
  {
    super("Daze Potion", new String[] {C.cYellow + "Right-Click" + C.cGray + " to use " + C.cGreen + "Daze Potion" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 2000L, true, true)) {
      return;
    }
    
    ThrownPotion potion = (ThrownPotion)player.launchProjectile(ThrownPotion.class);
    UtilAction.velocity(potion, player.getLocation().getDirection(), 1.0D, false, 0.0D, 0.2D, 10.0D, false);
    
    this._proj.add(potion);
    

    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Hit(ProjectileHitEvent event)
  {
    if (!this._proj.remove(event.getEntity())) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (!player.equals(event.getEntity().getShooter()))
      {

        if (UtilMath.offset(player.getLocation().add(0.0D, 1.0D, 0.0D), event.getEntity().getLocation()) <= 3.0D)
        {


          this.Manager.GetDamage().NewDamageEvent(player, event.getEntity().getShooter(), null, 
            EntityDamageEvent.DamageCause.CUSTOM, 5.0D, true, true, false, 
            UtilEnt.getName(event.getEntity().getShooter()), GetName()); }
      }
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Projectile> potionIterator = this._proj.iterator();
    
    while (potionIterator.hasNext())
    {
      Projectile proj = (Projectile)potionIterator.next();
      
      if (!proj.isValid())
      {
        potionIterator.remove();
      }
      else
      {
        UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.MOB_SPELL, proj.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      }
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event) {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.0D);
  }
}
