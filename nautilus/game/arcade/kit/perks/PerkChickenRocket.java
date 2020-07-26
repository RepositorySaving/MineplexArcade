package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.ChickenMissileData;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkChickenRocket extends Perk
{
  private HashSet<ChickenMissileData> _data = new HashSet();
  



  public PerkChickenRocket()
  {
    super("Chicken Missile", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Chicken Missile", C.cGreen + "Chicken Missile" + C.cGray + " instantly recharges if you hit a player." });
  }
  

  @EventHandler
  public void Missile(PlayerInteractEvent event)
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
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 7000L, true, true)) {
      return;
    }
    this.Manager.GetGame().CreatureAllowOverride = true;
    Chicken ent = (Chicken)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Chicken.class);
    ent.getLocation().setPitch(0.0F);
    ent.getLocation().setYaw(player.getLocation().getYaw());
    ent.setBaby();
    ent.setAgeLock(true);
    mineplex.core.common.util.UtilEnt.Vegetate(ent);
    this.Manager.GetGame().CreatureAllowOverride = false;
    
    this._data.add(new ChickenMissileData(player, ent));
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<ChickenMissileData> dataIterator = this._data.iterator();
    
    while (dataIterator.hasNext())
    {
      ChickenMissileData data = (ChickenMissileData)dataIterator.next();
      
      data.Chicken.setVelocity(data.Direction);
      data.Chicken.getWorld().playSound(data.Chicken.getLocation(), org.bukkit.Sound.CHICKEN_HURT, 0.3F, 1.5F);
      
      if (UtilTime.elapsed(data.Time, 200L))
      {



        boolean detonate = false;
        
        if (UtilTime.elapsed(data.Time, 4000L))
        {
          detonate = true;

        }
        else
        {
          for (Entity ent : data.Player.getWorld().getEntities())
          {
            if ((!(ent instanceof Arrow)) || 
              (!((Arrow)ent).isOnGround()))
            {

              if (!ent.equals(data.Player))
              {

                if (!ent.equals(data.Chicken))
                {

                  if (UtilMath.offset(data.Chicken.getLocation(), ent.getLocation().add(0.0D, 0.5D, 0.0D)) <= 2.0D)
                  {

                    if ((!(ent instanceof Player)) || 
                      (this.Manager.GetGame().IsAlive((Player)ent)))
                    {


                      Recharge.Instance.useForce(data.Player, GetName(), -1L);
                      
                      detonate = true;
                      break;
                    } } } }
            }
          }
          if ((!detonate) && (data.HasHitBlock()))
          {
            detonate = true;
          }
        }
        
        if (detonate)
        {

          for (Entity ent : data.Player.getWorld().getEntities())
          {
            if ((ent instanceof LivingEntity))
            {

              if (!ent.equals(data.Player))
              {

                if (UtilMath.offset(data.Chicken.getLocation(), ent.getLocation().add(0.0D, 0.5D, 0.0D)) <= 3.0D)
                {

                  if ((!(ent instanceof Player)) || 
                    (this.Manager.GetGame().IsAlive((Player)ent)))
                  {

                    LivingEntity livingEnt = (LivingEntity)ent;
                    

                    this.Manager.GetDamage().NewDamageEvent(livingEnt, data.Player, null, 
                      EntityDamageEvent.DamageCause.PROJECTILE, 8.0D, false, true, false, 
                      data.Player.getName(), GetName());
                    
                    mineplex.core.common.util.UtilAction.velocity(livingEnt, UtilAlg.getTrajectory2d(data.Chicken, livingEnt), 1.6D, true, 0.8D, 0.0D, 10.0D, true);
                  } } }
            }
          }
          data.Chicken.getWorld().createExplosion(data.Chicken.getLocation(), 1.8F);
          

          FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(org.bukkit.Color.WHITE).with(FireworkEffect.Type.BALL).trail(false).build();
          
          try
          {
            this.Manager.GetFirework().playFirework(data.Chicken.getLocation().add(0.0D, 0.6D, 0.0D), effect);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
          
          data.Chicken.remove();
          dataIterator.remove();
        }
      }
    }
  }
}
