package nautilus.game.arcade.game.games.gravity.objects;

import java.util.Collection;
import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.gravity.Gravity;
import nautilus.game.arcade.game.games.gravity.GravityObject;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

public class GravityPlayer extends GravityObject
{
  public GravityPlayer(Gravity host, Entity ent, double mass, Vector vel)
  {
    super(host, ent, mass, 2.0D, vel);
    SetMovingBat(false);
  }
  

  public void PlayCollideSound(double power)
  {
    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.HURT_FLESH, 0.8F, 0.75F);
  }
  
  public boolean NearBlock()
  {
    return !NearBlockList().isEmpty();
  }
  
  public Collection<Block> NearBlockList()
  {
    HashSet<Block> blocks = new HashSet();
    
    for (Block block : UtilBlock.getSurrounding(this.Base.getLocation().getBlock(), true))
    {
      if (!UtilBlock.airFoliage(block))
      {

        blocks.add(block);
      }
    }
    for (Block block : UtilBlock.getSurrounding(this.Base.getLocation().getBlock().getRelative(BlockFace.UP), true))
    {
      if (!UtilBlock.airFoliage(block))
      {

        blocks.add(block);
      }
    }
    for (Block block : UtilBlock.getSurrounding(this.Base.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP), true))
    {
      if (!UtilBlock.airFoliage(block))
      {

        blocks.add(block);
      }
    }
    return blocks;
  }
  
  public void AutoGrab()
  {
    if (this.Vel.length() == 0.0D) {
      return;
    }
    if (!mineplex.core.common.util.UtilTime.elapsed(this.GrabDelay, 1000L)) {
      return;
    }
    if (!NearBlock()) {
      return;
    }
    this.Vel.multiply(0);
    this.Base.setVelocity(new Vector(0, 0, 0));
    
    this.GrabDelay = System.currentTimeMillis();
    

    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.STEP_STONE, 2.0F, 0.5F);
    

    this.Ent.getWorld().playEffect(this.Ent.getLocation(), Effect.STEP_SOUND, 1);
    

    SetMovingBat(false);
  }
  


  public void KickOff(Player player)
  {
    this.GrabDelay = System.currentTimeMillis();
    
    AddVelocity(player.getLocation().getDirection().multiply(0.5D), 0.5D);
    

    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.STEP_WOOD, 2.0F, 0.5F);
    

    this.Ent.getWorld().playEffect(this.Ent.getLocation(), Effect.STEP_SOUND, 1);
  }
  


  public void Jetpack()
  {
    if (!this.Ent.isValid()) {
      return;
    }
    if (!(this.Ent instanceof Player)) {
      return;
    }
    Player player = (Player)this.Ent;
    
    if (!player.isBlocking()) {
      return;
    }
    if (player.getExp() <= 0.0F)
    {
      if (Recharge.Instance.use(player, "Fuel", 1000L, false, false))
        UtilPlayer.message(this.Ent, F.main("Skill", "You have no more " + F.skill("Jetpack Fuel") + "."));
      return;
    }
    
    player.setExp((float)Math.max(0.0D, player.getExp() - 0.004D));
    
    AddVelocity(player.getLocation().getDirection().multiply(0.025D), 0.5D);
    

    this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.GHAST_FIREBALL, 0.3F, 2.0F);
    

    UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, this.Ent.getLocation().add(0.0D, 0.5D, 0.0D), 0.1F, 0.1F, 0.1F, 0.0F, 2);
  }
  
  public void Oxygen()
  {
    boolean near = false;
    for (Block block : UtilBlock.getInRadius(this.Ent.getLocation(), 4.0D).keySet())
    {
      if (block.getType() == Material.EMERALD_BLOCK)
      {
        near = true;
        break;
      }
    }
    

    if (near)
    {
      UtilPlayer.message(this.Ent, F.main("Game", "Restoring Oxygen..."));
      this.Ent.getWorld().playSound(this.Ent.getLocation(), Sound.CAT_HISS, 0.2F, 0.5F);
      
      this.Base.setHealth(Math.min(60.0D, this.Base.getHealth() + 10.0D));

    }
    else
    {
      this.Base.setHealth(Math.max(1.0D, this.Base.getHealth() - 1.0D));
    }
    

    if (this.Base.getHealth() <= 1.0D)
    {
      this.Host.Manager.GetDamage().NewDamageEvent((Player)this.Ent, null, null, 
        org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 2.0D, false, true, false, 
        "Oxygen Depleted", "Oxygen Depleted");
      
      UtilPlayer.message(this.Ent, F.main("Game", "You are suffocating!"));
      UtilPlayer.message(this.Ent, F.main("Game", "Get more Oxygen from the Emerald Blocks!"));
    }
  }
  

  public void CustomCollide(GravityObject other)
  {
    UtilPlayer.health((Player)this.Ent, -1.0D);
  }
}
