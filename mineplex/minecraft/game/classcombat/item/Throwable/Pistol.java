package mineplex.minecraft.game.classcombat.item.Throwable;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;







public class Pistol
  extends ItemUsable
{
  public Pistol(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Pistol", new String[] { "Pew pew pew." }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
  }
  

  public void UseAction(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    

    if (!UtilInv.remove(player, Material.MELON_SEEDS, (byte)0, 1))
    {
      UtilPlayer.message(player, F.main("Skill", "You need " + F.item("Pistol Ammo") + " to use " + F.skill(GetName()) + "."));
      return;
    }
    
    RifleShoot(player);
  }
  

  public void RifleShoot(Player player)
  {
    double sharpness = 0.1D;
    
    double travel = 0.0D;
    double maxTravel = 100.0D;
    
    double hitBox = 0.5D;
    

    player.getWorld().playEffect(player.getEyeLocation().add(player.getLocation().getDirection()), Effect.SMOKE, 4);
    player.getWorld().playSound(player.getEyeLocation(), Sound.EXPLODE, 0.6F, 2.0F);
    
    while (travel < maxTravel)
    {
      Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(travel));
      for (Entity ent : player.getWorld().getEntities())
      {
        if ((ent instanceof LivingEntity))
        {

          LivingEntity cur = (LivingEntity)ent;
          
          if (!cur.equals(player))
          {


            if ((cur instanceof Player))
            {
              if (UtilEnt.hitBox(loc, (Player)cur, 1.0D, EntityType.PLAYER))
              {
                RifleHit(cur, player, true);
                player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
              }
              

            }
            else
            {
              if (UtilMath.offset(loc, cur.getEyeLocation()) < 0.3D)
              {
                RifleHit(cur, player, true);
                player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                return;
              }
              if (UtilMath.offset2d(loc, cur.getLocation()) < hitBox)
              {
                if ((loc.getY() > cur.getLocation().getY()) && (loc.getY() < cur.getLocation().getY() + 1.0D))
                {
                  RifleHit(cur, player, false);
                  player.getWorld().playSound(loc, Sound.BLAZE_HIT, 0.4F, 2.0F);
                  return;
                } }
            }
          }
        }
      }
      if (UtilBlock.solid(loc.getBlock()))
      {
        loc.getBlock().getWorld().playEffect(loc, Effect.STEP_SOUND, loc.getBlock().getTypeId());
        player.getWorld().playSound(player.getLocation(), Sound.BLAZE_HIT, 0.4F, 2.0F);
        return;
      }
      
      travel += sharpness;
    }
  }
  
  public void RifleHit(LivingEntity hit, Player attacker, boolean headshot)
  {
    int damage = 12;
    if (headshot)
    {
      if (!(hit instanceof Player))
      {

































        hit.getWorld().playEffect(hit.getEyeLocation(), Effect.STEP_SOUND, 55);
        damage = 500;
      }
    }
    
    if (damage > 1) {}
  }
  
  public void Collide(LivingEntity target, Block block, ProjectileUser data) {}
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
